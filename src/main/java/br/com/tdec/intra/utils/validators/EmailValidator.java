package br.com.tdec.intra.utils.validators;

import java.io.Serial;
import java.util.regex.Pattern;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

public class EmailValidator implements Validator<String> {

    @Serial
    private static final long serialVersionUID = 1L;
	private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
	private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

	@Override
	public ValidationResult apply(String value, ValueContext context) {
		if (value == null || value.trim().isEmpty()) {
			return ValidationResult.error("O e-mail é obrigatório");
		}

		if (!pattern.matcher(value).matches()) {
			return ValidationResult.error("Formato de e-mail inválido");
		}

		return ValidationResult.ok();
	}
}
