package br.com.tdec.intra.empresas.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.vaadin.flow.data.provider.QuerySortOrder;

import br.com.tdec.intra.config.WebClientConfig;
import br.com.tdec.intra.empresas.model.Cargo;
import lombok.Getter;
import lombok.Setter;

@Service
@Getter
@Setter
public class VerticalService {
	protected final WebClient webClient;
	protected String token;

	public VerticalService(WebClientConfig webClientConfig) {
		this.webClient = webClientConfig.getWebClient();
		this.token = webClientConfig.getToken();
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
				.uri("/lists/Verticais?dataSource=empresasscope&count=" + count + direction + "&column=Codigo&start="
						+ offset + "&startsWith=" + search)
				.header("Authorization", "Bearer " + token).retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<Cargo>>() {
				})//
				.block();

		return ret;
	}
}
