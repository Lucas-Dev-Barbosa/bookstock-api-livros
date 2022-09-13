package br.com.bookstock.converter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public AbstractAuthenticationToken convert(Jwt jwt) {
		JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

		Collection<GrantedAuthority> authorities = Stream
				.concat(grantedAuthoritiesConverter.convert(jwt).stream(), extractAuthorities(jwt).stream())
				.collect(Collectors.toSet());

		return new JwtAuthenticationToken(jwt, authorities);
	}

	private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
		Set<String> rolesWithPrefix = new HashSet<>();
		rolesWithPrefix.addAll(getRealmRoles(jwt));
		rolesWithPrefix.addAll(getResourceRoles(jwt));

		return AuthorityUtils.createAuthorityList(rolesWithPrefix.toArray(new String[0]));
	}

	private Collection<? extends String> getResourceRoles(Jwt jwt) {
		Set<String> rolesWithPrefix = new HashSet<>();
		Map<String, JsonNode> map = objectMapper.convertValue(jwt.getClaim("resource_access"),
				new TypeReference<Map<String, JsonNode>>() {
				});

		for (Map.Entry<String, JsonNode> jsonNode : map.entrySet()) {
			JsonNode jsonRoles = jsonNode.getValue().get("roles");

			jsonRoles.elements().forEachRemaining(r -> rolesWithPrefix.add(createRole(r.asText())));
		}

		return rolesWithPrefix;
	}

	private Collection<? extends String> getRealmRoles(Jwt jwt) {
		Set<String> rolesWithPrefix = new HashSet<>();
		JsonNode json = objectMapper.convertValue(jwt.getClaim("realm_access"), JsonNode.class);
		JsonNode jsonRoles = json.get("roles");
		jsonRoles.elements().forEachRemaining(r -> rolesWithPrefix.add(createRole(r.asText())));

		return rolesWithPrefix;
	}

	private String createRole(String value) {
		StringBuilder role = new StringBuilder();

		if (!value.startsWith("ROLE"))
			role.append("ROLE_");

		role.append(value.toUpperCase());

		return role.toString();
	}

}
