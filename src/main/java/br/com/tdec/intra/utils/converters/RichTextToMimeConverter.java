package br.com.tdec.intra.utils.converters;

import java.io.Serial;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import br.com.tdec.intra.abs.AbstractModelDoc;

public class RichTextToMimeConverter implements Converter<String, AbstractModelDoc.RichText> {

    @Serial
    private static final long serialVersionUID = 1L;

	@Override
	public Result<AbstractModelDoc.RichText> convertToModel(String value, ValueContext context) {
		AbstractModelDoc.RichText richText = new AbstractModelDoc.RichText();

		if (value != null && !value.trim().isEmpty()) {
			// Codifica o HTML em Base64 para enviar no formato MIME
			String base64Content = Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));

			richText.setContent(base64Content);
			richText.setEncoding("BASE64"); // Define a codificação como Base64 para MIME
			richText.setType("multipart/form-data"); // Define o tipo MIME
			richText.setHeaders("Content-Type: text/html; charset=UTF-8"); // Define cabeçalhos para HTML com charset
		} else {
			// Se o conteúdo estiver vazio, define valores padrão para o RichText
			richText.setContent("");
			richText.setEncoding("PLAIN"); // Alternativa caso o conteúdo seja vazio
			richText.setType("text/html");
		}

		return Result.ok(richText);
	}

	@Override
	public String convertToPresentation(AbstractModelDoc.RichText richText, ValueContext context) {
		if (richText != null && "BASE64".equalsIgnoreCase(richText.getEncoding()) && richText.getContent() != null) {
			// Decodifica o conteúdo Base64 para exibição no RichTextEditor
			byte[] decodedBytes = Base64.getDecoder().decode(richText.getContent());
			return new String(decodedBytes, StandardCharsets.UTF_8);
		} else if (richText != null && "PLAIN".equalsIgnoreCase(richText.getEncoding())) {
			// Retorna conteúdo diretamente se for PLAIN
			return richText.getContent();
		}
		return "";
	}
}
