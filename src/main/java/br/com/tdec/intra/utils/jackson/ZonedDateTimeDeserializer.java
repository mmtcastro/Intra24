package br.com.tdec.intra.utils.jackson;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

	// Construtor obrigatório padrão sem argumentos
	public ZonedDateTimeDeserializer() {
	}

	@Override
	public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		String date = p.getText();

		// Verifica se a string é vazia ou nula
		if (date == null || date.trim().isEmpty()) {
			return null; // Retorna null se o valor for vazio
		}

		// Verifica se há fração de segundos com dois dígitos e ajusta para três
		if (date.matches(".*T.*\\d{2}\\.\\d{2}-.*")) {
			// Adiciona um 0 no final da fração de segundos para conformidade com o formato
			date = date.replaceFirst("(\\.\\d{2})(-.*)", "$10$2");
		}

		// Agora tenta desserializar o ZonedDateTime
		try {
			return ZonedDateTime.parse(date, FORMATTER);
		} catch (DateTimeParseException e) {
			throw new JsonProcessingException("Erro ao desserializar a data: " + date, e) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;
			};
		}
	}
}
