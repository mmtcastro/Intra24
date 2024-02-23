package br.com.tdec.intra.empresas.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vaadin.flow.data.provider.QuerySortOrder;

import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.config.WebClientService;
import br.com.tdec.intra.empresas.model.Vertical;
import br.com.tdec.intra.services.PostResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Service
public class VerticalService extends AbstractService {

//	protected WebClient webClient = (WebClient) UI.getCurrent().getSession().getAttribute("webClient");
//	protected User user = (User) UI.getCurrent().getSession().getAttribute("user");
//	// protected String token;
//	protected final String scope = "empresas";

//	public VerticalService2() {
//		this.webClient = (WebClient) UI.getCurrent().getSession().getAttribute("webClient");
//		this.token = (String) UI.getCurrent().getSession().getAttribute("token");
//	}

	public VerticalService(WebClientService webClientService) {
		super(webClientService);
	}

	public List<Vertical> findAllByCodigo(int offset, int count, List<QuerySortOrder> sortOrders, Optional<Void> filter,
			String search) {
		List<Vertical> ret = new ArrayList<Vertical>();
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
				.uri("/lists/Verticais?dataSource=" + scope + "&count=" + count + direction + "&column=Codigo&start="
						+ offset + "&startsWith=" + search)
				.header("Authorization", "Bearer " + getUser().getToken()).retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<Vertical>>() {
				})//
				.block();

		return ret;
	}

	public Vertical findByUnid(String unid) {
		Vertical ret = null;
		try {
			ret = webClient.get()
					.uri("/document/" + unid + "?dataSource=" + scope
							+ "&computeWithForm=false&richTextAs=markdown&mode=default")
					.header("Authorization", "Bearer " + getUser().getToken()).retrieve().bodyToMono(Vertical.class)
					.block();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	public PostResponse save(Vertical vertical) {
		PostResponse ret = new PostResponse();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		String json = "";
		try {
			json = objectMapper.writeValueAsString(vertical);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(json);
//		try {
//			vertical = webClient.get()
//					.uri("/document/" + unid + "?dataSource=" + scope
//							+ "&computeWithForm=false&richTextAs=markdown&mode=default")
//					.header("Authorization", "Bearer " + token).retrieve().bodyToMono(Cargo.class).block();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return ret;
	}

	public String update(Vertical vertical) {
		// TODO Auto-generated method stub
		return null;
	}

	public String delete(String unid) {
		// TODO Auto-generated method stub
		return null;
	}
}
