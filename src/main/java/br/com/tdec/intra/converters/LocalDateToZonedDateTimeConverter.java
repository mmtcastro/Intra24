package br.com.tdec.intra.converters;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class LocalDateToZonedDateTimeConverter implements Converter<LocalDate, ZonedDateTime> {

	private static final long serialVersionUID = 1L;
	// private static final DateTimeFormatter formatter =
	// DateTimeFormatter.ofPattern("dd/MM/yyyy");

//	@Override
//	public Result<ZonedDateTime> convertToModel(LocalDate value, ValueContext context) {
//		if (value == null) {
//			return Result.error("LocalDate is null");
//		}
//		// Assuming the default system timezone. You can customize this if needed.
//		ZoneId zoneId = ZoneId.systemDefault();
//		return Result.ok(ZonedDateTime.of(value, LocalTime.MIDNIGHT, zoneId));
//	}
//
//	@Override
//	public LocalDate convertToPresentation(ZonedDateTime value, ValueContext context) {
//		if (value == null) {
//			return null;
//		}
//		return value.toLocalDate();
//	}

	@Override
	public Result<ZonedDateTime> convertToModel(LocalDate value, ValueContext context) {
		if (value == null) {
			return Result.ok(null);
		}
		try {
			return Result.ok(value.atStartOfDay(ZoneId.systemDefault()));
		} catch (Exception e) {
			return Result.error("Invalid date format");
		}
	}

	@Override
	public LocalDate convertToPresentation(ZonedDateTime value, ValueContext context) {
		if (value == null) {
			return null;
		}
		return value.toLocalDate();
	}
}
