package br.com.tdec.intra.utils.converters;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.io.Serial;

public class ProperCaseConverter implements Converter<String, String> {
    @Serial
    private static final long serialVersionUID = 1L;

	@Override
	public Result<String> convertToModel(String value, ValueContext context) {
		// Converte o valor para Proper Case ao salvar no modelo
		return Result.ok(value != null ? toProperCase(value) : null);
	}

	@Override
	public String convertToPresentation(String value, ValueContext context) {
		// Converte o valor para Proper Case ao ler do modelo
		return value != null ? toProperCase(value) : null;
	}

	private String toProperCase(String input) {
		StringBuilder properCase = new StringBuilder();
		String[] words = input.toLowerCase().split(" ");

		for (String word : words) {
			if (!word.isEmpty()) {
				// Capitaliza a primeira letra de cada palavra
				properCase.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
			}
		}

		// Remove o último espaço extra
		return properCase.toString().trim();
	}
}
