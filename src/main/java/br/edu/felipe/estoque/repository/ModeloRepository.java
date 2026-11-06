package br.edu.felipe.estoque.repository;

import br.edu.felipe.estoque.domain.Modelo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModeloRepository extends JpaRepository<Modelo, Long> {
  boolean existsByMarcaId(Long marcaId);

  boolean existsByNomeIgnoreCaseAndMarcaIdAndIdNot(String nome, Long marcaId, Long id);
}
