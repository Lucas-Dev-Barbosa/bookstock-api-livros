package br.com.bookstock.model.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.bookstock.model.domain.Livro;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

	@Query("FROM Livro l WHERE LOWER(l.titulo) like %:busca% OR LOWER(l.autor) like %:busca%")
	Page<Livro> getLivrosPorPaginacao(String busca, Pageable pageable);
	
	@Query("FROM Livro l")
	Page<Livro> getLivrosPorPaginacaoSemBusca(Pageable pageable);
	
}
