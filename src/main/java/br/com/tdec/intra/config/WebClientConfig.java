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
	private static final int BUFFER_SIZE = 16 * 1024 * 1024; // for example, 16 MB

//	public WebClientConfig() {
//		System.out.println("WebClientConfig - iniciando autenticacao");
//		long startTime = System.nanoTime();
//		webClient = WebClient.builder().baseUrl("http://zoloft.tdec.com.br:8880/api/v1/") // Base URL
//				.build();
//		try {
//			Map<String, String> credentials = new HashMap<>();
//			credentials.put("username", "mcastro"); // Replace with actual username
//			credentials.put("password", "Hodge$404"); // Replace with actual password
//
//			// Send the POST request with authentication credentials
//
//			Mono<String> tokenResponse = webClient.post().uri("/auth").contentType(MediaType.APPLICATION_JSON)
//					.bodyValue(credentials).retrieve().bodyToMono(String.class);
//			String jsonString = tokenResponse.block();
//			System.out.println(jsonString);
//			ObjectMapper mapper = new ObjectMapper();
//			TokenData tokenData = mapper.readValue(jsonString, TokenData.class);
//			this.token = tokenData.getBearer();
//			System.out.println("Fim autenticacao " + tokenData.getBearer());
//			long endTime = System.nanoTime();
//			long durationNanos = endTime - startTime; // tempo de execução em nanossegundos
//			double durationSeconds = durationNanos / 1_000_000_000.0; // convertendo para segundos
//
//			System.out.println("Tempo de execução: " + durationSeconds + " segundos");
//
//		} catch (JsonMappingException e) {
//			e.printStackTrace();
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
//	}
	
	public WebClientConfig () {
		System.out.println("WebClientConfig - iniciando autenticacao");
        long startTime = System.nanoTime();

        webClient = WebClient.builder()
                .baseUrl("http://zoloft.tdec.com.br:8880/api/v1/")
                .codecs(clientCodecConfigurer -> {
                    clientCodecConfigurer.defaultCodecs().maxInMemorySize(BUFFER_SIZE);
                })
                .build();

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "mcastro");
        credentials.put("password", "Hodge$404");

        tokenMono = webClient.post()
                             .uri("/auth")
                             .contentType(MediaType.APPLICATION_JSON)
                             .bodyValue(credentials)
                             .retrieve()
                             .bodyToMono(String.class)
                             .map(jsonString -> {
                                 try {
                                     ObjectMapper mapper = new ObjectMapper();
                                     TokenData tokenData = mapper.readValue(jsonString, TokenData.class);
                                     return tokenData.getBearer();
                                 } catch (Exception e) {
                                     throw new RuntimeException(e);
                                 }
                             })
                             .doOnSuccess(token -> {
                                 long endTime = System.nanoTime();
                                 double durationSeconds = (endTime - startTime) / 1_000_000_000.0;
                                 System.out.println("Fim autenticacao " + token);
                                 System.out.println("Tempo de execução: " + durationSeconds + " segundos");
                             })
                             .doOnError(e -> e.printStackTrace());

        // Now, tokenMono holds a reactive pipeline for fetching the token.
        // To use the token, subscribe to this Mono or use it in further reactive chains.
	}
	
	
}
