package br.edu.felipe.estoque.service;

import org.springframework.http.HttpStatus;

public class RegraException extends RuntimeException {
  private final HttpStatus status;

  public RegraException(HttpStatus status, String mensagem) {
    super(mensagem);
    this.status = status;
  }

  public HttpStatus getStatus() {
    return status;
  }
}
