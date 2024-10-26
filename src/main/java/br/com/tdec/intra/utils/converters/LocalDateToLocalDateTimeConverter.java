package br.com.tdec.intra.utils.converters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class LocalDateToLocalDateTimeConverter implements Converter<LocalDate, LocalDateTime> {
	private static final long serialVersionUID = 1L;
	private static final LocalTime DEFAULT_TIME = LocalTime.MIDNIGHT; // Define o horário padrão como 00:00

	@Override
	public Result<LocalDateTime> convertToModel(LocalDate value, ValueContext context) {
		if (value == null) {
			return Result.ok(null); // Retorna null se LocalDate for nulo
		}

		// Converte LocalDate para LocalDateTime com o horário padrão
		LocalDateTime localDateTime = value.atTime(DEFAULT_TIME);
		return Result.ok(localDateTime);
	}

	@Override
	public LocalDate convertToPresentation(LocalDateTime value, ValueContext context) {
		if (value == null) {
			return null; // Retorna null se LocalDateTime for nulo
		}

		// Converte LocalDateTime para LocalDate, descartando o horário
		return value.toLocalDate();
	}
}
