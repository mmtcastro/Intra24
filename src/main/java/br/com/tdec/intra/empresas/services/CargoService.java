package br.com.tdec.intra.empresas.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;

import com.vaadin.flow.data.provider.QuerySortOrder;

import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.empresas.model.Cargo;
import br.com.tdec.intra.services.PostResponse;
import lombok.Getter;
import lombok.Setter;

//@Service
@Getter
@Setter
public class CargoService extends AbstractService {
//	protected final WebClient webClient;
//	protected String token;
//	protected final String scope = "empresas";
//
//	public CargoService(WebClientConfig webClientConfig) {
//		this.webClient = webClientConfig.getWebClient();
//		this.token = webClientConfig.getToken();
//
//	}

	public CargoService() {
		super();
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
				.uri("/lists/Cargos?dataSource=empresas&count=" + count + direction + "&column=Codigo&start=" + offset
						+ "&startsWith=" + search)
				.header("Authorization", "Bearer " + user.getToken()).retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<Cargo>>() {
				})//
				.block();

		return ret;
	}

	public Cargo findByUnid(String unid) {
		Cargo cargo = null;
		try {
			cargo = webClient.get()
					.uri("/document/" + unid + "?dataSource=" + scope
							+ "&computeWithForm=false&richTextAs=markdown&mode=default")
					.header("Authorization", "Bearer " + user.getToken()).retrieve().bodyToMono(Cargo.class).block();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cargo;

	}

	public PostResponse save(Cargo cargo) {
		return null;
	}

	public void delete(Cargo cargo) {
		// TODO Auto-generated method stub

	}
}
