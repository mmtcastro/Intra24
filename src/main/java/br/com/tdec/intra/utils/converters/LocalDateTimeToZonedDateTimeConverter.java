package br.com.tdec.intra.utils.converters;

import java.io.Serial;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class LocalDateTimeToZonedDateTimeConverter implements Converter<LocalDateTime, ZonedDateTime> {

    @Serial
    private static final long serialVersionUID = 1L;

	@Override
	public Result<ZonedDateTime> convertToModel(LocalDateTime value, ValueContext context) {
		if (value == null) {
			return Result.error("LocalDateTime is null");
		}
		// Assuming the default system timezone. You can customize this if needed.
		ZoneId zoneId = ZoneId.systemDefault();
		return Result.ok(ZonedDateTime.of(value, zoneId));
	}

	@Override
	public LocalDateTime convertToPresentation(ZonedDateTime value, ValueContext context) {
		if (value == null) {
			return null;
		}
		return value.toLocalDateTime();
	}
}
