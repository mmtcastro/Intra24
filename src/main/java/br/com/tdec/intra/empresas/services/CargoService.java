package br.com.tdec.intra.empresas.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;

import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.config.WebClientConfig;
import br.com.tdec.intra.empresas.model.Cargo;

@Service
public class CargoService extends AbstractService {

	public CargoService(WebClientConfig webClientConfig) {
		super(webClientConfig);
	}

	public List<Cargo> findAllByCodigo(int offset, int count, List<QuerySortOrder> sortOrders, Optional<Void> filter,
			String search) {
		List<Cargo> ret = new ArrayList<Cargo>();
		count = 50; // nao consegui fazer funcionar o limit automaticamente.
		String direction = "";
		if (sortOrders != null) {
			for (QuerySortOrder sortOrder : sortOrders) {
				System.out.println("--- Sorting ----");
				System.out.println("Sorted: " + sortOrder.getSorted());
				System.out.println("Direction:  " + sortOrder.getDirection());
			}
			if (sortOrders.size() > 0) {
				if (sortOrders.get(0).getDirection() != null && sortOrders.get(0).getDirection().equals("ASCENDING")) {
					direction = "&direction=asc";
				} else {
					direction = "&direction=desc";
				}
			}
		}

		ret = webClient.get()
				.uri("/lists/Cargos?dataSource=empresasscope&count=" + count + direction + "&column=Codigo&start="
						+ offset + "&startsWith=" + search)
				.header("Authorization", "Bearer " + token).retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<Cargo>>() {
				})//
				.block();

		return ret;
	}

}
