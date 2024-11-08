package br.com.tdec.intra.utils.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import br.com.tdec.intra.abs.AbstractModelDoc.RichText;

public class BodyDeserializer extends JsonDeserializer<RichText> {

	public BodyDeserializer() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public RichText deserialize(JsonParser jsonParser, DeserializationContext context)
			throws IOException, JsonProcessingException {
		JsonNode node = jsonParser.getCodec().readTree(jsonParser);
		RichText richText = new RichText();
		richText.setType(node.get("type").asText());
		richText.setEncoding(node.get("encoding").asText());
		richText.setContent(node.get("content").asText());
		return richText;
	}

}
