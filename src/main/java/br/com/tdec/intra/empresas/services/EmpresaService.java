package br.com.tdec.intra.empresas.services;

import org.springframework.stereotype.Service;

import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.empresas.model.Empresa;
import br.com.tdec.intra.services.Response;

@Service
public class EmpresaService extends AbstractService<Empresa> {

	public EmpresaService() {
		super();
	}

	public Response<Empresa> findByCnpj(String cnpj) {
		if (cnpj == null || cnpj.trim().isEmpty()) {
			return new Response<>(null, "CNPJ não pode ser nulo ou vazio.", 400, false);
		}
		String uri = "/lists/2E9D13D5EDD5A29A832580EC006BAE4C?mode=" + mode + "&dataSource=empresas&key=" + cnpj
				+ "&count=1&direction=asc&start=0";
		return buscarPrimeiroDaLista(uri);
	}

}
