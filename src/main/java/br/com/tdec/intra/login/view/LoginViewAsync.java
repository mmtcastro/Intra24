package br.com.tdec.intra.login.view;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import br.com.tdec.intra.config.LdapConfig;
import br.com.tdec.intra.config.WebClientProperties;
import br.com.tdec.intra.config.WebClientService;
import br.com.tdec.intra.directory.model.TokenData;
import br.com.tdec.intra.directory.model.User;
import br.com.tdec.intra.utils.SecurityUtils;
import br.com.tdec.intra.utils.UtilsAuthentication;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

@Route("login")
@PageTitle("Login | Intra")
@AnonymousAllowed
@Getter
@Setter
public class LoginViewAsync extends VerticalLayout implements BeforeEnterObserver {

	private static final long serialVersionUID = 1L;
	private final LdapConfig ldapConfig;
	private final LoginForm login = new LoginForm();
	private static final String LOGIN_SUCCESS_URL = "/";
	private final WebClientProperties webClientProperties;
	private final WebClientService webClientService;
	private final WebClient webClient;
	private boolean authenticated = false;
	private User user;

	public LoginViewAsync(WebClientService webClientService, LdapConfig ldapConfig,
			WebClientProperties webClientProperties) {
		System.out.println("Iniciando autenticacao");
		this.webClientService = webClientService;
		this.webClient = webClientService.getWebClient();
		this.webClientProperties = webClientProperties;
		this.ldapConfig = ldapConfig;

		addClassName("login-view");
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);

		// 🔴 Remova esta linha!
		// login.setAction("login");

		add(new H1("Intranet TDec"), login);

//		login.addLoginListener(e -> {
//			authenticated = SecurityUtils.authenticate(e.getUsername(), e.getPassword());
//
//			if (authenticated) {
//				try {
//					user = authenticateAndFetchUser(e.getUsername(), e.getPassword());
//					VaadinSession.getCurrent().setAttribute("user", user);
//					setRolesInAuthority(user);
//					UI.getCurrent().getPage().setLocation(LOGIN_SUCCESS_URL);
//				} catch (Exception ex) {
//					ex.printStackTrace();
//					login.setError(true);
//				}
//			} else {
//				System.out.println("Login Form - Erro ao autenticar");
//				login.setError(true);
//			}
//		});

		login.addLoginListener(e -> {
			VaadinSession session = VaadinSession.getCurrent();
			User sessionUser = session.getAttribute(User.class);

			// Se o usuário já estiver autenticado, apenas redireciona para a página
			// principal
			if (sessionUser != null) {
				System.out.println("Usuário já autenticado: " + sessionUser.getUsername());
				UI.getCurrent().access(() -> UI.getCurrent().getPage().setLocation(LOGIN_SUCCESS_URL));
				return;
			}

			// 🔴 Remover qualquer sessão anterior antes de autenticar novamente
			session.setAttribute("user", null);
			session.close();
			VaadinSession.getCurrent().getSession().invalidate();
			VaadinSession.getCurrent().setAttribute(User.class, null);

			System.out.println("Tentando autenticar usuário: " + e.getUsername());
			authenticated = SecurityUtils.authenticate(e.getUsername(), e.getPassword());

			if (authenticated) {
				try {
					user = authenticateAndFetchUser(e.getUsername(), e.getPassword());
					session = VaadinSession.getCurrent();
					session.setAttribute("user", user); // Salva na sessão
					session.setAttribute(User.class, user);
					setRolesInAuthority(user);

					System.out.println("Usuário autenticado com sucesso: " + user.getUsername());

					// Redirecionamento para a home após autenticação bem-sucedida
					UI.getCurrent().access(() -> UI.getCurrent().getPage().setLocation(LOGIN_SUCCESS_URL));
				} catch (Exception ex) {
					ex.printStackTrace();
					System.out.println("Erro ao autenticar o usuário: " + e.getUsername());
					login.setError(true);
				}
			} else {
				System.out.println("Login Form - Erro ao autenticar usuário: " + e.getUsername());
				login.setError(true);
			}
		});

	}

	private User authenticateAndFetchUser(String username, String pw) throws JsonProcessingException {
		User user = new User();
		user.setUsername(username);
		Map<String, String> credentials = new HashMap<>();
		credentials.put("username", user.getUsername());
		credentials.put("password", pw);

		Mono<String> tokenResponse = webClient.post().uri("/auth").contentType(MediaType.APPLICATION_JSON)
				.bodyValue(credentials).retrieve().bodyToMono(String.class);
		String jsonString = tokenResponse.block();
		ObjectMapper mapper = new ObjectMapper();
		TokenData tokenData = mapper.readValue(jsonString, TokenData.class);
		user.setTokenData(tokenData);
		user.setToken(tokenData.getBearer());

		User tempUser = webClient.get().uri("/userinfo").header("Authorization", "Bearer " + user.getToken()).retrieve()
				.bodyToMono(User.class).block();
		user.addAddicionalUserInformation(tempUser);

		System.out.println("authenticateAndFetchUser - Fim autenticação " + tokenData);
		return user;
	}

	private void setRolesInAuthority(User user) {
		for (String role : user.getRoles()) {
			UtilsAuthentication.setRole(user.getUsername(), role);
		}
		UtilsAuthentication.setRole(user.getUsername(), "USER");
	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		// User user = (User) VaadinSession.getCurrent().getAttribute("user");
//		if (user == null) {
//			System.out.println("User is null in beforeEnter");
//		} else {
//			System.out.println("User is not null in beforeEnter");
//		}

		if (beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error")) {
			login.setError(true);
		}
	}

}
