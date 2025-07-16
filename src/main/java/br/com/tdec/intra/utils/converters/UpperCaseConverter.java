package br.com.tdec.intra.utils.converters;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.io.Serial;

public class UpperCaseConverter implements Converter<String, String> {

    @Serial
    private static final long serialVersionUID = 1L;

	@Override
	public Result<String> convertToModel(String value, ValueContext context) {
		// Convert the value to uppercase when writing to the model
		return Result.ok(value != null ? value.toUpperCase() : null);
	}

	@Override
	public String convertToPresentation(String value, ValueContext context) {
		// Convert the value to uppercase when reading from the model
		return value != null ? value.toUpperCase() : null;
	}

}
