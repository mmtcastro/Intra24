package br.com.tdec.intra.empresas.services;

import org.springframework.stereotype.Service;

import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.empresas.model.TipoExcecaoTributaria;
import lombok.Getter;
import lombok.Setter;

@Service
@Getter
@Setter
public class TipoExcecaoTributariaService extends AbstractService<TipoExcecaoTributaria> {

	public TipoExcecaoTributariaService() {
		super();
	}

}
