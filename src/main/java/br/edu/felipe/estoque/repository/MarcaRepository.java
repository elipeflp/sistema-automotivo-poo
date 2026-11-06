package br.edu.felipe.estoque.repository;

import br.edu.felipe.estoque.domain.Marca;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarcaRepository extends JpaRepository<Marca, Long> {
  boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
