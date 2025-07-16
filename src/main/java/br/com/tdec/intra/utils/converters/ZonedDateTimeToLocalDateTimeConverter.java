package br.com.tdec.intra.utils.converters;

import java.io.Serial;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class ZonedDateTimeToLocalDateTimeConverter implements Converter<ZonedDateTime, LocalDateTime> {

    @Serial
    private static final long serialVersionUID = 1L;

	@Override
	public Result<LocalDateTime> convertToModel(ZonedDateTime value, ValueContext context) {
		if (value == null) {
			return Result.error("ZonedDateTime is null");
		}
		return Result.ok(value.toLocalDateTime());
	}

	@Override
	public ZonedDateTime convertToPresentation(LocalDateTime value, ValueContext context) {
		if (value == null) {
			return null;
		}
		// Assuming the default system timezone. You can customize this if needed.
		ZoneId zoneId = ZoneId.systemDefault();
		return ZonedDateTime.of(value, zoneId);
	}

}
