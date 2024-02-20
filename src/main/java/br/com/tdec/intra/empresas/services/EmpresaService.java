package br.com.tdec.intra.empresas.services;

import java.util.List;
import java.util.Optional;

import com.vaadin.flow.data.provider.QuerySortOrder;

import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.empresas.model.Empresa;
import br.com.tdec.intra.inter.ServiceInter;

public class EmpresaService extends AbstractService implements ServiceInter<Empresa> {

	@Override
	public List<Empresa> findAllByCodigo(int offset, int count, List<QuerySortOrder> sortOrders, Optional<Void> filter,
			String search) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Empresa findByCodigo(String codigo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Empresa findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Empresa findByUnid(String unid) {
		// TODO Auto-generated method stub
		return null;
	}

}
