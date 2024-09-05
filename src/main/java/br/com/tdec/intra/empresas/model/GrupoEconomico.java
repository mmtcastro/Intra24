package br.com.tdec.intra.empresas.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAlias;

import br.com.tdec.intra.abs.AbstractModelDoc;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public class GrupoEconomico extends AbstractModelDoc {
	@JsonAlias({ "GerenteConta", "gerenteConta" })
	private String gerenteConta;
	@JsonAlias({ "quantNegocios", "QuantNegocios" })
	private Double quantNegocios;
	@JsonAlias({ "dataUltimoNegocio", "DataUltimoNegocio" })
	private Date dataUltimoNegocio;
	@JsonAlias({ "parceriaPrimeiroNegocio", "ParceriaPrimeiroNegocio" })
	private String parceriaPrimeiroNegocio;

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
