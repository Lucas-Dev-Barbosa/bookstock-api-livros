package br.com.bookstock.exception;

import org.springframework.http.HttpStatus;

public class EstoqueException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
	
	public EstoqueException(String message) {
		super(message);
	}

	public EstoqueException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}
	
	public HttpStatus getStatus() {
		return status;
	}
}
