package br.com.tdec.intra.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.vaadin.flow.spring.security.AuthenticationContext;

@Component
public class SecurityService {

	/**
	 * Aqui eu preciso para pegar o athentication context (nome do usuario, etc).
	 */

	private final AuthenticationContext authenticationContext;

	public SecurityService(AuthenticationContext autenticationContext) {
		this.authenticationContext = autenticationContext;
	}

	public UserDetails getAuthenticatedUser() {
		return authenticationContext.getAuthenticatedUser(UserDetails.class).get();
	}

	public void logout() {
		authenticationContext.logout();
	}
}
