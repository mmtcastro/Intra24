package br.com.tdec.intra.converters;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class StringToZonedDateTimeConverter implements Converter<String, ZonedDateTime> {

	private static final long serialVersionUID = 1L;
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	@Override
	public Result<ZonedDateTime> convertToModel(String value, ValueContext context) {
		if (value == null || value.isEmpty()) {
			return Result.error("String is null or empty");
		}
		try {
			LocalDateTime localDateTime = LocalDateTime.parse(value, formatter);
			ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
			return Result.ok(zonedDateTime);
		} catch (DateTimeParseException e) {
			return Result.error("Invalid date format. Please use dd/MM/yyyy HH:mm");
		}
	}

	@Override
	public String convertToPresentation(ZonedDateTime value, ValueContext context) {
		if (value == null) {
			return "";
		}
		return value.format(formatter);
	}

}
