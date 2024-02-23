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
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

	private static final long serialVersionUID = 1L;
	private final LdapConfig ldapConfig;
	private final LoginForm login = new LoginForm();
	private static final String LOGIN_SUCCESS_URL = "/";
	private final WebClientProperties webClientProperties;
	private final WebClientService webClientService;
	private final WebClient webClient;
	private boolean authenticated = false;
	// private ExecutorService executor = Executors.newSingleThreadExecutor();
	private User user;

	public LoginView(WebClientService webClientService, LdapConfig ldapConfig,
			WebClientProperties webClientProperties) {

		this.webClientService = webClientService;
		this.webClient = webClientService.getWebClient();
		this.webClientProperties = webClientProperties;
		this.ldapConfig = ldapConfig;

		addClassName("login-view");
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);

		login.setAction("login");

		add(new H1("Intranet TDec"), login);

		login.addLoginListener(e -> {

			authenticated = SecurityUtils.authenticate(e.getUsername(), e.getPassword());
			if (authenticated) {

				// VaadinSession.getCurrent().setAttribute("grupos",
				// ldapConfig.findGroupsForUser(e.getUsername()));

				setUserInVaadinSession(e.getUsername(), e.getPassword());
				UI.getCurrent().getPage().setLocation(LOGIN_SUCCESS_URL);

			} else {
				login.setError(true);
			}
		});

	}

	private void setUserInVaadinSession(String username, String pw) {

		user = new User();
		user.setUsername(username);
		Map<String, String> credentials = new HashMap<>();
		// credentials.put("username", webClientProperties.getUsername());
		// credentials.put("password", webClientProperties.getPassword());
		credentials.put("username", user.getUsername());
		credentials.put("password", pw);

		try {
			Mono<String> tokenResponse = webClient.post().uri("/auth").contentType(MediaType.APPLICATION_JSON)
					.bodyValue(credentials).retrieve().bodyToMono(String.class);
			String jsonString = tokenResponse.block();
			System.out.println(jsonString);
			ObjectMapper mapper = new ObjectMapper();
			TokenData tokenData;

			tokenData = mapper.readValue(jsonString, TokenData.class);
			user.setTokenData(tokenData);
			user.setToken(tokenData.getBearer());
			/*
			 * Buscando informacoes adicionais do usuario
			 * 
			 */
			User tempUser = webClient.get().uri("/userinfo").header("Authorization", "Bearer " + user.getToken())
					.retrieve().bodyToMono(User.class)//
					.block();
			user.addAddicionalUserInformation(tempUser);

			VaadinSession.getCurrent().setAttribute("user", user);
			setRolesInAuthority(user);

			System.out.println("setUserInVaadinSession - Fim autenticacao " + tokenData);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setRolesInAuthority(User user) {

		// List<String> grupos = (List<String>)
		// UI.getCurrent().getSession().getAttribute("grupos");
		for (String role : user.getRoles()) {
			UtilsAuthentication.setRole(user.getUsername(), role);
		}
		UtilsAuthentication.setRole(user.getUsername(), "USER");
	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

		if (beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error")) {
			login.setError(true);
		}
	}

}
