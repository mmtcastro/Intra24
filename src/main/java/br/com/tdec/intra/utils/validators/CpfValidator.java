package br.com.tdec.intra.utils.validators;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import java.io.Serial;

public class CpfValidator implements Validator<String> {

    @Serial
    private static final long serialVersionUID = 1L;

	@Override
	public ValidationResult apply(String value, ValueContext context) {
		if (value == null || value.trim().isEmpty()) {
			return ValidationResult.error("O CPF é obrigatório");
		}

		String cpf = value.replaceAll("[^0-9]", ""); // Remove caracteres não numéricos

		if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}") || !isValidCpf(cpf)) {
			return ValidationResult.error("CPF inválido");
		}

		return ValidationResult.ok();
	}

	private boolean isValidCpf(String cpf) {
		return calcularDigitoVerificador(cpf, 9) == Character.getNumericValue(cpf.charAt(9))
				&& calcularDigitoVerificador(cpf, 10) == Character.getNumericValue(cpf.charAt(10));
	}

	private int calcularDigitoVerificador(String cpf, int posicao) {
		int[] pesos = (posicao == 9) ? new int[] { 10, 9, 8, 7, 6, 5, 4, 3, 2 }
				: new int[] { 11, 10, 9, 8, 7, 6, 5, 4, 3, 2 };

		int soma = 0;
		for (int i = 0; i < posicao; i++) {
			soma += Character.getNumericValue(cpf.charAt(i)) * pesos[i];
		}
		return (soma % 11 < 2) ? 0 : (11 - soma % 11);
	}
}
