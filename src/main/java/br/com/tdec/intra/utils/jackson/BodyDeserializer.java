package br.com.tdec.intra.utils.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import br.com.tdec.intra.abs.AbstractModelDoc;
import br.com.tdec.intra.abs.AbstractModelDoc.Body;

public class BodyDeserializer extends JsonDeserializer<Body> {

	public BodyDeserializer() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public Body deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		JsonNode node = p.getCodec().readTree(p); // Cast to JsonNode
		Body body = new Body();

		// Use asText() method to extract values as Strings
		body.setType(node.get("type").asText());
		body.setEncoding(node.get("encoding").asText());
		body.setContent(node.get("content").asText()); // Decodes HTML content as a plain string

		return body;
	}

	public AbstractModelDoc getParent() {
		return parent;
	}

	public void setParent(AbstractModelDoc parent) {
		this.parent = parent;
	}

}
