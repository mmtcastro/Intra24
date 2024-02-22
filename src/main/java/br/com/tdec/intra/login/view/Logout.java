package br.com.tdec.intra.login.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import br.com.tdec.intra.config.SecurityService;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;

@PageTitle("Logout")
@Route(value = "logout", layout = MainLayout.class)
@PermitAll
@AnonymousAllowed
public class Logout extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	private SecurityService securityService;

	public Logout(SecurityService securityService) {
		this.setSecurityService(securityService);
		securityService.logout();
	}

	public SecurityService getSecurityService() {
		return securityService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

}
