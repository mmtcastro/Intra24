package br.com.tdec.intra.empresas.services;

import org.springframework.stereotype.Service;

import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.empresas.model.Empresa;

@Service
public class EmpresaService extends AbstractService<Empresa> {

	public EmpresaService() {
		super(Empresa.class);
	}

}
