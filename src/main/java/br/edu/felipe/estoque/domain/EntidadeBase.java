package br.edu.felipe.estoque.domain;

import jakarta.persistence.*;

/** Herança de identidade comum às entidades persistentes. */
@MappedSuperclass
public abstract class EntidadeBase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public Long getId() {
    return id;
  }
}
