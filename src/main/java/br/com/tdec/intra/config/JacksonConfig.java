package br.com.tdec.intra.config;

import java.time.ZonedDateTime;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.tdec.intra.utils.jackson.ZonedDateTimeDeserializer;

@Configuration
public class JacksonConfig {

	/*
	 * Sem este WebClientConfig, o ZonedDateTime não é deserializado corretamente e
	 * não é gravado do Domino
	 * 
	 */
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();

		// Registra o módulo que lida com a API de data e hora do Java 8
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer());
		objectMapper.registerModule(javaTimeModule);

		// Evita que as datas sejam escritas como timestamps
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return objectMapper;
	}

}
