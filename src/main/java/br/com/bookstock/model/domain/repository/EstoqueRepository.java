package br.com.bookstock.model.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.bookstock.model.domain.Estoque;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

	@Query("FROM Estoque e WHERE e.livro.id = :id")
	Estoque getEstoquePorLivroId(Long id);
	
}
