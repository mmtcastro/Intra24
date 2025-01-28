package br.com.tdec.intra.empresas.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import br.com.tdec.intra.abs.AbstractModelDoc;
import br.com.tdec.intra.utils.jackson.BodyDeserializer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GrupoEconomico extends AbstractModelDoc {
	@JsonAlias({ "GerenteConta", "gerenteConta" })
	private String gerenteConta;
	@JsonAlias({ "quantNegocios", "QuantNegocios" })
	private Double quantNegocios;
	@JsonAlias({ "dataUltimoNegocio", "DataUltimoNegocio" })
	private Date dataUltimoNegocio;
	@JsonAlias({ "parceriaPrimeiroNegocio", "ParceriaPrimeiroNegocio" })
	private String parceriaPrimeiroNegocio;
	@JsonAlias({ "origemCliente", "OrigemCliente" })
	private String origemCliente;

	@JsonAlias({ "obs", "Obs" })
	@JsonDeserialize(using = BodyDeserializer.class)
	protected RichText obs;

	public GrupoEconomico() {
		super();
	}

	public int compareTo(AbstractModelDoc outro) {
		if (getCodigo() != null) {
			return getCodigo().compareTo(outro.getCodigo());
		} else {
			return 0;
		}
	}
}
