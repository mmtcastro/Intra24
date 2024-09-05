package br.com.tdec.intra.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

@Configuration
@Getter
@Setter
public class WebClientService {
	private WebClient webClient;
	private String token;
	private Mono<String> tokenMono;
	private static final int BUFFER_SIZE = 16 * 1024 * 1024; // aumentar a quantidade de registros retornados pelo API.

//	public WebClientService(WebClientProperties webClientProperties) {
//		webClient = WebClient.builder().baseUrl(webClientProperties.getBaseUrl()).codecs(clientCodecConfigurer -> {
//			clientCodecConfigurer.defaultCodecs().maxInMemorySize(BUFFER_SIZE);
//		}).build();
//
//	}

	public WebClientService(WebClient webClient) {
		this.webClient = webClient.mutate()
				.codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs().maxInMemorySize(BUFFER_SIZE))
				.build();
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
