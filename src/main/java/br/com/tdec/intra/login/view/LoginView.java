package br.com.tdec.intra.login.view;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

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
import br.com.tdec.intra.directory.model.TokenData;
import br.com.tdec.intra.directory.model.User;
import br.com.tdec.intra.utils.SecurityUtils;
import br.com.tdec.intra.utils.UtilsAuthentication;
import lombok.Getter;
import lombok.Setter;

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
	private final RestTemplate restTemplate;
	private boolean authenticated = false;
	private User user;

	public LoginView(LdapConfig ldapConfig, WebClientProperties webClientProperties, RestTemplate restTemplate) {
		this.webClientProperties = webClientProperties;
		this.ldapConfig = ldapConfig;
		this.restTemplate = restTemplate;

		addClassName("login-view");
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);

		login.setAction("login");

		add(new H1("Intranet TDec"), login);

		login.addLoginListener(e -> {
			authenticated = SecurityUtils.authenticate(e.getUsername(), e.getPassword());
			if (authenticated) {
				try {
					user = authenticateAndFetchUser(e.getUsername(), e.getPassword());
					if (user != null) {
						VaadinSession.getCurrent().setAttribute("user", user);
						setRolesInAuthority(user);
						UI.getCurrent().getPage().setLocation(LOGIN_SUCCESS_URL);
					} else {
						login.setError(true);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					login.setError(true);
				}
			} else {
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

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Map<String, String>> entity = new HttpEntity<>(credentials, headers);

		ResponseEntity<String> tokenResponse = restTemplate.exchange(webClientProperties.getBaseUrl() + "/auth",
				HttpMethod.POST, entity, String.class);
		String jsonString = tokenResponse.getBody();
		ObjectMapper mapper = new ObjectMapper();
		TokenData tokenData = mapper.readValue(jsonString, TokenData.class);
		user.setTokenData(tokenData);
		user.setToken(tokenData.getBearer());

		HttpHeaders authHeaders = new HttpHeaders();
		authHeaders.set("Authorization", "Bearer " + user.getToken());
		HttpEntity<Void> authEntity = new HttpEntity<>(authHeaders);

		ResponseEntity<User> userInfoResponse = restTemplate.exchange(webClientProperties.getBaseUrl() + "/userinfo",
				HttpMethod.GET, authEntity, User.class);
		User tempUser = userInfoResponse.getBody();
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
		User user = (User) VaadinSession.getCurrent().getAttribute("user");
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
