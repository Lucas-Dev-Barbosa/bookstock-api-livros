package br.com.bookstock.exceptionhandler;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.com.bookstock.exception.EstoqueException;

@RestControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		List<String> messages = ex.getBindingResult().getFieldErrors().stream().map(x -> x.getDefaultMessage())
				.collect(Collectors.toList());

		Map<String, Object> body = getBody(status.value(), status.getReasonPhrase(), messages);

		return new ResponseEntity<Object>(body, headers, status);
	}

	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	public ResponseEntity<Object> constraintViolationException(SQLIntegrityConstraintViolationException ex)
			throws IOException {
		return getResponseEntity(HttpStatus.BAD_REQUEST, "Violação de integridade");
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<Object> entityNotFoundException(EntityNotFoundException ex) throws IOException {
		return getResponseEntity(HttpStatus.BAD_REQUEST, "Não foi possível encontrar este registro em nossa base");
	}
	
	@ExceptionHandler(EmptyResultDataAccessException.class)
	public ResponseEntity<Object> emptyResultDataAccessException(EmptyResultDataAccessException ex) throws IOException {
		return getResponseEntity(HttpStatus.BAD_REQUEST, "Não foi possível encontrar este registro em nossa base");
	}

	@ExceptionHandler(EstoqueException.class)
	public ResponseEntity<Object> estoqueException(EstoqueException ex) throws IOException {
		return getResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
	}

	private ResponseEntity<Object> getResponseEntity(HttpStatus httpStatus, String... messages) {
		Map<String, Object> body = getBody(httpStatus.value(), httpStatus.getReasonPhrase(),
				Arrays.asList(messages));

		return new ResponseEntity<Object>(body, httpStatus);
	}

	private Map<String, Object> getBody(int status, String error, List<String> message) {
		Map<String, Object> body = new LinkedHashMap<>();

		body.put("timestamp", new Date());
		body.put("status", status);
		body.put("error", error);
		body.put("message", message);

		return body;
	}

}
