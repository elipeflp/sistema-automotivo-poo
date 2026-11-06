package br.edu.felipe.estoque.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "modelos", uniqueConstraints = @UniqueConstraint(columnNames = {"marca_id", "nome"}))
public class Modelo extends EntidadeBase {
  @Column(nullable = false, length = 80)
  private String nome;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "marca_id", nullable = false)
  private Marca marca;

  protected Modelo() {}

  public Modelo(String nome, Marca marca) {
    alterar(nome, marca);
  }

  public void alterar(String nome, Marca marca) {
    this.nome = nome.strip();
    this.marca = marca;
  }

  public String getNome() {
    return nome;
  }

  public Marca getMarca() {
    return marca;
  }
}
