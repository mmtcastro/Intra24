package br.com.tdec.intra.empresas.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import br.com.tdec.intra.abs.AbstractModelDoc;
import br.com.tdec.intra.utils.jackson.BodyDeserializer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cargo extends AbstractModelDoc {

	@JsonAlias({ "obs", "Obs" })
	@JsonDeserialize(using = BodyDeserializer.class)
	protected RichText obs;
}
