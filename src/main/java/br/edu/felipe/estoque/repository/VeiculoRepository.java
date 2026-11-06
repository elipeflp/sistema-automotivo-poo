package br.edu.felipe.estoque.repository;

import br.edu.felipe.estoque.domain.Veiculo;
import org.springframework.data.jpa.repository.*;

public interface VeiculoRepository
    extends JpaRepository<Veiculo, Long>, JpaSpecificationExecutor<Veiculo> {
  boolean existsByModeloId(Long modeloId);
}
