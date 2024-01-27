package br.com.tdec.intra.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class DominoRestAuth {
	private final WebClient webClient;

	public DominoRestAuth(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                            .baseUrl("http://zoloft.tdec.com.br:8880")
                            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                            .build();
    }

	public WebClient getWebClient() {
		return webClient;
	}
	
	public Mono<String> authenticate(String username, String password) {
        AuthRequest request = new AuthRequest();
        request.setUsername("mcastro");
        request.setPassword("Hodge$404");

        return webClient.post()
                        .uri("/api/v1/auth")
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(String.class);
    }
	
	public class AuthRequest {
		private String username;
		private String password;
		
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		
		
	}
}
