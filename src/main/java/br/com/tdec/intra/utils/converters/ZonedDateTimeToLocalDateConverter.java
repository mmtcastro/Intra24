package br.com.tdec.intra.utils.converters;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class ZonedDateTimeToLocalDateConverter implements Converter<LocalDate, ZonedDateTime> {

	private static final long serialVersionUID = 1L;

	@Override
	public Result<ZonedDateTime> convertToModel(LocalDate value, ValueContext context) {
		if (value == null) {
			return Result.ok(null); // Retorna nulo se não houver data selecionada
		}
		// Converte LocalDate para ZonedDateTime com a hora 00:00 e o fuso horário atual
		// ZonedDateTime zonedDateTime = value.atStartOfDay(ZoneId.systemDefault());
		ZonedDateTime zonedDateTime = value.atStartOfDay(ZoneId.of("America/Sao_Paulo"));
		return Result.ok(zonedDateTime);
	}

	@Override
	public LocalDate convertToPresentation(ZonedDateTime value, ValueContext context) {
		if (value == null) {
			return null; // Retorna nulo se o ZonedDateTime for nulo
		}
		// Converte ZonedDateTime para LocalDate (apenas a parte da data)
		return value.toLocalDate();
	}
}
