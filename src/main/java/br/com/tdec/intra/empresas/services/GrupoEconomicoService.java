package br.com.tdec.intra.empresas.services;

import org.springframework.stereotype.Service;

import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.empresas.model.GrupoEconomico;
import lombok.Getter;
import lombok.Setter;

@Service
@Getter
@Setter
public class GrupoEconomicoService extends AbstractService<GrupoEconomico> {

	public GrupoEconomicoService() {
		super();
	}

}
