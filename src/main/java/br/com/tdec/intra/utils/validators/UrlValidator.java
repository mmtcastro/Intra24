package br.com.tdec.intra.utils.validators;

import java.io.Serial;
import java.net.URI;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

public class UrlValidator implements Validator<String> {

    @Serial
    private static final long serialVersionUID = 1L;
	private final boolean required;

	public UrlValidator(boolean required) {
		this.required = required;
	}

	@Override
	public ValidationResult apply(String value, ValueContext context) {
		if (value == null || value.trim().isEmpty()) {
			return required ? ValidationResult.error("A URL é obrigatória") : ValidationResult.ok();
		}

		try {
			URI uri = new URI(value);
			if (uri.getScheme() == null || uri.getHost() == null) {
				return ValidationResult.error("Formato de URL/URI inválido");
			}
		} catch (Exception e) {
			return ValidationResult.error("Formato de URL/URI inválido");
		}

		return ValidationResult.ok();
	}
}
