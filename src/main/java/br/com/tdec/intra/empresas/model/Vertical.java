package br.com.tdec.intra.empresas.model;

import br.com.tdec.intra.abs.AbstractModelDoc;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public class Vertical extends AbstractModelDoc {

	public Vertical() {
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
