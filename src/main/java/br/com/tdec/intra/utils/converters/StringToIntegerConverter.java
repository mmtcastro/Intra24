package br.com.tdec.intra.utils.converters;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.io.Serial;

public class StringToIntegerConverter implements Converter<String, Integer> {

    @Serial
    private static final long serialVersionUID = 1L;

	@Override
	public Result<Integer> convertToModel(String fieldValue, ValueContext context) {
		// Produces a converted value or an error
		try {
			// ok is a static helper method that
			// creates a Result
			return Result.ok(Integer.valueOf(fieldValue));
		} catch (NumberFormatException e) {
			// error is a static helper method
			// that creates a Result
			return Result.error("Enter a number");
		}
	}

	@Override
	public String convertToPresentation(Integer integer, ValueContext context) {
		// Converting to the field type should
		// always succeed, so there is no support for
		// returning an error Result.
		return String.valueOf(integer);
	}

}
