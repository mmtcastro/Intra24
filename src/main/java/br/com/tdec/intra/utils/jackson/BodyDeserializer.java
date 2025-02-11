package br.com.tdec.intra.utils.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import br.com.tdec.intra.abs.AbstractModelDoc.RichText;

public class BodyDeserializer extends JsonDeserializer<RichText> {

	@Override
	public RichText deserialize(JsonParser jsonParser, DeserializationContext context)
			throws IOException, JsonProcessingException {

		JsonNode node = jsonParser.getCodec().readTree(jsonParser);

		// Criando um novo objeto RichText
		RichText richText = new RichText();

		// Verifica se cada campo existe antes de tentar acessá-lo
		if (node.has("type") && !node.get("type").isNull()) {
			richText.setType(node.get("type").asText());
		} else {
			richText.setType(""); // Definir valor padrão
		}

		if (node.has("encoding") && !node.get("encoding").isNull()) {
			richText.setEncoding(node.get("encoding").asText());
		} else {
			richText.setEncoding(""); // Definir valor padrão
		}

		if (node.has("content") && !node.get("content").isNull()) {
			richText.setContent(node.get("content").asText());
		} else {
			richText.setContent(""); // Definir valor padrão
		}

		return richText;
	}
}
