package br.com.bookstock.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.bookstock.exception.EstoqueException;
import br.com.bookstock.model.domain.Livro;
import br.com.bookstock.model.domain.service.LivroService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/")
@Tag(name = "Livro Endpoint")
public class LivroController {

	@Autowired
	private LivroService service;

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
	@GetMapping
	@Tag(name = "listarLivros", description = "Retorna uma lista com todos os livros no estoque")
	public List<Livro> listarLivros() {
		return service.getListaLivros();
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
	@GetMapping("/paginacao")
	@Tag(name = "listarLivrosPorPaginacao", description = "Retorna uma lista paginada com todos os livros no estoque")
	public Page<Livro> listarLivrosPorPaginacao(
			@RequestParam(name = "pagina", required = false, defaultValue = "1") Integer pagina,
			@RequestParam(name = "direcao", required = false, defaultValue = "asc") String direcao,
			@RequestParam(name = "filtro", required = false) String filtro) {

		Page<Livro> livros = service.buscarLivrosPorPaginacao(pagina, direcao, filtro);

		return livros;
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
	@GetMapping("/{id}")
	@Tag(name = "buscarLivroPorId", description = "Retorna um livro pelo seu id")
	public Livro buscarLivroPorId(@PathVariable Long id) {
		Livro livro = service.obterLivroPorId(id);
		return livro;
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	@Tag(name = "cadastrarLivro", description = "Realiza o cadastramento de um livro no estoque. Será criado um novo registro na base de estoque")
	public Livro cadastrarLivro(@Valid @RequestBody Livro livro) throws EstoqueException {
		Livro newLivro = service.salvarLivro(livro);
		return newLivro;
	}

	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@PutMapping
	@ResponseStatus(code = HttpStatus.OK)
	@Tag(name = "editarLivro", description = "Realiza alteração de alguma informação de um livro no estoque")
	public void editarLivro(@Valid @RequestBody Livro livro) {
		service.editarLivro(livro);
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@DeleteMapping("/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	@Tag(name = "excluirTitulo", description = "Realiza a exclusão de um livro no estoque")
	public void excluirTitulo(@PathVariable Long id) throws EstoqueException {
		service.excluirTitulo(id);
	}

}
