package br.edu.felipe.estoque.api;

import br.edu.felipe.estoque.api.Dados.*;
import br.edu.felipe.estoque.service.CatalogoService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CatalogoController {
  private final CatalogoService service;

  public CatalogoController(CatalogoService service) {
    this.service = service;
  }

  @GetMapping("/marcas")
  public List<MarcaSaida> marcas() {
    return service.marcas();
  }

  @PostMapping("/marcas")
  public ResponseEntity<MarcaSaida> criarMarca(@Valid @RequestBody MarcaEntrada d) {
    var m = service.salvarMarca(null, d);
    return ResponseEntity.created(URI.create("/api/marcas/" + m.id())).body(m);
  }

  @PutMapping("/marcas/{id}")
  public MarcaSaida editarMarca(@PathVariable Long id, @Valid @RequestBody MarcaEntrada d) {
    return service.salvarMarca(id, d);
  }

  @DeleteMapping("/marcas/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluirMarca(@PathVariable Long id) {
    service.excluirMarca(id);
  }

  @GetMapping("/modelos")
  public List<ModeloSaida> modelos(@RequestParam(required = false) Long marcaId) {
    return service.modelos(marcaId);
  }

  @PostMapping("/modelos")
  public ResponseEntity<ModeloSaida> criarModelo(@Valid @RequestBody ModeloEntrada d) {
    var m = service.salvarModelo(null, d);
    return ResponseEntity.created(URI.create("/api/modelos/" + m.id())).body(m);
  }

  @PutMapping("/modelos/{id}")
  public ModeloSaida editarModelo(@PathVariable Long id, @Valid @RequestBody ModeloEntrada d) {
    return service.salvarModelo(id, d);
  }

  @DeleteMapping("/modelos/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluirModelo(@PathVariable Long id) {
    service.excluirModelo(id);
  }
}
