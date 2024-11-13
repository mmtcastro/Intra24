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
		JavaTimeModule javaTimeModule = new JavaTimeModule();

		// Adiciona deserializadores para tipos de data
		javaTimeModule.addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer());
		objectMapper.registerModule(javaTimeModule);

		// Desabilita serialização como timestamp
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

//		// Registra o deserializador para AbstractModelDoc e seus subtipos
//		SimpleModule module = new SimpleModule();
//		module.addDeserializer(AbstractModelDoc.class, new AbstractModelDocDeserializer<>(AbstractModelDoc.class));
//		objectMapper.registerModule(module);
//
//		// Adiciona mixin para subtipos
//		objectMapper.addMixIn(AbstractModelDoc.class, AbstractModelDocMixIn.class);

		return objectMapper;
	}

}
