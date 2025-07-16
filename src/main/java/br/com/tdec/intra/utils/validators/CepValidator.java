package br.com.tdec.intra.utils.validators;

import java.io.Serial;
import java.util.regex.Pattern;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

public class CepValidator implements Validator<String> {

    /**
     * 
     */
    @Serial
    private static final long serialVersionUID = 1L;
	private static final Pattern CEP_PATTERN = Pattern.compile("^\\d{5}-\\d{3}$");

	@Override
	public ValidationResult apply(String value, ValueContext context) {
		if (value == null || value.trim().isEmpty()) {
			return ValidationResult.error("O CEP não pode estar vazio");
		}

		if (!CEP_PATTERN.matcher(value).matches()) {
			return ValidationResult.error("Formato de CEP inválido (ex: 12345-678)");
		}

		return ValidationResult.ok();
	}
}
