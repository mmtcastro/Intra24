package br.com.tdec.intra.abs;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Service
public abstract class AbstractService {
	protected WebClient webClient;
	protected String token;
	protected String scope;

//	public List<AbstractModelDoc> findAllByCodigo(int offset, int count, List<QuerySortOrder> sortOrders,
//			Optional<Void> filter, String search) {
//		List<AbstractModelDoc> ret = new ArrayList<AbstractModelDoc>();
//		count = 50; // nao consegui fazer funcionar o limit automaticamente.
//		String direction = "";
//		if (sortOrders != null) {
//			for (QuerySortOrder sortOrder : sortOrders) {
//				System.out.println("--- Sorting ----");
//				System.out.println("Sorted: " + sortOrder.getSorted());
//				System.out.println("Direction:  " + sortOrder.getDirection());
//			}
//			if (sortOrders.size() > 0) {
//				if (sortOrders.get(0).getDirection() != null && sortOrders.get(0).getDirection().equals("ASCENDING")) {
//					direction = "&direction=asc";
//				} else {
//					direction = "&direction=desc";
//				}
//			}
//		}
//
//		ret = webClient.get()
//				.uri("/lists/Verticais?dataSource=" + scope + "&count=" + count + direction + "&column=Codigo&start="
//						+ offset + "&startsWith=" + search)
//				.header("Authorization", "Bearer " + token).retrieve()
//				.bodyToMono(new ParameterizedTypeReference<List<AbstractModelDoc>>() {
//				})//
//				.block();
//
//		return ret;
//	}
//
//	public AbstractModelDoc findByUnid(String unid) {
//		AbstractModelDoc AbstractModelDoc = null;
//		try {
//			AbstractModelDoc = webClient.get()
//					.uri("/document/" + unid + "?dataSource=" + scope
//							+ "&computeWithForm=false&richTextAs=markdown&mode=default")
//					.header("Authorization", "Bearer " + token).retrieve().bodyToMono(AbstractModelDoc.class).block();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return AbstractModelDoc;
//	}
//
//	public PostResponse save(AbstractModelDoc AbstractModelDoc) {
//		PostResponse ret = new PostResponse();
//		ObjectMapper objectMapper = new ObjectMapper();
//		objectMapper.registerModule(new JavaTimeModule());
//		String json = "";
//		try {
//			json = objectMapper.writeValueAsString(AbstractModelDoc);
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(json);
////		try {
////			AbstractModelDoc = webClient.get()
////					.uri("/document/" + unid + "?dataSource=" + scope
////							+ "&computeWithForm=false&richTextAs=markdown&mode=default")
////					.header("Authorization", "Bearer " + token).retrieve().bodyToMono(Cargo.class).block();
////		} catch (Exception e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//		return ret;
//	}
//
//	public String update(AbstractModelDoc AbstractModelDoc) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public String delete(String unid) {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
