package br.edu.felipe.estoque.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "marcas", uniqueConstraints = @UniqueConstraint(columnNames = "nome"))
public class Marca extends EntidadeBase {
  @Column(nullable = false, length = 80)
  private String nome;

  protected Marca() {}

  public Marca(String nome) {
    renomear(nome);
  }

  public void renomear(String nome) {
    this.nome = nome.strip();
  }

  public String getNome() {
    return nome;
  }
}
