package br.com.bookstock.model.domain;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.number.NumberStyleFormatter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "t_livro", uniqueConstraints = @UniqueConstraint(columnNames = { "isbn" }))
@SuppressWarnings("serial")
public class Livro extends AbstractEntity {

	@NotBlank(message = "Digite o título.")
	private String titulo;

	@NotBlank(message = "Digite a sinopse do livro.")
	@Column(columnDefinition = "TEXT")
	private String sinopse;

	@NotBlank(message = "Digite o autor.")
	private String autor;

	@NotBlank(message = "Digite o ISBN.")
	@Column(length = 20)
	private String isbn;

	@NotBlank(message = "Digite o nome da editora.")
	private String editora;

	@Column(columnDefinition = "LONGBLOB")
	@NotEmpty(message = "Insira a foto da capa do livro.")
	private byte[] fotoCapa;

	@Column(columnDefinition = "DECIMAL(19,2)")
	@NotNull(message = "Informe o preço.")
	private BigDecimal preco;

	@NotNull(message = "Informe a data de publicação.")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date dataPublicacao;

	@Min(value = 1, message = "O número de páginas tem que ser maior do que zero.")
	@NotNull(message = "Digite o número de páginas")
	private Integer numeroPaginas;

	public String getPreco() {
		if (this.preco != null)
			return getNumberFormat().format(this.preco);

		return null;
	}

	public void setPreco(String preco) throws ParseException {
		if (preco != null && !preco.isEmpty())
			this.preco = (BigDecimal) getNumberFormat().parse(preco);
	}

	@JsonIgnore
	private NumberFormat getNumberFormat() {
		return new NumberStyleFormatter("###,###.##").getNumberFormat(LocaleContextHolder.getLocale());
	}
	
	public void setTitulo(String titulo) throws ParseException {
		if (titulo != null && !titulo.isEmpty())
			this.titulo = titulo.trim();
	}
	
	public void setSinopse(String sinopse) throws ParseException {
		if (sinopse != null && !sinopse.isEmpty())
			this.sinopse = sinopse.trim();
	}
	
	public void setAutor(String autor) throws ParseException {
		if (autor != null && !autor.isEmpty())
			this.autor = autor.trim();
	}
	
	public void setIsbn(String isbn) throws ParseException {
		if (isbn!= null && !isbn.isEmpty())
			this.isbn = isbn.trim();
	}
	
	public void setEditora(String editora) throws ParseException {
		if (editora!= null && !editora.isEmpty())
			this.editora = editora.trim();
	}

}
