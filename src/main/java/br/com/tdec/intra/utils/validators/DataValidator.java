package br.com.tdec.intra.utils.validators;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

public class DataValidator implements Validator<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@Override
	public ValidationResult apply(String value, ValueContext context) {
		if (value == null || value.trim().isEmpty()) {
			return ValidationResult.error("A data não pode estar vazia");
		}

		try {
			LocalDate data = LocalDate.parse(value, FORMATTER);
			if (data.isAfter(LocalDate.now())) {
				return ValidationResult.error("A data não pode ser no futuro");
			}
		} catch (DateTimeParseException e) {
			return ValidationResult.error("Formato de data inválido (use dd/MM/yyyy)");
		}

		return ValidationResult.ok();
	}
}
