package br.com.bookstock.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import br.com.bookstock.converter.KeycloakJwtAuthenticationConverter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ServerConfig {

	@Autowired
	private KeycloakJwtAuthenticationConverter authenticationConverter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable()
				.authorizeRequests(auth -> auth.antMatchers("/info", "/actuator/**").permitAll())
				.authorizeRequests(auth -> auth.antMatchers(
						"/livros/**").authenticated()).oauth2ResourceServer().jwt()
				.jwtAuthenticationConverter(authenticationConverter);

		return http.build();
	}

}
