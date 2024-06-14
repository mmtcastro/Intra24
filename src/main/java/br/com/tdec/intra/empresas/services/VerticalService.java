package br.com.tdec.intra.empresas.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

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
			// Verificar se a meta foi carregada
			if (ret != null && ret.getMeta() != null) {
				System.out.println("Meta unid: " + ret.getMeta().getUnid());
			} else {
				System.out.println("Meta is null");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

//	public Vertical findByCodigo(String codigo) {
//		Vertical ret = null;
//		List<Vertical> verticais = webClient.get()
//				.uri("/lists/Verticais?dataSource=" + scope + "&column=Codigo&startsWith=" + codigo)
//				.header("Authorization", "Bearer " + getUser().getToken()).retrieve()
//				.bodyToMono(new ParameterizedTypeReference<List<Vertical>>() {
//				})//
//				.block();
//		if (verticais.size() > 0) {
//			ret = verticais.get(0);
//		}
//		return ret;
//
//	}

//	public PostResponse save(Vertical vertical) {
//		PostResponse ret = new PostResponse();
//		ObjectMapper objectMapper = new ObjectMapper();
//		objectMapper.registerModule(new JavaTimeModule());
//		String json = "";
//		try {
//			json = objectMapper.writeValueAsString(vertical);
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
//		System.out.println(json);
//		try {
//			webClient.get().uri("/document?dataSource=" + scope)
//					.header("Authorization", "Bearer " + getUser().getToken()).retrieve().bodyToMono(Vertical.class)
//					.block();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return ret;
//	}
//
//	public String update(Vertical vertical) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	public DeleteResponse delete(String unid) {
		System.out.println("Delete unid: " + unid);
		DeleteResponse deleteResponse = webClient.delete()
				.uri("/document/" + unid + "?dataSource=" + scope + "&mode=" + mode)
				.header("Authorization", "Bearer " + getUser().getToken()).retrieve().bodyToMono(DeleteResponse.class)
				.block();
		return deleteResponse;
	}

	public SaveResponse save(Vertical model) {
		SaveResponse saveResponse = webClient.post().uri("/document?dataSource=" + scope)
				.header("Content-Type", "application/json")//
				.header("Authorization", "Bearer " + getUser().getToken())//
				.body(Mono.just(model), Vertical.class).retrieve()//
				.bodyToMono(SaveResponse.class).block();
		return saveResponse;
	}

	public SaveResponse update(Vertical model) {
		SaveResponse saveResponse = webClient.put().uri("/document?dataSource=" + scope)
				.header("Content-Type", "application/json")//
				.header("Authorization", "Bearer " + getUser().getToken())//
				.body(Mono.just(model), Vertical.class).retrieve()//
				.bodyToMono(SaveResponse.class).block();
		return saveResponse;
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

	@Override
	public DeleteResponse delete(Vertical model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vertical createModel() {
		return new Vertical();
	}

	@Override
	public Vertical findByCodigo(String unid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SaveResponse put(Vertical model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SaveResponse patch(Vertical model) {
		// TODO Auto-generated method stub
		return null;
	}

}
