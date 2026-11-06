package br.edu.felipe.estoque.service;

import static org.springframework.http.HttpStatus.*;

import br.edu.felipe.estoque.api.Dados.*;
import br.edu.felipe.estoque.domain.*;
import br.edu.felipe.estoque.repository.*;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CatalogoService {
  private final MarcaRepository marcas;
  private final ModeloRepository modelos;
  private final VeiculoRepository veiculos;

  public CatalogoService(
      MarcaRepository marcas, ModeloRepository modelos, VeiculoRepository veiculos) {
    this.marcas = marcas;
    this.modelos = modelos;
    this.veiculos = veiculos;
  }

  public Marca buscarMarca(Long id) {
    return marcas
        .findById(id)
        .orElseThrow(() -> new RegraException(NOT_FOUND, "Marca não encontrada."));
  }

  public Modelo buscarModelo(Long id) {
    return modelos
        .findById(id)
        .orElseThrow(() -> new RegraException(NOT_FOUND, "Modelo não encontrado."));
  }

  @Transactional(readOnly = true)
  public List<MarcaSaida> marcas() {
    return marcas.findAll(Sort.by("nome")).stream().map(MarcaSaida::de).toList();
  }

  @Transactional(readOnly = true)
  public List<ModeloSaida> modelos(Long marcaId) {
    return modelos.findAll(Sort.by("nome")).stream()
        .filter(m -> marcaId == null || m.getMarca().getId().equals(marcaId))
        .map(ModeloSaida::de)
        .toList();
  }

  public MarcaSaida salvarMarca(Long id, MarcaEntrada d) {
    String nome = d.nome().strip();
    if (marcas.existsByNomeIgnoreCaseAndIdNot(nome, id == null ? 0L : id))
      throw new RegraException(CONFLICT, "Já existe uma marca com esse nome.");
    Marca m = id == null ? new Marca(nome) : buscarMarca(id);
    m.renomear(nome);
    return MarcaSaida.de(marcas.saveAndFlush(m));
  }

  public ModeloSaida salvarModelo(Long id, ModeloEntrada d) {
    Marca marca = buscarMarca(d.marcaId());
    String nome = d.nome().strip();
    if (modelos.existsByNomeIgnoreCaseAndMarcaIdAndIdNot(nome, marca.getId(), id == null ? 0L : id))
      throw new RegraException(CONFLICT, "Já existe esse modelo para a marca.");
    Modelo m = id == null ? new Modelo(nome, marca) : buscarModelo(id);
    m.alterar(nome, marca);
    return ModeloSaida.de(modelos.saveAndFlush(m));
  }

  public void excluirMarca(Long id) {
    Marca m = buscarMarca(id);
    if (modelos.existsByMarcaId(id))
      throw new RegraException(CONFLICT, "Remova os modelos vinculados antes de excluir a marca.");
    marcas.delete(m);
  }

  public void excluirModelo(Long id) {
    Modelo m = buscarModelo(id);
    if (veiculos.existsByModeloId(id))
      throw new RegraException(CONFLICT, "Há veículos vinculados a esse modelo.");
    modelos.delete(m);
  }
}
