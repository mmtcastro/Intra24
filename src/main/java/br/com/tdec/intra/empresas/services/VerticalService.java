package br.com.tdec.intra.empresas.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.vaadin.flow.data.provider.QuerySortOrder;

import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.empresas.model.Vertical;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

@Getter
@Setter
@Service
public class VerticalService extends AbstractService<Vertical> {

	public VerticalService() {
		super(Vertical.class);
	}

	public List<Vertical> findAllByCodigo(int offset, int count, List<QuerySortOrder> sortOrders, Optional<Void> filter,
			String search) {
		List<Vertical> ret = new ArrayList<Vertical>();
		try {
			count = 50; // nao consegui fazer funcionar o limit automaticamente.
			String direction = "";
			if (sortOrders != null) {
				for (QuerySortOrder sortOrder : sortOrders) {
					System.out.println("--- Sorting ----");
					System.out.println("Sorted: " + sortOrder.getSorted());
					System.out.println("Direction:  " + sortOrder.getDirection());
				}
				if (sortOrders.size() > 0) {
					if (sortOrders.get(0).getDirection() != null
							&& sortOrders.get(0).getDirection().equals("ASCENDING")) {
						direction = "&direction=asc";
					} else {
						direction = "&direction=desc";
					}
				}
			}
			ret = webClient.get()
					.uri("/lists/Verticais?dataSource=" + scope + "&count=" + count + direction
							+ "&column=Codigo&start=" + offset + "&startsWith=" + search)
					.header("Authorization", "Bearer " + getUser().getToken()).retrieve()
					.bodyToMono(new ParameterizedTypeReference<List<Vertical>>() {
					})//
					.block();
		} catch (WebClientResponseException e) {
			System.err.println("Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
			throw e;
		}

		return ret;
	}

	public Vertical findByUnid(String unid) {
		Vertical ret = null;
		try {
			ret = webClient.get()
					.uri("/document/" + unid + "?dataSource=" + scope
							+ "&computeWithForm=false&richTextAs=markdown&mode=" + mode)
					.header("Authorization", "Bearer " + getUser().getToken()).retrieve().bodyToMono(Vertical.class)
					.block();
			// Verificar se a meta foi carregada
			if (ret != null && ret.getMeta() != null) {
				System.out.println("Meta unid: " + ret.getMeta().getUnid());
			} else {
				System.out.println("Meta is null");
			}
		} catch (WebClientResponseException e) {
			System.err.println("Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
			throw e;
		}
		return ret;
	}

	public Vertical findByCodigo(String codigo) {
		Vertical ret = null;
		try {
			List<Vertical> verticais = webClient.get()
					.uri("/lists/Verticais?dataSource=" + scope + "&column=Codigo&startsWith=" + codigo)
					.header("Authorization", "Bearer " + getUser().getToken()).retrieve()
					.bodyToMono(new ParameterizedTypeReference<List<Vertical>>() {
					})//
					.block();
			if (verticais.size() > 0) {
				ret = verticais.get(0);
			}
		} catch (WebClientResponseException e) {
			System.err.println("Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
			throw e;
		}
		return ret;

	}

	@Override
	public SaveResponse save(Vertical model) {
		System.out.println("Save: " + model.toString());
		try {
			SaveResponse saveResponse = webClient.post().uri("/document?dataSource=" + scope)
					.header("Accept: application/json")//
					.header("Content-Type", "application/json")//
					.header("Authorization", "Bearer " + getUser().getToken())//
					.body(Mono.just(model), Vertical.class).retrieve()//
					.bodyToMono(SaveResponse.class).block();
			return saveResponse;
		} catch (WebClientResponseException e) {
			System.err.println("Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
			throw e;
		}
	}

	public SaveResponse put(String unid) {
		try {
			SaveResponse saveResponse = webClient.put()
					.uri("/document/" + unid + "?richTextAs=mime&dataSource=" + scope + "&mode=" + scope)
					.header("Accept: application/json")//
					.header("Content-Type", "application/json")//
					.header("Authorization", "Bearer " + getUser().getToken())//
					.body(Mono.just(model), Vertical.class).retrieve()//
					.bodyToMono(SaveResponse.class).block();
			return saveResponse;
		} catch (WebClientResponseException e) {
			System.err.println("Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
			throw e;
		}
	}

	public SaveResponse patch(String unid) {
		try {
			SaveResponse saveResponse = webClient.patch()
					.uri("/document/" + unid + "?richTextAs=mime&dataSource=" + scope + "&mode=" + scope)
					.header("Accept: application/json")//
					.header("Content-Type", "application/json")//
					.header("Authorization", "Bearer " + getUser().getToken())//
					.body(Mono.just(model), Vertical.class).retrieve()//
					.bodyToMono(SaveResponse.class).block();
			return saveResponse;
		} catch (WebClientResponseException e) {
			System.err.println("Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
			throw e;
		}
	}

	@Override
	public Vertical createModel() {
		return new Vertical();
	}

}
