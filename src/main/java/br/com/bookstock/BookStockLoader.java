package br.com.bookstock;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.java.Log;

@Log
@Component
@Order(1)
public class BookStockLoader implements ApplicationRunner {

	@Override
	public void run(ApplicationArguments args) throws Exception {
		
		log.info("Iniciando a API de livros Book Stock Manager...");
		
	}

}
