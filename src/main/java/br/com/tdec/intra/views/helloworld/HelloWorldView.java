package br.com.tdec.intra.views.helloworld;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import br.com.tdec.intra.config.DominoServer;
import br.com.tdec.intra.empresas.model.Empresa;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

@PageTitle("Hello World")
@Route(value = "hello", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
@Data
@EqualsAndHashCode(callSuper=false)
public class HelloWorldView extends HorizontalLayout {

	private static final long serialVersionUID = 1L;
	private final DominoServer dominoServer;
	private TextField name;
	private Button sayHello;
	private String token;
	private final WebClient webClient;
	private Button getUserInfoButton = new Button("Get User Info");
	private Button getViewsButton = new Button("Get Views");
	private Button getScopesButton = new Button("Get Scopes");
	private Button getGruposEconomicosButton = new Button("Get Grupos Economicos");
	private Button getVerticaisButton =  new Button("Get Verticais");

	public HelloWorldView(DominoServer dominoServer) {
		this.dominoServer = dominoServer;
		webClient = createWebClient();
		name = new TextField("Your name");
		sayHello = new Button("Diga Olá");

		sayHello.addClickListener(e -> {
			Notification.show("Hello " + name.getValue());
		});
		getUserInfoButton.addClickListener(e -> {
			getUserInfo();
		});

		getViewsButton.addClickListener(e -> {
			getViews();
		});
		
		getScopesButton.addClickListener(e -> {
			getScopes();
		});
		
		getGruposEconomicosButton.addClickListener(e -> {
			getGruposEconomicos();
		});
		
		getVerticaisButton.addClickListener(e -> {
			getVerticais();
		});
		
		

		Empresa empresa = new Empresa();
		empresa.setNome("TDEC");

		sayHello.addClickShortcut(Key.ENTER);
		Text serverName = new Text(dominoServer.getHostName());

		setMargin(true);
		setVerticalComponentAlignment(Alignment.END, name, sayHello);

		add(name, sayHello, getUserInfoButton,getViewsButton,getScopesButton ,getGruposEconomicosButton,getVerticaisButton);
	}

	

	private void getVerticais() {
		Mono<String> response = webClient.get().uri("/lists/Verticais?dataSource=empresasscope").header("Authorization", "Bearer " + token) // Bearer
				.retrieve().bodyToMono(String.class);
		response.subscribe(data -> System.out.println("Data: " + data), error -> System.err.println("Error: " + error),
				() -> System.out.println("Completed"));
	}



	private void getGruposEconomicos() {
		Mono<String> response = webClient.get().uri("/lists/GruposEconomicos?dataSource=empresasscope&count=10").header("Authorization", "Bearer " + token) // Bearer
				.retrieve().bodyToMono(String.class);
		response.subscribe(data -> System.out.println("Data: " + data), error -> System.err.println("Error: " + error),
				() -> System.out.println("Completed"));
		
		
	}



	public WebClient createWebClient() {
		WebClient webClient = WebClient.builder().baseUrl("http://zoloft.tdec.com.br:8880/api/v1/") // Base URL
				.build();
		try {
			Map<String, String> credentials = new HashMap<>();
			credentials.put("username", "mcastro"); // Replace with actual username
			credentials.put("password", "Hodge$404"); // Replace with actual password

			// Send the POST request with authentication credentials

			Mono<String> tokenResponse = webClient.post().uri("/auth").contentType(MediaType.APPLICATION_JSON)
					.bodyValue(credentials).retrieve().bodyToMono(String.class);
			String jsonString = tokenResponse.block();
			System.out.println(jsonString);
			ObjectMapper mapper = new ObjectMapper();
			TokenData tokenData = mapper.readValue(jsonString, TokenData.class);
			this.token = tokenData.getBearer();
			System.out.println(tokenData.getBearer());

		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return webClient;
	}
	
	public void getUserInfo() {
		Mono<String> response = webClient.get().uri("/userinfo").header("Authorization", "Bearer " + token) // Bearer
				.retrieve().bodyToMono(String.class);
		response.subscribe(data -> System.out.println("Data: " + data), error -> System.err.println("Error: " + error),
				() -> System.out.println("Completed"));
	}

	public void getViews() {
		Mono<String> response = webClient.get().uri("/lists?type=all&dataSource=empresasscope&columns=false")
				.header("Authorization", "Bearer " + token) // Bearer
				.retrieve().bodyToMono(String.class);
		response.subscribe(data -> System.out.println("Data: " + data), error -> System.err.println("Error: " + error),
				() -> System.out.println("Completed"));

	}
	
	private void getScopes() {
		Mono<String> response = webClient.get().uri("/scopes").header("Authorization", "Bearer " + token) // Bearer
				.retrieve().bodyToMono(String.class);
		response.subscribe(data -> System.out.println("Data: " + data), error -> System.err.println("Error: " + error),
				() -> System.out.println("Completed"));
		
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class TokenData {
		private String bearer;
		private Claims claims;
		private int leeway;
		@JsonProperty("expSeconds")
		private int expSeconds;
		@JsonProperty("issueDate")
		private String issueDate;
		

	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Claims {
		private String iss;
		private String sub;
		private long iat;
		private long exp;
		private List<String> aud;
		private String CN;
		private String scope;
		private String email;
	}

	public DominoServer getDominoServer() {
		return dominoServer;
	}

}
