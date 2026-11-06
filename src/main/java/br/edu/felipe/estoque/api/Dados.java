package br.edu.felipe.estoque.api;

import br.edu.felipe.estoque.domain.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/** Records separam os contratos HTTP das entidades JPA. */
public final class Dados {
  private Dados() {}

  public record MarcaEntrada(@NotBlank @Size(max = 80) String nome) {}

  public record ModeloEntrada(
      @NotBlank @Size(max = 80) String nome, @NotNull @Positive Long marcaId) {}

  public record VeiculoEntrada(
      @NotNull @Positive Long modeloId,
      @NotNull @Min(1886) Integer ano,
      @NotBlank @Size(max = 40) String cor,
      @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2) BigDecimal preco,
      @NotNull @Min(0) Long quilometragem,
      @NotNull StatusVeiculo status,
      @NotNull TipoVeiculo tipo) {}

  public record MarcaSaida(Long id, String nome) {
    public static MarcaSaida de(Marca m) {
      return new MarcaSaida(m.getId(), m.getNome());
    }
  }

  public record ModeloSaida(Long id, String nome, Long marcaId, String marca) {
    public static ModeloSaida de(Modelo m) {
      return new ModeloSaida(m.getId(), m.getNome(), m.getMarca().getId(), m.getMarca().getNome());
    }
  }

  public record VeiculoSaida(
      Long id,
      Long marcaId,
      String marca,
      Long modeloId,
      String modelo,
      Integer ano,
      String cor,
      BigDecimal preco,
      Long quilometragem,
      StatusVeiculo status,
      TipoVeiculo tipo) {
    public static VeiculoSaida de(Veiculo v) {
      return new VeiculoSaida(
          v.getId(),
          v.getModelo().getMarca().getId(),
          v.getModelo().getMarca().getNome(),
          v.getModelo().getId(),
          v.getModelo().getNome(),
          v.getAno(),
          v.getCor(),
          v.getPreco(),
          v.getQuilometragem(),
          v.getStatus(),
          v.getTipo());
    }
  }
}
