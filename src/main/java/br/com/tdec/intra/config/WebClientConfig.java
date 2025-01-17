package br.com.tdec.intra.config;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
public class WebClientConfig {

	private final AtomicInteger index = new AtomicInteger(0);
	private static final Logger logger = LoggerFactory.getLogger(WebClientConfig.class);

	@Bean
	public WebClient webClient(ObjectMapper objectMapper, WebClientProperties webClientProperties) {
		ExchangeStrategies strategies = ExchangeStrategies.builder().codecs(clientDefaultCodecsConfigurer -> {
			clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
			clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
		}).build();

		/*
		 * Esta versão tentou fazer load balancing mas tenho v vários problemas com este
		 * approach. Primeiro que só funciona quando o servidor é iniciado, pois o
		 * webclient fica em memoria. Segundo - mesmo que o funcione, eu teria que
		 * solicitar um novo token pois o servidor é diferente Balancear realmente sera
		 * um desafio devido ao token, mesmo com load balancer.
		 */
//		return WebClient.builder().exchangeStrategies(strategies).filter((request, next) -> {
//			String currentBaseUrl = getNextBaseUrl(webClientProperties.getBaseUrls());
//			ClientRequest updatedRequest = ClientRequest.from(request)
//					.url(URI.create(currentBaseUrl + request.url().getPath())) // Convert String to URI
//					.headers(headers -> headers.addAll(request.headers())) // Maintain existing headers
//					.build();
//
//			// Log the URL being used
//			logger.info("Requisição para a URL: {}", updatedRequest.url());
//			System.out.println("Requisição para a URL: " + updatedRequest.url());
//
//			return next.exchange(updatedRequest).doOnNext(response -> {
//				logger.info("Resposta recebida com status: {}", response.statusCode());
//				System.out.println("Resposta recebida com status: " + response.statusCode());
//			}).doOnError(error -> logger.error("Erro na requisição: {}", error.getMessage()));
//		}).build();

		return WebClient.builder()//
				.exchangeStrategies(strategies).baseUrl(webClientProperties.getBaseUrl())//
				.build();
	}

	/**
	 * Retorna a próxima URL da lista usando Round-Robin.
	 */
	private String getNextBaseUrl(List<String> baseUrls) {
		int currentIndex = index.getAndUpdate(i -> (i + 1) % baseUrls.size());
		String baseUrl = baseUrls.get(currentIndex);
		logger.info("Utilizando a URL do servidor: {}", baseUrl);
		System.out.println("Utilizando a URL do servidor: " + baseUrl);
		return baseUrl;
	}

}
