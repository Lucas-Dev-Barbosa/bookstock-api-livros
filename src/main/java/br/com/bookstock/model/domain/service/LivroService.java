package br.com.bookstock.model.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import br.com.bookstock.exception.EstoqueException;
import br.com.bookstock.model.domain.Estoque;
import br.com.bookstock.model.domain.Livro;
import br.com.bookstock.model.domain.repository.EstoqueRepository;
import br.com.bookstock.model.domain.repository.LivroRepository;
import lombok.extern.java.Log;

@Log
@Service
public class LivroService {

	@Autowired
	private LivroRepository repository;

	@Autowired
	private EstoqueRepository estoqueRepository;

	@Autowired
	private RestTemplate restTemplate;

	private final String PATH_API_ESTOQUE = "http://ESTOQUE-SERVICE/bookstock/api/estoque/";

	public List<Livro> getListaLivros() {
		return repository.findAll();
	}

	public Page<Livro> buscarLivrosPorPaginacao(int paginaAtual, String ordem, String busca) {
		PageRequest pageRequest = PageRequest.of(paginaAtual - 1, 12, Sort.Direction.valueOf(ordem.toUpperCase()),
				"titulo");

		if (busca == null || busca.isEmpty())
			return repository.getLivrosPorPaginacaoSemBusca(pageRequest);

		return repository.getLivrosPorPaginacao(busca, pageRequest);
	}

	@Transactional(readOnly = false)
	public Estoque salvarLivro(Livro livro) throws EstoqueException {
		if (livro != null)
			log.info("Cadastrando livro [" + livro.getTitulo() + "] no estoque");

		ResponseEntity<Estoque> estoqueEntity = null;
		try {
			estoqueEntity = restTemplate.postForEntity(PATH_API_ESTOQUE, livro, Estoque.class);
		} catch (Exception e) {
			processaErroGeracaoEstoqueClient(e.getMessage());
		}

		if (estoqueEntity.getStatusCodeValue() != 201)
			processaErroGeracaoEstoqueClient("Codigo do status retornado[" + estoqueEntity.getStatusCodeValue() + "]");

		Estoque estoque = estoqueEntity.getBody();
		return estoque;
	}

	private void processaErroGeracaoEstoqueClient(String message) throws EstoqueException {
		String erro = "Erro no processo de geração do estoque.";
		log.info(erro);
		log.info(message);
		throw new EstoqueException(erro);
	}

	@Transactional(readOnly = false)
	public Livro editarLivro(Livro livro) {
		if (livro != null)
			log.info("Editando informacoes do livro [" + livro.getTitulo() + "] no estoque");

		return repository.save(livro);
	}

	public Livro obterLivroPorId(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Transactional(readOnly = false)
	public void excluirTitulo(Long id) throws EstoqueException {
		Estoque estoque = estoqueRepository.getEstoquePorLivroId(id);

		if(estoque == null)
			throw new EstoqueException("Este livro não consta em nossa base");
		
		try {
			restTemplate.delete(PATH_API_ESTOQUE + "/" + estoque.getId());
		} catch (Exception e) {
			String erro = "Erro no processo de exclusão do título.";
			log.info(erro);
			throw new EstoqueException(erro);
		}
	}

}
