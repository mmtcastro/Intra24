package br.com.tdec.intra.converters;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class RemoveSpacesConverter implements Converter<String, String> {

	private static final long serialVersionUID = 1L;

	@Override
	public Result<String> convertToModel(String fieldValue, ValueContext context) {
		try {
			return Result.ok(fieldValue.replaceAll(" ", ""));
		} catch (Exception e) {
			return Result.error("Entre com uma string");
		}
	}

	@Override
	public String convertToPresentation(String textoOriginal, ValueContext context) {
		if (textoOriginal != null) {
			return textoOriginal.replaceAll(" ", "");
		} else {
			return textoOriginal;
		}
	}

}
