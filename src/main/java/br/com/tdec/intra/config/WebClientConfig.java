package br.com.tdec.intra.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

@Configuration
@Getter
@Setter
public class WebClientConfig {
	private WebClient webClient;
	private String token;
	private Mono<String> tokenMono;
	private static final int BUFFER_SIZE = 16 * 1024 * 1024; // aumentar a quantidade de registros retornados pelo API.

	public WebClientConfig(WebClientProperties webClientProperties) {
		System.out.println("WebClientConfig - iniciando autenticacao");
		long startTime = System.nanoTime();
		webClient = WebClient.builder().baseUrl(webClientProperties.getBaseUrl()).codecs(clientCodecConfigurer -> {
			clientCodecConfigurer.defaultCodecs().maxInMemorySize(BUFFER_SIZE);
		}).build();

		Map<String, String> credentials = new HashMap<>();
		credentials.put("username", webClientProperties.getUsername());
		credentials.put("password", webClientProperties.getPassword());

		try {
			Mono<String> tokenResponse = webClient.post().uri("/auth").contentType(MediaType.APPLICATION_JSON)
					.bodyValue(credentials).retrieve().bodyToMono(String.class);
			String jsonString = tokenResponse.block();
			System.out.println(jsonString);
			ObjectMapper mapper = new ObjectMapper();
			TokenData tokenData;

			tokenData = mapper.readValue(jsonString, TokenData.class);
			this.token = tokenData.getBearer();
			System.out.println("Fim autenticacao " + tokenData.getBearer());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long endTime = System.nanoTime();
		long durationNanos = endTime - startTime; // tempo de execução em nanossegundos
		double durationSeconds = durationNanos / 1_000_000_000.0; // convertendo para segundos

		System.out.println("Tempo de execução: " + durationSeconds + " segundos");

	}

//	public WebClientConfig(WebClientProperties webClientProperties) {
//		System.out.println("WebClientConfig - iniciando autenticacao");
//		long startTime = System.nanoTime();
//
//		webClient = WebClient.builder().baseUrl(webClientProperties.getBaseUrl()).codecs(clientCodecConfigurer -> {
//			clientCodecConfigurer.defaultCodecs().maxInMemorySize(BUFFER_SIZE);
//		}).build();
//
//		Map<String, String> credentials = new HashMap<>();
//		credentials.put("username", webClientProperties.getUsername());
//		credentials.put("password", webClientProperties.getPassword());
//
//		tokenMono = webClient.post().uri("/auth").contentType(MediaType.APPLICATION_JSON).bodyValue(credentials)
//				.retrieve().bodyToMono(String.class).map(jsonString -> {
//					try {
//						ObjectMapper mapper = new ObjectMapper();
//						TokenData tokenData = mapper.readValue(jsonString, TokenData.class);
//						return tokenData.getBearer();
//					} catch (Exception e) {
//						throw new RuntimeException(e);
//					}
//				}).doOnSuccess(token -> {
//					long endTime = System.nanoTime();
//					double durationSeconds = (endTime - startTime) / 1_000_000_000.0;
//					System.out.println("Fim autenticacao " + token);
//					System.out.println("Tempo de execução: " + durationSeconds + " segundos");
//				}).doOnError(e -> e.printStackTrace());
//
//		// Now, tokenMono holds a reactive pipeline for fetching the token.
//		// To use the token, subscribe to this Mono or use it in further reactive
//		// chains.
//
//		tokenMono.subscribe(token -> {
//			this.token = token;
//			System.out.println("Token recebido e autenticação concluída: " + token);
//			// Execute qualquer lógica adicional que dependa do token aqui dentro
//		}, error -> {
//			// Lógica de tratamento de erro
//			System.err.println("Erro na autenticação: " + error.getMessage());
//		});
//
//	}

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

}
