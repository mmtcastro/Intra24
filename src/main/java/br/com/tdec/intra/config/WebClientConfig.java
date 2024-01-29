package br.com.tdec.intra.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.tdec.intra.views.helloworld.HelloWorldView.TokenData;
import lombok.Data;
import reactor.core.publisher.Mono;

@Configuration
@Data
public class WebClientConfig {
	private WebClient webClient;
	private String token;

	public WebClientConfig() {
		webClient = WebClient.builder().baseUrl("http://zoloft.tdec.com.br:8880/api/v1/") // Base URL
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
	}
}
