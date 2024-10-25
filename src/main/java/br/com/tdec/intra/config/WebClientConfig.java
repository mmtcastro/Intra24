package br.com.tdec.intra.config;

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

	@Bean
	public WebClient webClient(ObjectMapper objectMapper, WebClientProperties webClientProperties) {
		ExchangeStrategies strategies = ExchangeStrategies.builder().codecs(clientDefaultCodecsConfigurer -> {
			clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
			clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
		}).build();

//		return WebClient.builder()//
//				.exchangeStrategies(strategies).baseUrl("http://restapi.tdec.com.br:8880/api/v1")//
//				.build();

		return WebClient.builder()//
				.exchangeStrategies(strategies).baseUrl(webClientProperties.getBaseUrl())//
				.build();
	}
}
