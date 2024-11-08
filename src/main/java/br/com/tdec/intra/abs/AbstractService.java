package br.com.tdec.intra.abs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.provider.QuerySortOrder;

import br.com.tdec.intra.config.WebClientService;
import br.com.tdec.intra.directory.model.User;
import br.com.tdec.intra.utils.Utils;
import br.com.tdec.intra.utils.exceptions.CustomWebClientException;
import br.com.tdec.intra.utils.exceptions.ErrorResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import reactor.core.publisher.Mono;

@Getter
@Setter
@Service
public abstract class AbstractService<T extends AbstractModelDoc> {

	protected WebClientService webClientService;
	protected WebClient webClient;
	@Autowired
	private ObjectMapper objectMapper; // jackson datas
	protected String scope;
	protected String mode; // usado do Domino Restapi para definir se pode ou não deletar e rodar agentes
	protected T model;
	protected Class<T> modelClass;

	public AbstractService(Class<T> modelClass) {
		scope = Utils.getScopeFromClass(this.getClass());
		this.mode = "default"; // tem que trocar para DQL ou outro mode caso necessário. Esta aqui para //
								// simplificar.
		this.modelClass = modelClass;
		this.model = createModel();
	}

	@Autowired
	public void setWebClientService(WebClientService webClientService) {
		this.webClientService = webClientService;
		this.webClient = webClientService.getWebClient();
	}

	protected T createModel() {
		try {
			return modelClass.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Nao foi possivel criar o modelo - " + modelClass, e);
		}
	}

	public Response<T> findByUnid(String unid) {
		Response<T> response = null;
		try {
			// Captura a resposta do WebClient, lidando com erros
			String rawResponse = webClient.get()
					.uri("/document/" + unid + "?dataSource=" + scope + "&computeWithForm=false"
							+ "&richTextAs=html&mode=" + mode)
					.header("Content-Type", "application/json; charset=UTF-8")//
					.header("Accept-Charset", "UTF-8")//
					.header("Authorization", "Bearer " + getUser().getToken()).retrieve()
					.onStatus(HttpStatusCode::isError,
							clientResponse -> clientResponse.bodyToMono(ErrorResponse.class).flatMap(errorResponse -> {
								// Lança CustomWebClientException, que é um Throwable
								return Mono.<Throwable>error(new CustomWebClientException(errorResponse.getMessage(),
										errorResponse.getStatus(), errorResponse));
							}))
					.bodyToMono(String.class) // Captura a resposta como String bruta
					.block(); // Bloqueia e espera a resposta

			// Verifica se a resposta está vazia antes de desserializar
			if (rawResponse == null || rawResponse.isEmpty()) {
				return new Response<>(null, "Resposta vazia da Web API.", 204, false);
			}
			// Exibe a resposta bruta no console para análise
			System.out.println("Resposta bruta da Web API: " + rawResponse);

			// Desserializa a resposta bruta manualmente para o tipo esperado (T)
			// ObjectMapper objectMapper = new ObjectMapper(); // cuidado para nao pegar o
			// objectMapper padrao
			T model = objectMapper.readValue(rawResponse, modelClass);

			// Retorna o modelo em um Response de sucesso
			response = new Response<>(model, "Documento carregado com sucesso.", 200, true);

		} catch (CustomWebClientException e) {
			ErrorResponse error = e.getErrorResponse();
			System.out.println("Erro ao tentar buscar documento. Código HTTP: " + error.getStatus());
			System.out.println("Mensagem de erro: " + error.getMessage());
			System.out.println("Detalhes do erro: " + error.getDetails());

			// Monta a resposta de erro com base no status e mensagem do erro capturado
			response = new Response<>(null, error.getMessage() + " - " + error.getDetails(), error.getStatus(), false);

		} catch (WebClientResponseException e) {
			HttpStatusCode statusCode = e.getStatusCode();
			System.out.println("Erro ao tentar buscar documento. Código HTTP: " + statusCode);
			System.out.println("Mensagem de erro: " + e.getMessage());

			// Monta a resposta de erro padrão
			response = new Response<>(null, "Erro ao buscar documento: " + e.getMessage(), statusCode.value(), false);
		} catch (Exception e) {
			e.printStackTrace();
			response = new Response<>(null, "Erro inesperado ao buscar documento.", 500, false);
		}

		return response;
	}

	/**
	 * Esta funcao retorna um array. Como só pode existir um codigo para cada tipo
	 * de form, retorno o primeiro da List<String>
	 * 
	 * @param codigo
	 * @return
	 */
	public Response<T> findByCodigo(String codigo) {

		// Verifica se o código é nulo ou vazio
		if (codigo == null || codigo.trim().isEmpty()) {
			return new Response<>(null, "Código não pode ser nulo ou vazio.", 400, false);
		}

		Response<T> response = null;
		try {
			// Define o valor de `form` usando o nome simples da classe do modelo
			String form = modelClass.getSimpleName(); // Exemplo: "Vertical"

			// Monta a URI com o código e form
			String uri = String.format("/lists/_intraCodigos?mode=default&dataSource=empresas&key=%s&key=%s", codigo,
					form);

			// Faz a requisição e captura a resposta
			String rawResponse = webClient.get().uri(uri)//
					.header("Content-Type", "application/json; charset=UTF-8")
					.header("Authorization", "Bearer " + getUser().getToken()).retrieve()
					.onStatus(HttpStatusCode::isError,
							clientResponse -> clientResponse.bodyToMono(ErrorResponse.class).flatMap(errorResponse -> {
								return Mono.<Throwable>error(new CustomWebClientException(errorResponse.getMessage(),
										errorResponse.getStatus(), errorResponse));
							}))
					.bodyToMono(String.class) // Recebe a resposta como String
					.block();

			// Exibe a resposta bruta no console para análise
			System.out.println("Resposta bruta da Web API: " + rawResponse);

			// Desserializa como lista e captura apenas o primeiro item
			List<T> resultList = objectMapper.readValue(rawResponse,
					objectMapper.getTypeFactory().constructCollectionType(List.class, modelClass));

			if (!resultList.isEmpty()) {
				T resultModel = resultList.get(0);
				response = new Response<>(resultModel, "Documento carregado com sucesso.", 200, true);
			} else {
				response = new Response<>(null, "Nenhum documento encontrado.", 404, false);
			}

		} catch (CustomWebClientException e) {
			ErrorResponse error = e.getErrorResponse();
			System.out.println("Erro ao tentar buscar documento. Código HTTP: " + error.getStatus());
			System.out.println("Mensagem de erro: " + error.getMessage());
			System.out.println("Detalhes do erro: " + error.getDetails());

			response = new Response<>(null, error.getMessage() + " - " + error.getDetails(), error.getStatus(), false);

		} catch (WebClientResponseException e) {
			HttpStatusCode statusCode = e.getStatusCode();
			System.out.println("Erro ao tentar buscar documento. Código HTTP: " + statusCode);
			System.out.println("Mensagem de erro: " + e.getMessage());

			response = new Response<>(null, "Erro ao buscar documento: " + e.getMessage(), statusCode.value(), false);

		} catch (Exception e) {
			e.printStackTrace();
			response = new Response<>(null, "Erro inesperado ao buscar documento.", 500, false);
		}

		return response;
	}

	public SaveResponse save(T model) {
		SaveResponse saveResponse = null;
		try {
			String rawResponse = "erro rawRepsonse";
			boolean isNew = model.getMeta() == null;
			String requestBodyJson = objectMapper.writeValueAsString(model);
			System.out.println("JSON a ser enviado: " + requestBodyJson);
			if (isNew) {
				rawResponse = webClient.post().uri("/document?dataSource=" + scope + "&richTextAs=mime")//
						.header("Accept", "application/json")//
						.header("Content-Type", "application/json")
						.header("Authorization", "Bearer " + getUser().getToken())
						.body(Mono.just(model), model.getClass()).retrieve()
						.onStatus(HttpStatusCode::isError, clientResponse -> clientResponse
								.bodyToMono(ErrorResponse.class).flatMap(errorResponse -> {
									int statusCode = errorResponse.getStatus();
									String message = errorResponse.getMessage();
									String details = errorResponse.getDetails();

									if (statusCode == 403) {
										return Mono.error(new CustomWebClientException("Sem permissão: " + message,
												statusCode, errorResponse));
									} else if (statusCode == 406) {
										return Mono.error(new CustomWebClientException(
												"Operação não suportada: " + message, statusCode, errorResponse));
									} else if (statusCode == 500) {
										return Mono.error(new CustomWebClientException("Erro no servidor: " + message,
												statusCode, errorResponse));
									} else {
										return Mono.error(new CustomWebClientException("Erro desconhecido: " + message,
												statusCode, errorResponse));
									}
								}))
						.bodyToMono(String.class).block();
			} else {
				String unid = model.getMeta().getUnid();
				model.newRevision();
				System.out.println(unid + "Revision eh " + model.getRevision());
				System.out.println("Data eh " + model.getData());
				rawResponse = webClient.put()
						.uri("/document/" + unid + "?dataSource=" + scope + "&richTextAs=mime&mode=" + mode
								+ "&revision=" + model.getRevision())
						.header("Accept", "application/json").header("Content-Type", "application/json")
						.header("Authorization", "Bearer " + getUser().getToken())
						.body(Mono.just(model), model.getClass()).retrieve()
						.onStatus(HttpStatusCode::isError, clientResponse -> clientResponse
								.bodyToMono(ErrorResponse.class).flatMap(errorResponse -> {
									int statusCode = errorResponse.getStatus();
									String message = errorResponse.getMessage();
									String details = errorResponse.getDetails();

									if (statusCode == 403) {
										return Mono.error(new CustomWebClientException("Sem permissão: " + message,
												statusCode, errorResponse));
									} else if (statusCode == 406) {
										return Mono.error(new CustomWebClientException(
												"Operação não suportada: " + message, statusCode, errorResponse));
									} else if (statusCode == 500) {
										return Mono.error(new CustomWebClientException("Erro no servidor: " + message,
												statusCode, errorResponse));
									} else if (statusCode == 501) {
										return Mono.error(new CustomWebClientException(
												"Operação não implementada: " + message, statusCode, errorResponse));
									} else {
										return Mono.error(new CustomWebClientException("Erro desconhecido: " + message,
												statusCode, errorResponse));
									}
								}))
						.bodyToMono(String.class).block();
			}
			System.out.println("Raw eh " + rawResponse);
			saveResponse = objectMapper.readValue(rawResponse, SaveResponse.class);

		} catch (CustomWebClientException e) {
			ErrorResponse error = e.getErrorResponse();
			// Cria uma resposta SaveResponse personalizada com base no erro
			saveResponse = new SaveResponse();
			saveResponse.setStatus(String.valueOf(error.getStatus()));
			saveResponse.setMessage("Erro ao salvar documento: " + error.getMessage() + " - " + error.getDetails());

		} catch (WebClientResponseException e) {
			saveResponse = new SaveResponse();
			saveResponse.setStatus(String.valueOf(e.getStatusCode().value()));
			saveResponse.setMessage("Erro ao salvar documento: " + e.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			saveResponse = new SaveResponse();
			saveResponse.setStatus("500");
			saveResponse.setMessage("Erro inesperado ao salvar documento.");
		}

		return saveResponse;
	}

//	public SaveResponse save2(T model) {
//		SaveResponse saveResponse = null;
//		try {
//			String requestBodyJson = objectMapper.writeValueAsString(model);
//			System.out.println("JSON a ser enviado: " + requestBodyJson);
//			String rawResponse = webClient.post().uri("/document?dataSource=" + scope)
//					.header("Accept", "application/json").header("Content-Type", "application/json")
//					.header("Authorization", "Bearer " + getUser().getToken()).body(Mono.just(model), model.getClass())
//					.retrieve().onStatus(HttpStatusCode::isError,
//							clientResponse -> clientResponse.bodyToMono(ErrorResponse.class).flatMap(errorResponse -> {
//								int statusCode = errorResponse.getStatus();
//								String message = errorResponse.getMessage();
//								String details = errorResponse.getDetails();
//
//								if (statusCode == 403) {
//									return Mono.error(new CustomWebClientException("Sem permissão: " + message,
//											statusCode, errorResponse));
//								} else if (statusCode == 406) {
//									return Mono.error(new CustomWebClientException("Operação não suportada: " + message,
//											statusCode, errorResponse));
//								} else if (statusCode == 500) {
//									return Mono.error(new CustomWebClientException("Erro no servidor: " + message,
//											statusCode, errorResponse));
//								} else {
//									return Mono.error(new CustomWebClientException("Erro desconhecido: " + message,
//											statusCode, errorResponse));
//								}
//							}))
//					.bodyToMono(String.class).block();
//			System.out.println("Raw eh " + rawResponse);
//			saveResponse = objectMapper.readValue(rawResponse, SaveResponse.class);
//
//		} catch (CustomWebClientException e) {
//			ErrorResponse error = e.getErrorResponse();
//			// Cria uma resposta SaveResponse personalizada com base no erro
//			saveResponse = new SaveResponse();
//			saveResponse.setStatus(String.valueOf(error.getStatus()));
//			saveResponse.setMessage("Erro ao salvar documento: " + error.getMessage() + " - " + error.getDetails());
//
//		} catch (WebClientResponseException e) {
//			saveResponse = new SaveResponse();
//			saveResponse.setStatus(String.valueOf(e.getStatusCode().value()));
//			saveResponse.setMessage("Erro ao salvar documento: " + e.getMessage());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			saveResponse = new SaveResponse();
//			saveResponse.setStatus("500");
//			saveResponse.setMessage("Erro inesperado ao salvar documento.");
//		}
//
//		return saveResponse;
//	}
//
//	public SaveResponse put(T model) {
//		SaveResponse saveResponse = null;
//		try {
//			String unid = model.getMeta().getUnid();
//			model.newRevision();
//			System.out.println(unid + "Revision eh " + model.getRevision());
//			System.out.println("Data eh " + model.getData());
//			// Realiza a solicitação de PUT usando o `unid` na URL e o próprio `model` como
//			// corpo da requisição
//			String rawResponse = webClient.put()
//					.uri("/document/" + unid + "?dataSource=" + scope + "&richTextAs=mime&mode=html" + mode
//							+ "&revision=" + model.getRevision())
//					.header("Accept", "application/json").header("Content-Type", "application/json")
//					.header("Authorization", "Bearer " + getUser().getToken()).body(Mono.just(model), model.getClass())
//					.retrieve().onStatus(HttpStatusCode::isError,
//							clientResponse -> clientResponse.bodyToMono(ErrorResponse.class).flatMap(errorResponse -> {
//								int statusCode = errorResponse.getStatus();
//								String message = errorResponse.getMessage();
//								String details = errorResponse.getDetails();
//
//								if (statusCode == 403) {
//									return Mono.error(new CustomWebClientException("Sem permissão: " + message,
//											statusCode, errorResponse));
//								} else if (statusCode == 406) {
//									return Mono.error(new CustomWebClientException("Operação não suportada: " + message,
//											statusCode, errorResponse));
//								} else if (statusCode == 500) {
//									return Mono.error(new CustomWebClientException("Erro no servidor: " + message,
//											statusCode, errorResponse));
//								} else if (statusCode == 501) {
//									return Mono.error(new CustomWebClientException(
//											"Operação não implementada: " + message, statusCode, errorResponse));
//								} else {
//									return Mono.error(new CustomWebClientException("Erro desconhecido: " + message,
//											statusCode, errorResponse));
//								}
//							}))
//					.bodyToMono(String.class).block();
//
//			// Desserializa a resposta bruta manualmente para SaveResponse
//			System.out.println("Raw: " + rawResponse);
//			saveResponse = objectMapper.readValue(rawResponse, SaveResponse.class);
//
//		} catch (CustomWebClientException e) {
//			ErrorResponse error = e.getErrorResponse();
//			saveResponse = new SaveResponse();
//			saveResponse.setStatus(String.valueOf(error.getStatus()));
//			saveResponse.setMessage("Erro ao salvar documento: " + error.getMessage() + " - " + error.getDetails());
//
//		} catch (WebClientResponseException e) {
//			saveResponse = new SaveResponse();
//			saveResponse.setStatus(String.valueOf(e.getStatusCode().value()));
//			saveResponse.setMessage("Erro ao salvar documento: " + e.getMessage());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			saveResponse = new SaveResponse();
//			saveResponse.setStatus("500");
//			saveResponse.setMessage("Erro inesperado ao salvar documento.");
//		}
//
//		return saveResponse;
//	}

	public SaveResponse patch(String unid, T model) {
		SaveResponse patchResponse = null;

		try {
			String rawResponse = webClient.patch().uri("/document/" + unid + "?dataSource=" + scope)
					.header("Accept", "application/json").header("Content-Type", "application/json")
					.header("Authorization", "Bearer " + getUser().getToken()).body(Mono.just(model), model.getClass())
					.retrieve().onStatus(HttpStatusCode::isError,
							clientResponse -> clientResponse.bodyToMono(ErrorResponse.class).flatMap(errorResponse -> {
								int statusCode = errorResponse.getStatus();
								String message = errorResponse.getMessage();
								String details = errorResponse.getDetails();

								if (statusCode == 403) {
									return Mono.error(new CustomWebClientException("Sem permissão: " + message,
											statusCode, errorResponse));
								} else if (statusCode == 406) {
									return Mono.error(new CustomWebClientException("Operação não suportada: " + message,
											statusCode, errorResponse));
								} else if (statusCode == 500) {
									return Mono.error(new CustomWebClientException("Erro no servidor: " + message,
											statusCode, errorResponse));
								} else if (statusCode == 501) {
									return Mono.error(new CustomWebClientException("Não disponível: " + message,
											statusCode, errorResponse));
								} else {
									return Mono.error(new CustomWebClientException("Erro desconhecido: " + message,
											statusCode, errorResponse));
								}
							}))
					.bodyToMono(String.class).block();

			// Desserializa a resposta bruta para SaveResponse
			patchResponse = objectMapper.readValue(rawResponse, SaveResponse.class);

		} catch (CustomWebClientException e) {
			ErrorResponse error = e.getErrorResponse();
			// System.out.println("Erro ao atualizar documento. Código HTTP: " +
			// error.getStatus());
			// System.out.println("Mensagem de erro: " + error.getMessage());
			// System.out.println("Detalhes do erro: " + error.getDetails());

			// Cria um SaveResponse customizado com as informações do erro
			patchResponse = new SaveResponse();
			patchResponse.setStatus(String.valueOf(error.getStatus()));
			patchResponse.setMessage("Erro ao atualizar documento: " + error.getMessage() + " - " + error.getDetails());

		} catch (WebClientResponseException e) {
			System.out.println("Erro ao atualizar documento. Código HTTP: " + e.getStatusCode());
			patchResponse = new SaveResponse();
			patchResponse.setStatus(String.valueOf(e.getStatusCode().value()));
			patchResponse.setMessage("Erro ao atualizar documento: " + e.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			patchResponse = new SaveResponse();
			patchResponse.setStatus("500");
			patchResponse.setMessage("Erro inesperado ao atualizar documento.");
		}

		return patchResponse;
	}

	// public abstract DeleteResponse delete(T model);

	// public abstract DeleteResponse delete(String unid);

	public DeleteResponse delete(AbstractModelDoc model) {
		DeleteResponse deleteResponse = null;
		try {
			// Captura a resposta do WebClient, lidando com erros
			String rawResponse = webClient.delete()
					.uri("/document/" + model.getMeta().getUnid() + "?dataSource=" + scope + "&mode=" + mode)
					.header("Content-Type", "application/json")
					.header("Authorization", "Bearer " + getUser().getToken()).retrieve()
					.onStatus(HttpStatusCode::isError, // Verifica se o status é um erro
							clientResponse -> {
								int statusCode = clientResponse.statusCode().value();
								if (statusCode == 403) {
									return Mono.error(new WebClientResponseException("403 Sem Permissão", statusCode,
											"Forbidden", null, null, null));
								}
								return Mono.error(new WebClientResponseException("Erro desconhecido", statusCode,
										clientResponse.statusCode().toString(), null, null, null));
							})
					.bodyToMono(String.class) // Captura a resposta como String bruta
					.block(); // Bloqueia e espera a resposta

			// Exibe a resposta bruta no console para análise
			System.out.println("Resposta bruta da Web API: " + rawResponse);

			// Desserializa a resposta bruta manualmente para DeleteResponse
			// ObjectMapper objectMapper = new ObjectMapper();
			deleteResponse = objectMapper.readValue(rawResponse, DeleteResponse.class);

		} catch (WebClientResponseException e) {
			HttpStatusCode statusCode = e.getStatusCode();
			System.out.println("Erro ao tentar deletar documento. Código HTTP: " + statusCode);
			System.out.println("Mensagem de erro: " + e.getMessage());

			// Monta a resposta DeleteResponse com base no erro capturado
			deleteResponse = new DeleteResponse();
			deleteResponse.setStatus(String.valueOf(statusCode.value()));
			deleteResponse.setMessage("Erro ao tentar deletar documento: " + e.getMessage());

		} catch (Exception e) {
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
	@JsonInclude(JsonInclude.Include.NON_NULL) // Inclui apenas campos não nulos
	public static class DeleteResponse {
		@JsonProperty("statusText")
		private String statusText;
		@JsonProperty("statusCode")
		private Integer statusCode;
		@JsonDeserialize(using = StatusDeserializer.class)
		@JsonProperty("status")
		private String status;
		@JsonProperty("message")
		private String message;
		@JsonProperty("unid")
		private String unid;
		@JsonProperty("details")
		private String details;
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

	/**
	 * Tive que criar um deserializer especial, pois a resposta do RestAPI para o
	 * campo status pode ser String no caso de 200 "OK" ou pode ser um integer no
	 * caso de erro.
	 * 
	 */
	public static class StatusDeserializer extends JsonDeserializer<String> {

		@Override
		public String deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {

			// Verifica o tipo do valor e converte de acordo
			if (p.getCurrentToken().isNumeric()) {
				// Se for numérico, converte para String
				return String.valueOf(p.getIntValue());
			} else if (p.getCurrentToken().isScalarValue()) {
				// Se for uma string ou outro valor escalar, trata como texto
				return p.getText();
			} else {
				// Tratamento para tipo inesperado
				ctxt.reportInputMismatch(StatusDeserializer.class, "Tipo inesperado para o campo 'status'");
				return null;
			}
		}
	}

	public class StatusCodeDeserializer extends JsonDeserializer<Integer> {
		@Override
		public Integer deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			JsonToken token = p.getCurrentToken();

			if (token == JsonToken.VALUE_NUMBER_INT) {
				// Caso seja um número inteiro, retorna o valor como está
				return p.getIntValue();
			} else if (token == JsonToken.VALUE_STRING) {
				// Caso seja uma string, tenta converter para um número se possível
				String text = p.getText();
				if ("OK".equalsIgnoreCase(text)) {
					return 200; // Se for "OK", atribui 200 como valor de sucesso
				} else {
					// Caso a string não seja um status conhecido, lança uma exceção
					ctxt.reportInputMismatch(Integer.class, "Status code string '{0}' is not valid", text);
					return null;
				}
			} else {
				// Se o tipo do token for inesperado, lança uma exceção
				ctxt.reportInputMismatch(Integer.class, "Expected a string or number for status code");
				return null;
			}
		}
	}

	public List<T> findAllByCodigo(int offset, int count, List<QuerySortOrder> sortOrders, String search,
			Class<T> model) {
		List<T> ret = new ArrayList<>();
		System.out.println("Vista é " + Utils.getListaNameFromModelName(model.getSimpleName()));

		try {
			count = 50; // Define um limite fixo para a contagem, ajustável conforme necessário
			String direction = "";
			if (sortOrders != null && !sortOrders.isEmpty()) {
				QuerySortOrder sortOrder = sortOrders.get(0); // Use o primeiro critério de ordenação
				if (sortOrder.getDirection() != null
						&& sortOrder.getDirection().toString().equalsIgnoreCase("ASCENDING")) {
					direction = "&direction=asc";
				} else {
					direction = "&direction=desc";
				}
			}

			// Realiza a requisição GET e captura a resposta como String
			String rawResponse = webClient.get()
					.uri("/lists/" + Utils.getListaNameFromModelName(model.getSimpleName()) + "?dataSource=" + scope
							+ "&count=" + count + direction + "&column=Codigo&start=" + offset + "&startsWith="
							+ search)
					.header("Authorization", "Bearer " + getUser().getToken())//
					.retrieve()//
					.onStatus(HttpStatusCode::isError, clientResponse -> {
						return clientResponse.bodyToMono(ErrorResponse.class).flatMap(errorResponse -> {
							// Verifica se o erro é um redirecionamento (302)
							if (clientResponse.statusCode().value() == 302) {
								return Mono.<Throwable>error(new CustomWebClientException(
										"Erro de acesso ao servidor RESTAPI", 302, errorResponse));
							}
							// Trata outros erros como 404 e demais status
							return Mono.<Throwable>error(new CustomWebClientException(errorResponse.getMessage(),
									errorResponse.getStatus(), errorResponse));
						});
					}).bodyToMono(String.class) // Captura como string bruta
					.block(); // Bloqueia e espera a resposta

			// Exibe a resposta bruta no console para análise
			// System.out.println("Resposta bruta da Web API: " + rawResponse);

			// Usa o ObjectMapper injetado para desserializar a lista
			JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, model);
			ret = objectMapper.readValue(rawResponse, listType);

		} catch (CustomWebClientException e) {
			// Lida com o erro customizado
			ErrorResponse error = e.getErrorResponse();
			System.err.println("Erro ao buscar lista. Código HTTP: " + error.getStatus());
			System.err.println("Mensagem de erro: " + error.getMessage());
			if (error.getStatus() == 302) {
				System.err.println("Erro de acesso ao servidor RESTAPI");
			}
		} catch (WebClientResponseException e) {
			// Lida com erros do WebClient
			System.err.println("Erro: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	@SuppressWarnings("hiding")
	@Getter
	@Setter
	public class Response<T> {
		private T model;
		private String message;
		private int status;
		private boolean success;

		public Response(T body, String message, int status, boolean success) {
			this.model = body;
			this.message = message;
			this.status = status;
			this.success = success;
		}

	}

}
