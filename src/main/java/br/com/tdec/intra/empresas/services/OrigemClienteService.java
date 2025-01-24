package br.com.tdec.intra.empresas.services;

import org.springframework.stereotype.Service;

import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.empresas.model.OrigemCliente;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Service
public class OrigemClienteService extends AbstractService<OrigemCliente> {

	public OrigemClienteService() {
		super(OrigemCliente.class);
	}

}
