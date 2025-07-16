package br.com.tdec.intra.utils.validators;

import java.io.Serial;
import java.util.regex.Pattern;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

public class TelefoneValidator implements Validator<String> {

    /**
     * 
     */
    @Serial
    private static final long serialVersionUID = 1L;
	private static final Pattern TELEFONE_PATTERN = Pattern.compile("^\\+?[0-9\\s()-]{10,20}$");

	@Override
	public ValidationResult apply(String value, ValueContext context) {
		if (value == null || value.trim().isEmpty()) {
			return ValidationResult.error("O telefone não pode estar vazio");
		}

		if (!TELEFONE_PATTERN.matcher(value).matches()) {
			return ValidationResult.error("Número de telefone inválido");
		}

		return ValidationResult.ok();
	}
}
