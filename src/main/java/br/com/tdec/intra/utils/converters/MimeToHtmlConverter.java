package br.com.tdec.intra.utils.converters;

import java.io.ByteArrayInputStream;
import java.util.Base64;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;

public class MimeToHtmlConverter implements Converter<String, String> {

	private static final long serialVersionUID = 1L;

	@Override
	public Result<String> convertToModel(String htmlContent, ValueContext context) {
		try {
			if (htmlContent == null || htmlContent.isEmpty()) {
				return Result.ok(null);
			}

			var htmlPart = new jakarta.mail.internet.MimeBodyPart();
			htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");

			var multipart = new MimeMultipart("related");
			multipart.addBodyPart(htmlPart);

			var outputStream = new java.io.ByteArrayOutputStream();
			multipart.writeTo(outputStream);
			return Result.ok(Base64.getEncoder().encodeToString(outputStream.toByteArray()));
		} catch (Exception e) {
			return Result.error("Erro ao converter HTML para MIME.");
		}
	}

	@Override
	public String convertToPresentation(String base64Content, ValueContext context) {
		try {
			if (base64Content == null || base64Content.isEmpty()) {
				return null;
			}

			byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
			// Session session = Session.getInstance(new Properties());
			MimeMultipart multipart = new MimeMultipart(
					new ByteArrayDataSource(new ByteArrayInputStream(decodedBytes), "multipart/related"));

			for (int i = 0; i < multipart.getCount(); i++) {
				var part = multipart.getBodyPart(i);

				if (part.isMimeType("text/html")) {
					return (String) part.getContent();
				}
			}
		} catch (Exception e) {
			System.out.println("Erro ao converter MIME para HTML: " + e.getMessage());
		}
		return null;
	}
}