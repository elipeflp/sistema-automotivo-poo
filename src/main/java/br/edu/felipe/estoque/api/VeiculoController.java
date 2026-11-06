package br.edu.felipe.estoque.api;

import br.edu.felipe.estoque.api.Dados.*;
import br.edu.felipe.estoque.domain.*;
import br.edu.felipe.estoque.service.VeiculoService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {
  private final VeiculoService service;

  public VeiculoController(VeiculoService service) {
    this.service = service;
  }

  @GetMapping
  public List<VeiculoSaida> listar(
      @RequestParam(required = false) Long marcaId,
      @RequestParam(required = false) Long modeloId,
      @RequestParam(required = false) BigDecimal precoMin,
      @RequestParam(required = false) BigDecimal precoMax,
      @RequestParam(required = false) Integer anoMin,
      @RequestParam(required = false) Integer anoMax,
      @RequestParam(required = false) StatusVeiculo status,
      @RequestParam(required = false) TipoVeiculo tipo) {
    return service.filtrar(marcaId, modeloId, precoMin, precoMax, anoMin, anoMax, status, tipo);
  }

  @GetMapping("/{id}")
  public VeiculoSaida obter(@PathVariable Long id) {
    return service.obter(id);
  }

  @PostMapping
  public ResponseEntity<VeiculoSaida> criar(@Valid @RequestBody VeiculoEntrada d) {
    var v = service.salvar(null, d);
    return ResponseEntity.created(URI.create("/api/veiculos/" + v.id())).body(v);
  }

  @PutMapping("/{id}")
  public VeiculoSaida atualizar(@PathVariable Long id, @Valid @RequestBody VeiculoEntrada d) {
    return service.salvar(id, d);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluir(@PathVariable Long id) {
    service.excluir(id);
  }
}
