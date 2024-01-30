package br.com.tdec.intra.empresas.services;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;

import br.com.tdec.intra.abs.AbstractModelDoc;
import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.config.WebClientConfig;

@Service
public class CargoService extends AbstractService {

	public CargoService(WebClientConfig webClientConfig) {
		super(webClientConfig);
	}

	@Override
	protected Collection<AbstractModelDoc> findAllByCodigo(int offset, int limit, List<QuerySortOrder> sortOrders,
			Optional<Void> filter, String searchText) {
		// TODO Auto-generated method stub
		return null;
	}

}
