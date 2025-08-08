package br.com.tdec.intra.utils.converters;

import java.util.List;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class ChainedConverter implements Converter<String, String> {

	/**
	 * Utilizado no MultivalueGrid para aplicarmos multiplas conversões em apenas
	 * uma linha
	 */
	private static final long serialVersionUID = 1L;
	private final List<Converter<String, String>> converters;

	@SuppressWarnings("unchecked")
	public ChainedConverter(Converter<String, String>... converters) {
		this.converters = List.of(converters);
	}

	@Override
	public Result<String> convertToModel(String value, ValueContext context) {
		String result = value;
		for (Converter<String, String> c : converters) {
			Result<String> r = c.convertToModel(result, context);
			if (r.isError())
				return r;
			result = r.getOrThrow(e -> new RuntimeException("Erro na conversão"));
		}
		return Result.ok(result);
	}

	@Override
	public String convertToPresentation(String value, ValueContext context) {
		for (int i = converters.size() - 1; i >= 0; i--) {
			value = converters.get(i).convertToPresentation(value, context);
		}
		return value;
	}
}
