package br.com.tdec.intra.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.tdec.intra.config.WebClientConfig.TokenData;
import br.com.tdec.intra.config.WebClientProperties;
import reactor.core.publisher.Mono;

public class UtilsWebClient {

	public static String getRestApiToken(WebClientProperties webClientProperties) {
		System.out.println("WebClientConfig - iniciando autenticacao");
		long startTime = System.nanoTime();
		String token = "";
		int BUFFER_SIZE = 16 * 1024 * 1024; // aumentar a quantidade de registros retornados pelo API.

		WebClient webClient = WebClient.builder().baseUrl(webClientProperties.getBaseUrl())
				.codecs(clientCodecConfigurer -> {
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
			token = tokenData.getBearer();
			System.out.println("Fim autenticacao " + tokenData.getBearer());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long endTime = System.nanoTime();
		long durationNanos = endTime - startTime; // tempo de execução em nanossegundos
		double durationSeconds = durationNanos / 1_000_000_000.0; // convertendo para segundos

		System.out.println("Tempo de execução: " + durationSeconds + " segundos");
		return token;
	}
}
