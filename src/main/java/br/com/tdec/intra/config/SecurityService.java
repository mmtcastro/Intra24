package br.com.tdec.intra.config;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.security.AuthenticationContext;

//🔐 SecurityService com melhorias para Vaadin + Spring Security
@Component
public class SecurityService {

	private final transient AuthenticationContext authenticationContext;

	public SecurityService(AuthenticationContext authenticationContext) {
		this.authenticationContext = authenticationContext;
	}

	// ✅ Retorna usuário autenticado como Optional
	public Optional<UserDetails> getAuthenticatedUser() {
		return authenticationContext.getAuthenticatedUser(UserDetails.class);
	}

	// ✅ Verifica se o usuário possui uma role específica
	public boolean hasRole(String role) {
		return authenticationContext.getAuthenticatedUser(UserDetails.class)
				.map(user -> user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase(role)))
				.orElse(false);
	}

	// ✅ Executa logout seguro e redireciona
	public void logout() {
		authenticationContext.logout();
		UI.getCurrent().getPage().setLocation("/login");
	}
}
