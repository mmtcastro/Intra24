package br.com.tdec.intra.utils.converters;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import br.com.tdec.intra.abs.AbstractModelDoc;

public class RichTextToStringConverter implements Converter<String, AbstractModelDoc.RichText> {

	private static final long serialVersionUID = 1L;

	@Override
	public Result<AbstractModelDoc.RichText> convertToModel(String value, ValueContext context) {
		AbstractModelDoc.RichText richText = new AbstractModelDoc.RichText();

		// Armazena o conteúdo HTML diretamente
		// richText.setContent(value);
		richText.setContent(value != null ? value : ""); // Define o conteúdo como o valor informado
		richText.setEncoding("PLAIN"); // Definimos o encoding como PLAIN, pois é HTML direto
		richText.setType("text/html"); // Define o tipo como HTML

		return Result.ok(richText);
	}

	@Override
	public String convertToPresentation(AbstractModelDoc.RichText richText, ValueContext context) {
		// Retorna o conteúdo HTML diretamente, sem codificação
		if (richText != null && "text/html".equalsIgnoreCase(richText.getType()) && richText.getContent() != null) {
			return richText.getContent();
		}
		return "";
	}
}
