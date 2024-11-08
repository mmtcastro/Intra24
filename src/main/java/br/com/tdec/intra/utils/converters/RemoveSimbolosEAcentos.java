package br.com.tdec.intra.utils.converters;

import java.text.Normalizer;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class RemoveSimbolosEAcentos implements Converter<String, String> {
	private static final long serialVersionUID = 1L;

	@Override
	public Result<String> convertToModel(String value, ValueContext context) {
		// Remove acentos, números e símbolos ao salvar no modelo
		return Result.ok(value != null ? removeAccentsAndSymbols(value) : null);
	}

	@Override
	public String convertToPresentation(String value, ValueContext context) {
		// Remove acentos, números e símbolos ao ler do modelo
		return value != null ? removeAccentsAndSymbols(value) : null;
	}

	private String removeAccentsAndSymbols(String input) {
		// Remove acentos
		String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
		String noAccents = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

		// Remove números e símbolos, mantendo apenas letras e espaços
		String result = noAccents.replaceAll("[^a-zA-Z ]", "");

		// Se não houver letras, retorne uma string vazia
		return result.matches(".*[a-zA-Z].*") ? result : "";
	}
}
