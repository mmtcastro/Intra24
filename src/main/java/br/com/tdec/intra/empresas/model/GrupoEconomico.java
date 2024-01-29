package br.com.tdec.intra.empresas.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAlias;

import br.com.tdec.intra.abs.AbstractModelDoc;
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
}
