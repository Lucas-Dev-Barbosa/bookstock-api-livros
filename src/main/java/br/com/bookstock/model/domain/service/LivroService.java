package br.com.bookstock.model.domain.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import br.com.bookstock.exception.EstoqueException;
import br.com.bookstock.model.domain.Livro;
import br.com.bookstock.model.domain.repository.LivroRepository;
import lombok.extern.java.Log;

@Log
@Service
public class LivroService {

	@Autowired
	private LivroRepository repository;

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
	public Livro salvarLivro(Livro livro) throws EstoqueException {
		if (livro != null)
			log.info("Cadastrando livro [" + livro.getTitulo() + "] no estoque");

		Livro newLivro = repository.save(livro);
		
		Map<String, Long> livroMap = new HashMap<>();
		livroMap.put("id-livro", newLivro.getId());
		
		ResponseEntity<String> estoqueEntity = null;
		try {
			estoqueEntity = restTemplate.postForEntity(PATH_API_ESTOQUE + "/livro", livroMap, String.class);
		} catch (RestClientException e) {
			processaErroGeracaoEstoqueClient(livro.getId(), e.getMessage());
		}

		if (estoqueEntity.getStatusCodeValue() != 201)
			processaErroGeracaoEstoqueClient(livro.getId(), "Codigo do status retornado[" + estoqueEntity.getStatusCodeValue() + "]");

		String estoque = estoqueEntity.getBody();
		log.info("Estoque gerado para o livro:");
		log.info(estoque);
		
		return newLivro;
	}

	private void processaErroGeracaoEstoqueClient(Long id, String message) throws EstoqueException {
		String erro = "Erro no processo de geração do estoque, excluindo livro da base.";
		log.info(erro);
		log.info(message);
		repository.deleteById(id);
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
		log.info("Excluindo livro ID [" + id + "] da base");
		Livro livro = repository.findById(id).orElseThrow(() -> new EntityNotFoundException());
		
		try {
			restTemplate.delete(PATH_API_ESTOQUE + "/" + livro.getId());
			repository.deleteById(livro.getId());
		} catch (Exception e) {
			String erro = "Erro no processo de exclusão do título.";
			log.info(erro);
			throw new EstoqueException(erro);
		}
	}

}
