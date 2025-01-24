package br.com.tdec.intra.empresas.services;

import org.springframework.stereotype.Service;

import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.empresas.model.TipoEmpresa;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Service
public class TipoEmpresaService extends AbstractService<TipoEmpresa> {

	public TipoEmpresaService() {
		super(TipoEmpresa.class);
	}

}
