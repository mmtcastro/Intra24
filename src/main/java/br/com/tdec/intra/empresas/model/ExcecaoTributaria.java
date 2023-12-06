package br.com.tdec.intra.empresas.model;

import br.com.tdec.intra.abs.AbstractModelDoc;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExcecaoTributaria extends AbstractModelDoc {
	private String tipoReducao;
	private Double percReducao;
	private Boolean membroSuframa;
	private String inscricaoSuframa;
	private String ncm;
	private String suframa;
}
