package br.com.tdec.intra.utils.validators;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

public class CnpjValidator implements Validator<String> {

	private static final long serialVersionUID = 1L;

	@Override
	public ValidationResult apply(String value, ValueContext context) {
		if (value == null || value.trim().isEmpty()) {
			return ValidationResult.error("CNPJ não pode estar vazio");
		}

		String cnpj = value.replaceAll("[^0-9]", ""); // Remove caracteres não numéricos

		if (cnpj.length() != 14) {
			return ValidationResult.error("CNPJ deve ter 14 dígitos numéricos");
		}

		if (!isValidCnpj(cnpj)) {
			return ValidationResult.error("CNPJ inválido");
		}

		return ValidationResult.ok();
	}

	private boolean isValidCnpj(String cnpj) {
		if (cnpj.matches("(\\d)\\1{13}"))
			return false; // Verifica se todos os dígitos são iguais (inválido)

		int[] pesos1 = { 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };
		int[] pesos2 = { 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };

		int soma1 = 0, soma2 = 0;
		for (int i = 0; i < 12; i++) {
			soma1 += Character.getNumericValue(cnpj.charAt(i)) * pesos1[i];
			soma2 += Character.getNumericValue(cnpj.charAt(i)) * pesos2[i];
		}

		int digito1 = (soma1 % 11 < 2) ? 0 : (11 - soma1 % 11);
		soma2 += digito1 * pesos2[12];
		int digito2 = (soma2 % 11 < 2) ? 0 : (11 - soma2 % 11);

		return (digito1 == Character.getNumericValue(cnpj.charAt(12))
				&& digito2 == Character.getNumericValue(cnpj.charAt(13)));
	}
}
