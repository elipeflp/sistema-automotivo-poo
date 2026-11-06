package br.edu.felipe.estoque.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
    name = "veiculos",
    indexes = {
      @Index(name = "idx_veiculo_status", columnList = "status"),
      @Index(name = "idx_veiculo_preco", columnList = "preco"),
      @Index(name = "idx_veiculo_ano", columnList = "ano")
    })
public class Veiculo extends EntidadeBase {
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "modelo_id", nullable = false)
  private Modelo modelo;

  @Column(nullable = false)
  private Integer ano;

  @Column(nullable = false, length = 40)
  private String cor;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal preco;

  @Column(nullable = false)
  private Long quilometragem;

  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private StatusVeiculo status;

  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TipoVeiculo tipo;

  protected Veiculo() {}

  public Veiculo(
      Modelo modelo,
      Integer ano,
      String cor,
      BigDecimal preco,
      Long km,
      StatusVeiculo status,
      TipoVeiculo tipo) {
    atualizar(modelo, ano, cor, preco, km, status, tipo);
  }

  public void atualizar(
      Modelo modelo,
      Integer ano,
      String cor,
      BigDecimal preco,
      Long km,
      StatusVeiculo status,
      TipoVeiculo tipo) {
    this.modelo = modelo;
    this.ano = ano;
    this.cor = cor.strip();
    this.preco = preco;
    this.quilometragem = km;
    this.status = status;
    this.tipo = tipo;
  }

  public boolean podeSerRemovido() {
    return status == StatusVeiculo.VENDIDO || status == StatusVeiculo.DESCONTINUADO;
  }

  public Modelo getModelo() {
    return modelo;
  }

  public Integer getAno() {
    return ano;
  }

  public String getCor() {
    return cor;
  }

  public BigDecimal getPreco() {
    return preco;
  }

  public Long getQuilometragem() {
    return quilometragem;
  }

  public StatusVeiculo getStatus() {
    return status;
  }

  public TipoVeiculo getTipo() {
    return tipo;
  }
}
