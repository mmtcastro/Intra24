package br.com.tdec.intra.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class AuthenticationSuccessHandler
		implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {
	// @Autowired
	// private LdapConfig ldapConfig;
	private static final Logger logger = LogManager.getLogger(AuthenticationSuccessHandler.class);

	public AuthenticationSuccessHandler() {
		logger.info("AuthenticationSuccessHandler.AuthenticationSuccessHandler()");
		String a = "";
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		logger.info("AuthenticationSuccessHandler.onAuthenticationSuccess()");
		String username = authentication.getName();

		// List<String> groups = ldapConfig.findGroupsForUser(username); // Fetch groups
		// from
		// LDAP
		// List<String> groups = new ArrayList<String>();
		// VaadinSession.getCurrent().setAttribute("groups", groups);

	}
}
