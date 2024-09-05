package br.com.tdec.intra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
public class WebClientConfig {
	/*
	 * Sem este WebClientConfig, o ZonedDateTime não é deserializado corretamente e
	 * não é gravado do Domino
	 * 
	 */
	@Bean
	public ObjectMapper objectMapper() {
		return Jackson2ObjectMapperBuilder.json()//
				.modules(new JavaTimeModule())//
				.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).build();
	}

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
