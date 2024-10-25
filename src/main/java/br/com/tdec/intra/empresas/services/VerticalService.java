package br.com.tdec.intra.empresas.services;

import org.springframework.stereotype.Service;

import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.empresas.model.Vertical;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Service
public class VerticalService extends AbstractService<Vertical> {

	public VerticalService() {
		super(Vertical.class);
	}

}
