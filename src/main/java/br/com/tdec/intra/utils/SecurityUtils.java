package br.com.tdec.intra.utils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinSession;

import jakarta.servlet.ServletException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecurityUtils {

	private br.com.tdec.intra.config.SecurityService securityService;
	private static final String LOGOUT_SUCCESS_URL = "/";

	public SecurityUtils(br.com.tdec.intra.config.SecurityService securityService) {
		this.securityService = securityService;
	}

	public static boolean isAuthenticated() {
		VaadinServletRequest request = VaadinServletRequest.getCurrent();
		return request != null && request.getUserPrincipal() != null;
	}

	public static boolean authenticate(String username, String password) {
		VaadinServletRequest request = VaadinServletRequest.getCurrent();
		if (request == null) {
			System.out.println("Erro: Não é possível obter a requisição atual.");
			return false;
		}
		try {
			System.out.println("🔐 Tentando autenticar usuário: " + username);
			request.login(username, password);

			// ❌ Remova essa linha abaixo para manter a sessão ativa corretamente
			// request.getHttpServletRequest().changeSessionId();

			System.out.println("✅ Autenticação bem-sucedida para o usuário: " + username);
			return true;
		} catch (ServletException e) {
			System.err.println("❌ Erro ao autenticar o usuário: " + username);
			System.err.println("📛 Mensagem: " + e.getMessage());
			return false;
		}
	}

	public static void logout() {
		UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
		VaadinSession.getCurrent().getSession().invalidate();
	}

//	public String getCurrentUser() {
//		return securityService.getAuthenticatedUser().getUsername();
//	}
}
