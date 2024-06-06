package br.com.tdec.intra.converters;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class StringToZonedDateTimeConverter implements Converter<String, ZonedDateTime> {

	private static final long serialVersionUID = 1L;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", new Locale("pt", "BR"));

	@Override
	public Result<ZonedDateTime> convertToModel(String value, ValueContext context) {
		if (value == null) {
			return Result.ok(null);
		}
		try {
			ZonedDateTime zonedDateTime = ZonedDateTime.parse(value, formatter);
			return Result.ok(zonedDateTime);
		} catch (DateTimeParseException e) {
			return Result.error("Data e hora inválidas");
		}
	}

	@Override
	public String convertToPresentation(ZonedDateTime value, ValueContext context) {
		if (value == null) {
			return "";
		}
		return value.format(formatter);
	}

	public DateTimeFormatter getFormatter() {
		return formatter;
	}

	public void setFormatter(DateTimeFormatter formatter) {
		this.formatter = formatter;
	}

}
