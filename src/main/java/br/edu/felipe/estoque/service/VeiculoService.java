package br.edu.felipe.estoque.service;

import static org.springframework.http.HttpStatus.*;

import br.edu.felipe.estoque.api.Dados.*;
import br.edu.felipe.estoque.domain.*;
import br.edu.felipe.estoque.repository.VeiculoRepository;
import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VeiculoService {
  private final VeiculoRepository veiculos;
  private final CatalogoService catalogo;

  public VeiculoService(VeiculoRepository veiculos, CatalogoService catalogo) {
    this.veiculos = veiculos;
    this.catalogo = catalogo;
  }

  private Veiculo buscar(Long id) {
    return veiculos
        .findById(id)
        .orElseThrow(() -> new RegraException(NOT_FOUND, "Veículo não encontrado."));
  }

  @Transactional(readOnly = true)
  public VeiculoSaida obter(Long id) {
    return VeiculoSaida.de(buscar(id));
  }

  public VeiculoSaida salvar(Long id, VeiculoEntrada d) {
    if (d.ano() > Year.now().getValue() + 1)
      throw new RegraException(BAD_REQUEST, "Ano deve ser no máximo o próximo ano.");
    Modelo modelo = catalogo.buscarModelo(d.modeloId());
    Veiculo v =
        id == null
            ? new Veiculo(
                modelo, d.ano(), d.cor(), d.preco(), d.quilometragem(), d.status(), d.tipo())
            : buscar(id);
    v.atualizar(modelo, d.ano(), d.cor(), d.preco(), d.quilometragem(), d.status(), d.tipo());
    return VeiculoSaida.de(veiculos.saveAndFlush(v));
  }

  public void excluir(Long id) {
    Veiculo v = buscar(id);
    if (!v.podeSerRemovido())
      throw new RegraException(
          CONFLICT, "Apenas veículos vendidos ou descontinuados podem ser excluídos.");
    veiculos.delete(v);
  }

  @Transactional(readOnly = true)
  public List<VeiculoSaida> filtrar(
      Long marcaId,
      Long modeloId,
      BigDecimal precoMin,
      BigDecimal precoMax,
      Integer anoMin,
      Integer anoMax,
      StatusVeiculo status,
      TipoVeiculo tipo) {
    if ((precoMin != null && precoMin.signum() < 0) || (precoMax != null && precoMax.signum() < 0))
      throw new RegraException(BAD_REQUEST, "Preço de filtro não pode ser negativo.");
    if (precoMin != null && precoMax != null && precoMin.compareTo(precoMax) > 0)
      throw new RegraException(BAD_REQUEST, "Preço mínimo não pode superar o máximo.");
    if (anoMin != null && anoMax != null && anoMin > anoMax)
      throw new RegraException(BAD_REQUEST, "Ano inicial não pode superar o final.");
    Specification<Veiculo> spec = (root, q, cb) -> cb.conjunction();
    if (marcaId != null)
      spec = spec.and((r, q, c) -> c.equal(r.get("modelo").get("marca").get("id"), marcaId));
    if (modeloId != null)
      spec = spec.and((r, q, c) -> c.equal(r.get("modelo").get("id"), modeloId));
    if (precoMin != null)
      spec = spec.and((r, q, c) -> c.greaterThanOrEqualTo(r.get("preco"), precoMin));
    if (precoMax != null)
      spec = spec.and((r, q, c) -> c.lessThanOrEqualTo(r.get("preco"), precoMax));
    if (anoMin != null) spec = spec.and((r, q, c) -> c.greaterThanOrEqualTo(r.get("ano"), anoMin));
    if (anoMax != null) spec = spec.and((r, q, c) -> c.lessThanOrEqualTo(r.get("ano"), anoMax));
    if (status != null) spec = spec.and((r, q, c) -> c.equal(r.get("status"), status));
    if (tipo != null) spec = spec.and((r, q, c) -> c.equal(r.get("tipo"), tipo));
    return veiculos.findAll(spec, Sort.by(Sort.Direction.DESC, "id")).stream()
        .map(VeiculoSaida::de)
        .toList();
  }
}
