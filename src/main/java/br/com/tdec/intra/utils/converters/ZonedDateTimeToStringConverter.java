package br.com.tdec.intra.utils.converters;

import java.io.Serial;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class ZonedDateTimeToStringConverter implements Converter<String, ZonedDateTime> {

    @Serial
    private static final long serialVersionUID = 1L;
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@Override
	public Result<ZonedDateTime> convertToModel(String value, ValueContext context) {
		try {
			if (value == null || value.trim().isEmpty()) {
				return Result.ok(null); // Trata valores nulos ou vazios
			}
			return Result.ok(ZonedDateTime.parse(value, FORMATTER)); // Converte string para ZonedDateTime
		} catch (DateTimeParseException e) {
			return Result.error("Data inválida. Use o formato dd/MM/yyyy.");
		}
	}

	@Override
	public String convertToPresentation(ZonedDateTime value, ValueContext context) {
		if (value == null) {
			return ""; // Retorna string vazia se o valor for nulo
		}
		return value.format(FORMATTER); // Converte ZonedDateTime para string
	}

}
