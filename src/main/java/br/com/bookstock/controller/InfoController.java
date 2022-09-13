package br.com.bookstock.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("info")
@Tag(name = "Info Endpoint")
public class InfoController {

	@GetMapping
	public Map<String, Object> info() {
		Map<String, Object> body = new LinkedHashMap<>();

		body.put("api_info", "API criada para gerenciamento do cadastro de livros do sistema BOOK STOCK Manager");
		body.put("timestamp", new SimpleDateFormat("dd/MM/YYYY HH:mm:ss").format(new Date()));
		body.put("status", "up");

		return body;
	}

}
