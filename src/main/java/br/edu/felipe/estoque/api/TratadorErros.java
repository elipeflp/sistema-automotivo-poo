package br.edu.felipe.estoque.api;

import br.edu.felipe.estoque.service.RegraException;
import java.time.Instant;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class TratadorErros {
  public record Erro(Instant instante, int status, String mensagem, List<String> detalhes) {}

  private ResponseEntity<Erro> resposta(HttpStatus s, String m, List<String> d) {
    return ResponseEntity.status(s).body(new Erro(Instant.now(), s.value(), m, d));
  }

  @ExceptionHandler(RegraException.class)
  public ResponseEntity<Erro> regra(RegraException e) {
    return resposta(e.getStatus(), e.getMessage(), List.of());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Erro> validacao(MethodArgumentNotValidException e) {
    return resposta(
        HttpStatus.BAD_REQUEST,
        "Revise os campos informados.",
        e.getBindingResult().getFieldErrors().stream()
            .map(x -> x.getField() + ": " + x.getDefaultMessage())
            .toList());
  }

  @ExceptionHandler({
    MethodArgumentTypeMismatchException.class,
    HttpMessageNotReadableException.class
  })
  public ResponseEntity<Erro> formato(Exception e) {
    return resposta(
        HttpStatus.BAD_REQUEST,
        "Formato inválido. Confira números, identificadores e opções de status/tipo.",
        List.of());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Erro> integridade(DataIntegrityViolationException e) {
    return resposta(
        HttpStatus.CONFLICT,
        "Operação bloqueada por duplicidade ou vínculo entre registros.",
        List.of());
  }
}
