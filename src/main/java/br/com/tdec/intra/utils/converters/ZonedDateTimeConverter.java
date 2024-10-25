package br.com.tdec.intra.utils.converters;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class ZonedDateTimeConverter implements Converter<LocalDate, ZonedDateTime> {

	private static final long serialVersionUID = 1L;
	private ZoneId zoneId;

	public ZonedDateTimeConverter() {
		this.zoneId = ZoneId.of("America/Sao_Paulo");
		// this.zoneId = Objects.requireNonNull(zoneId, "Zone identifier cannot be
		// null");
	}

//	public ZonedDateTimeConverter() {
//		this.zoneId = ZoneId.systemDefault(); // Padrão para o fuso horário do sistema
//	}

	@Override
	public Result<ZonedDateTime> convertToModel(LocalDate localDate, ValueContext context) {
		if (localDate == null) {
			return Result.ok(null);
		}
		return Result.ok(localDate.atStartOfDay(zoneId));
	}

	@Override
	public LocalDate convertToPresentation(ZonedDateTime zonedDateTime, ValueContext context) {
		if (zonedDateTime == null) {
			return null;
		}
		// Converte ZonedDateTime para LocalDate, ajustando o fuso horário
		return zonedDateTime.withZoneSameInstant(zoneId).toLocalDate();
	}

	// Conversão entre Date (java.util.Date) e ZonedDateTime
	public ZonedDateTime convertDateToZonedDateTime(Date date) {
		if (date == null) {
			return null;
		}
		// Converte de java.util.Date para ZonedDateTime com o fuso horário definido
		Instant instant = date.toInstant();
		return instant.atZone(zoneId);
	}

	public Date convertZonedDateTimeToDate(ZonedDateTime zonedDateTime) {
		if (zonedDateTime == null) {
			return null;
		}
		// Converte de ZonedDateTime para java.util.Date
		Instant instant = zonedDateTime.toInstant();
		return Date.from(instant);
	}

}
