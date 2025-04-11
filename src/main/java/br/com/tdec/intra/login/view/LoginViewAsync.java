package br.com.tdec.intra.login.view;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
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
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import br.com.tdec.intra.config.LdapConfig;
import br.com.tdec.intra.config.WebClientProperties;
import br.com.tdec.intra.config.WebClientService;
import br.com.tdec.intra.directory.model.IntraUserDetails;
import br.com.tdec.intra.directory.model.TokenData;
import br.com.tdec.intra.directory.model.User;
import br.com.tdec.intra.utils.SecurityUtils;
import br.com.tdec.intra.utils.UtilsAuthentication;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;

@Route("login")
@PageTitle("Login | Intra")
@AnonymousAllowed // vaaadin opcional
@PermitAll
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
		System.out.println("🔧 LoginViewAsync iniciado");
		this.webClientService = webClientService;
		this.webClient = webClientService.getWebClient();
		this.webClientProperties = webClientProperties;
		this.ldapConfig = ldapConfig;

		addClassName("login-view");
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);

		add(new H1("Intranet TDec"), login);

		login.addLoginListener(e -> {
			String username = e.getUsername();
			String password = e.getPassword();

			System.out.println("🔐 Tentando autenticar usuário: " + username);
			VaadinSession session = VaadinSession.getCurrent();
			User sessionUser = session.getAttribute(User.class);

			if (sessionUser != null) {
				System.out.println("⚠️ Usuário já autenticado: " + sessionUser.getUsername());
				UI.getCurrent().getPage().executeJs("window.location.href = '/'");
				return;
			}

			// Limpa a sessão Vaadin
			session.setAttribute("user", null);
			session.setAttribute(User.class, null);

			authenticated = SecurityUtils.authenticate(username, password);

			if (authenticated) {
				try {
					user = authenticateAndFetchUser(username, password);
					System.out.println("✅ Token recebido: " + user.getToken());
					System.out.println("🔎 Roles atribuídas: " + user.getRoles());

					// Cria UserDetails e Authentication
					IntraUserDetails userDetails = new IntraUserDetails(user);
					Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
							userDetails.getAuthorities());

					// Registra no SecurityContext
					SecurityContext context = SecurityContextHolder.createEmptyContext();
					context.setAuthentication(authToken);

					// Persiste o contexto na sessão HTTP real
					HttpServletRequest request = ((VaadinServletRequest) VaadinService.getCurrentRequest())
							.getHttpServletRequest();
					HttpSession httpSession = request.getSession();
					httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

					// Sincroniza com VaadinSession
					VaadinSession vaadinSession = VaadinSession.getCurrent();
					vaadinSession.getSession().setAttribute(HttpSession.class.getName(), httpSession);

					System.out.println("🧠 SecurityContext armazenado na sessão: " + httpSession.getId());
					System.out.println("👤 Auth principal: " + authToken.getPrincipal());
					System.out.println("🍪 Cookie JSESSIONID (login): " + request.getRequestedSessionId());

					// Também guarda na VaadinSession (opcional)
					session.setAttribute("user", user);
					session.setAttribute(User.class, user);

					setRolesInAuthority(user);

					// Redireciona após login
					System.out.println("➡️ Redirecionando para: " + LOGIN_SUCCESS_URL);
					// UI.getCurrent().getPage().executeJs("window.location.href = '" +
					// LOGIN_SUCCESS_URL + "'");
					UI.getCurrent().access(() -> UI.getCurrent().navigate(""));

				} catch (Exception ex) {
					ex.printStackTrace();
					System.out.println("❌ Erro ao autenticar o usuário: " + username);
					login.setError(true);
				}
			} else {
				System.out.println("❌ Credenciais inválidas para: " + username);
				login.setError(true);
			}
		});
	}

	private User authenticateAndFetchUser(String username, String pw) throws JsonProcessingException {
		System.out.println("🌐 Iniciando chamada ao Domino RestAPI...");
		User user = new User();
		user.setUsername(username);

		Map<String, String> credentials = new HashMap<>();
		credentials.put("username", username);
		credentials.put("password", pw);

		String jsonString = webClient.post().uri("/auth").contentType(MediaType.APPLICATION_JSON).bodyValue(credentials)
				.retrieve().bodyToMono(String.class).block();

		ObjectMapper mapper = new ObjectMapper();
		TokenData tokenData = mapper.readValue(jsonString, TokenData.class);
		user.setTokenData(tokenData);
		user.setToken(tokenData.getBearer());

		User tempUser = webClient.get().uri("/userinfo").header("Authorization", "Bearer " + user.getToken()).retrieve()
				.bodyToMono(User.class).block();

		user.addAddicionalUserInformation(tempUser);
		System.out.println("✅ Fim da autenticação - token e dados carregados");
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
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		HttpServletRequest request = ((VaadinServletRequest) VaadinService.getCurrentRequest()).getHttpServletRequest();
		System.out.println("🔁 [beforeEnter] Authentication: " + auth);
		System.out.println("🍪 Cookie JSESSIONID (beforeEnter): " + request.getRequestedSessionId());

		if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
			System.out.println("🔁 [beforeEnter] Usuário já autenticado, redirecionando");
			beforeEnterEvent.forwardTo("");
		}

		if (beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error")) {
			login.setError(true);
		}
	}
}
