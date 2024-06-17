package br.com.tdec.intra.abs;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.provider.QuerySortOrder;

import br.com.tdec.intra.config.WebClientService;
import br.com.tdec.intra.directory.model.User;
import br.com.tdec.intra.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Service
public abstract class AbstractService<T extends AbstractModelDoc> {

	protected WebClientService webClientService;
	protected WebClient webClient;
	protected String scope;
	protected String mode; // usado do Domino Restapi para definir se pode ou não deletar e rodar agentes
	protected T model;
	protected Class<T> modelType;

	public AbstractService(Class<T> modelType) {
		scope = Utils.getScopeFromClass(this.getClass());
		this.mode = "default"; // tem que trocar para DQL ou outro mode caso necessário. Esta aqui para //
								// simplificar.
		this.modelType = modelType;
		this.model = createModel();
	}

	@Autowired
	public void setWebClientService(WebClientService webClientService) {
		this.webClientService = webClientService;
		this.webClient = webClientService.getWebClient();
	}

	protected T createModel() {
		try {
			return modelType.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Nao foi possivel criar o modelo - " + modelType, e);
		}
	}

	public abstract T findByUnid(String unid);

	public abstract T findByCodigo(String unid);

	public abstract List<T> findAllByCodigo(int offset, int count, List<QuerySortOrder> sortOrders,
			Optional<Void> filter, String search);

	public abstract SaveResponse save(T model);

	public abstract SaveResponse put(T model);

	public abstract SaveResponse patch(T model);

	// public abstract DeleteResponse delete(T model);

	// public abstract DeleteResponse delete(String unid);

	public DeleteResponse delete(String unid) {
		DeleteResponse deleteResponse = new DeleteResponse();
		try {
			deleteResponse = webClient.delete().uri("/document/" + unid + "?dataSource=" + scope + "&mode=" + mode)
					.header("Content-Type", "application/json")//
					.header("Authorization", "Bearer " + getUser().getToken()).retrieve()
					.bodyToMono(DeleteResponse.class).block();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return deleteResponse;
	}

	protected User getUser() {
		User user = null;
		try {
			user = (User) UI.getCurrent().getSession().getAttribute("user");
			return user;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	@Getter
	@Setter
	@ToString
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class SaveResponse {
		// Inicio em caso de erro
		@JsonProperty("details")
		private String details;
		@JsonProperty("message")
		private String message;
		@JsonProperty("status")
		private String status; // 500, 403, 406
		// Fim em caso de erro
		@JsonProperty("@meta")
		private Meta meta;
		@JsonProperty("Id")
		private String id;
		@JsonProperty("Codigo")
		private String codigo;
		@JsonProperty("Descricao")
		private String descricao;
		@JsonProperty("Form")
		private String form;
		@JsonProperty("@warnings")
		private List<String> warnings;
	}

	@Getter
	@Setter
	@ToString
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class DeleteResponse {
		@JsonProperty("statusText")
		private String statusCode;
		@JsonProperty("status")
		private String status;
		@JsonProperty("message")
		private String message;
		@JsonProperty("unid")
		private String unid;
	}

	@Getter
	@Setter
	@ToString
	@JsonIgnoreProperties(ignoreUnknown = true)
	/**
	 * O Restapi retorna dois metas. Um deles está em AbstractModelDoc
	 * 
	 */
	public static class Meta {
		@JsonProperty("noteid")
		protected int noteId;
		@JsonProperty("unid")
		protected String unid;
		@JsonProperty("created")
		protected String created;
		@JsonProperty("lastmodified")
		protected String lastModified;
		@JsonProperty("lastaccessed")
		protected String lastAccessed;
		@JsonProperty("lastmodifiedinfile")
		protected String lastModifiedInFile;
		@JsonProperty("addedtofile")
		protected String addedToFile;
		@JsonProperty("noteclass")
		protected String[] noteClass;
		@JsonProperty("unread")
		protected boolean unread;
		@JsonProperty("editable")
		protected boolean editable;
		@JsonProperty("revision")
		protected String revision;
		@JsonProperty("etag")
		protected String etag;
		@JsonProperty("size")
		protected int size;
		@JsonProperty("warnings")
		protected List<String> warnings;
	}

//	public T findByUnid(String unid) {
//		T ret = null;
//		try {
//			ret = webClient.get()
//					.uri("/document/" + unid + "?dataSource=" + scope
//							+ "&computeWithForm=false&richTextAs=markdown&mode=default")
//					.header("Authorization", "Bearer " + getUser().getToken()).retrieve().bodyToMono(modelType).block();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return ret;
//	}

//	public List<T> findAllByCodigo(int offset, int count, List<QuerySortOrder> sortOrders, Optional<Void> filter,
//			String search) {
//		List<T> ret = new ArrayList<T>();
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
//				.uri("/lists/_intraCodigos?dataSource=" + scope + "&count=" + count + direction
//						+ "&column=Codigo&start=" + offset + "&startsWith=" + search)
//				.header("Authorization", "Bearer " + getUser().getToken()).retrieve()
//				.bodyToMono(new ParameterizedTypeReference<List<T>>() {
//				})//
//				.block();
//
////		String form = this.getClass().getSimpleName().replace("Service", "");
////		ret = webClient.get()
////				.uri("/lists/_intraCodigos?dataSource=" + scope + "&documents=true&key=" + search + "&key=" + form
////						+ "&scope=documents")
////				.header("Authorization", "Bearer " + getUser().getToken()).retrieve()
////				.bodyToMono(new ParameterizedTypeReference<List<T>>() {
////				})//
////				.block();
//
//		return ret;
//	}

//	public T findByCodigo(String codigo) {
//		T model = null;
//		try {
//			String form = this.getClass().getSimpleName().replace("Service", "");
//			List<T> models = webClient.get()
//					.uri("/lists/_intraCodigos?dataSource=" + scope + "&documents=true&key=" + codigo + "&key=" + form
//							+ "&scope=documents")
//					.header("Authorization", "Bearer " + getUser().getToken()).retrieve()
//					.bodyToMono(new ParameterizedTypeReference<List<T>>() {
//					})//
//					.block();
//			if (models.size() > 0) {
//				model = models.get(0);
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return model;
//	}

}
