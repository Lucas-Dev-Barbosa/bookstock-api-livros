package br.com.bookstock.model.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "t_estoque")
@SuppressWarnings("serial")
public class Estoque extends AbstractEntity {

	@Column(columnDefinition = "int default 0")
	@Min(value = 0, message = "O valor do estoque não pode ser negativo.")
	private Integer emEstoque = 0;
	
	@Column(columnDefinition = "int default 0")
	@Min(value = 0, message = "O valor de vendidos não pode ser negativo.")
	private Integer vendidos = 0;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_livro")
	private Livro livro;
	
	public Estoque(Livro livro) {
		this.livro = livro;
	}
	
}
