package br.com.tdec.intra.login.view;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import br.com.tdec.intra.utils.SecurityUtils;

@Route("login")
@PageTitle("Login | Intra")
@AnonymousAllowed
public class LoginView extends VerticalLayout
		implements BeforeEnterObserver, ComponentEventListener<AbstractLogin.LoginEvent> {

	/**
	 * Meu login personalizado
	 */
	private static final long serialVersionUID = 1L;
	private final LoginForm login = new LoginForm();
	private static final String LOGIN_SUCCESS_URL = "/";

	public LoginView() {
		addClassName("login-view");
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);

		login.setAction("login");

		add(new H1("Intranet TDec"), login);

	}

//	@Override
//	public void beforeEnter(BeforeEnterEvent event) {
//		// TODO Auto-generated method stub
//
//	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		if (beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error")) {
			login.setError(true);
		}
	}

	@Override
	public void onComponentEvent(AbstractLogin.LoginEvent loginEvent) {
		System.out.println("LoginEvent - " + loginEvent.getUsername() + " - " + loginEvent.getPassword());
		boolean authenticated = SecurityUtils.authenticate(loginEvent.getUsername(), loginEvent.getPassword());
		if (authenticated) {
			UI.getCurrent().getPage().setLocation(LOGIN_SUCCESS_URL);
		} else {
			login.setError(true);
		}
	}

}
