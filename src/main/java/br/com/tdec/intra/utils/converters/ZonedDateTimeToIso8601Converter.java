package br.com.tdec.intra.utils.converters;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class ZonedDateTimeToIso8601Converter implements Converter<LocalDate, ZonedDateTime> {
	private static final long serialVersionUID = 1L;
	// Usa o formatador ISO para garantir apenas data, hora e offset, sem o ID de
	// zona
	private static final DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

	@Override
	public Result<ZonedDateTime> convertToModel(LocalDate value, ValueContext context) {
		if (value == null) {
			return Result.ok(null);
		}

		// Converte LocalDate para ZonedDateTime usando o fuso horário do sistema
		ZonedDateTime zonedDateTime = value.atStartOfDay(ZoneId.systemDefault());

		// Formata para ISO 8601 com o fuso horário sem o ID de zona
		String formattedDate = zonedDateTime.withZoneSameInstant(ZoneOffset.ofHours(-3)).format(isoFormatter);
		ZonedDateTime resultDateTime = ZonedDateTime.parse(formattedDate, isoFormatter);

		return Result.ok(resultDateTime);
	}

	@Override
	public LocalDate convertToPresentation(ZonedDateTime value, ValueContext context) {
		if (value == null) {
			return null;
		}

		// Converte ZonedDateTime para LocalDate para exibir no DatePicker
		return value.toLocalDate();
	}
}
