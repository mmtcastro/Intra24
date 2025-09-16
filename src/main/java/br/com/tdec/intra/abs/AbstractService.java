package br.com.tdec.intra.abs;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.hilla.ApplicationContextProvider;

import br.com.tdec.intra.comum.services.ServiceLocator;
import br.com.tdec.intra.config.WebClientService;
import br.com.tdec.intra.directory.model.User;
import br.com.tdec.intra.services.Response;
import br.com.tdec.intra.utils.Utils;
import br.com.tdec.intra.utils.exceptions.CustomWebClientException;
import br.com.tdec.intra.utils.exceptions.ErrorResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import reactor.core.publisher.Mono;

@Getter
@Setter
public abstract class AbstractService<T extends AbstractModelDoc> extends Abstract {

	@Autowired
	protected WebClientService webClientService;
	@Autowired
	protected ServiceLocator serviceLocator;
	@Autowired
	protected ObjectMapper objectMapper; // jackson datas

	protected WebClient webClient;
	protected String scope;
	protected String form;
	protected String mode; // usado do Domino Restapi para definir se pode ou não deletar e rodar agentes
	protected T model;
	protected Class<T> modelClass;
	protected Integer totalCount;

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@SuppressWarnings("unchecked")
	public AbstractService() {
		this.modelClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		this.scope = Utils.getScopeFromClass(modelClass);
		this.mode = "default";
		this.model = createModel();
		this.form = model.getClass().getSimpleName();
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
		if (unid == null || unid.isBlank())
			return new Response<>(null, "Unid não pode ser nulo ou vazio.", 400, false);

		String uri = "/document/" + unid + "?dataSource=" + scope + "&computeWithForm=false&richTextAs=html&mode="
				+ mode;

		return getAndPopulaModelo(uri, true);
	}

	public Response<T> findById(String id) {
		if (id == null || id.isBlank())
			return new Response<>(null, "Id não pode ser nulo ou vazio.", 400, false);

		String uri = "/lists/_intraIds?mode=default&dataSource=" + scope //
				+ "&keyAllowPartial=false&documents=true&richTextAs=mime&key=" + id //
				+ "&scope=documents&count=1";

		return getAndPopulaModelo(uri, true);
	}

	public Response<T> findByCodigo(String codigo) {
		if (codigo == null || codigo.isBlank())
			return new Response<>(null, "Código não pode ser nulo ou vazio.", 400, false);
//		String uri = ("/lists/_intraCodigos?//"//
//				+ "mode=" + mode//
//				+ "&dataSource=" + scope //
//				+ "&keyAllowPartial=false&documents=false&richTextAs=mime&key=" + codigo //
//				+ "&key=" + form + "&count=1")//
//				.formatted();

		String uri = "/lists/_intraCodigos" //
				+ "?mode=" + mode //
				+ "&dataSource=" + scope //
				+ "&keyAllowPartial=false" //
				+ "&documents=true" //
				+ "&richTextAs=mime" //
				+ "&key=" + codigo //
				+ "&key=" + form //
				+ "&count=1";

		return getAndPopulaModelo(uri, true);
	}

	public SaveResponse saveOnlyParent(T model) {
		SaveResponse saveResponse = null;
		try {
			model.extrairCamposMultivalueGenerico(); // Extrai campos multivalorados
			String rawResponse = "erro rawRepsonse";
			boolean isNew = model.getMeta() == null;
			// String requestBodyJson = objectMapper.writeValueAsString(model);
			// 1) Monte o JSON final aceito pelo Domino
			ObjectNode payload = flattenForDomino(model);
			String json = objectMapper.writeValueAsString(payload);
			// System.out.println("JSON a ser enviado: " + requestBodyJson);
			System.out.println("JSON a ser enviado: " + json);
			if (isNew) {
				rawResponse = webClient.post().uri("/document?dataSource=" + scope + "&richTextAs=mime")//
						.header("Accept", "application/json")//
						.header("Content-Type", "application/json")
						.header("Authorization", "Bearer " + getUser().getToken())
						// .body(Mono.just(model), model.getClass())//
						.bodyValue(payload)//
						.retrieve()//
						.onStatus(HttpStatusCode::isError, clientResponse -> clientResponse
								.bodyToMono(ErrorResponse.class).flatMap(errorResponse -> {
									int statusCode = errorResponse.getStatus();
									String message = errorResponse.getMessage();

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
						// .body(Mono.just(model), model.getClass())//
						.bodyValue(payload)//
						.retrieve()//
						.onStatus(HttpStatusCode::isError, clientResponse -> clientResponse
								.bodyToMono(ErrorResponse.class).flatMap(errorResponse -> {
									int statusCode = errorResponse.getStatus();
									String message = errorResponse.getMessage();

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

			model.getLogger().info("Documento salvo com sucesso. Unid eh " + saveResponse.getMeta().getUnid());

//			// Se o salvamento do formulário foi bem-sucedido, salvar os anexos
//			if (saveResponse.getMeta() != null && saveResponse.getMeta().getUnid() != null) {
//				deleteAllAnexos(saveResponse.getMeta().getUnid());
//				saveAnexos(saveResponse.getMeta().getUnid());
//			}

			// Exclusão de anexos pendentes após o salvamento bem-sucedido
			if (saveResponse.isSuccess() && model.getAnexosParaExcluir() != null) {
				for (String fileName : model.getAnexosParaExcluir()) {
					FileResponse deleteResponse = deleteAnexo(model.getMeta().getUnid(), fileName);
					if (!deleteResponse.isDeleteSuccess()) {
						System.err.println("Erro ao excluir anexo: " + fileName + " - " + deleteResponse.getMessage());
					} else {
						System.out.println("Anexo excluído com sucesso: " + fileName);
					}
				}
				model.getAnexosParaExcluir().clear();
			}

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

	/** depois de gravar o parent, verificar se ele tem filhos e sincronizar */
	public SaveResponse save(T parent) {
		// 1) salva o pai (garante id e meta/revision)
		SaveResponse sr = saveOnlyParent(parent); // seu método atual (o mesmo save), mas sem cascatear
		if (!sr.isSuccess())
			return sr;

		// 2) sincroniza filhos
		SyncReport rep = syncChildren(parent);
		log.info("Children synced: created={}, updated={}, deleted={}, errors={}", rep.created, rep.updated,
				rep.deleted, rep.errors.size());
		if (!rep.errors.isEmpty()) {
			// opcional: agregar mensagens, avisar UI etc.
			log.warn("Sync errors: {}", rep.errors);
		}
		return sr;
	}

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
			System.out.println("delete - Resposta bruta da Web API: " + rawResponse);

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

	public FileResponse getAnexo(String unid, String fileName) {
		FileResponse response = new FileResponse();

		try {
			// Codificar o nome do arquivo para evitar problemas com caracteres especiais
			// String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
			String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replace("+", "%20");

			System.out.println("Nome do arquivo codificado: " + encodedFileName);

			// Requisição usando WebClient para obter o anexo
			response = webClient.get().uri("/attachments/" + unid + "/" + encodedFileName + "?dataSource=" + scope)
					.header("Authorization", "Bearer " + getUser().getToken()).accept(MediaType.ALL)
					.exchangeToMono(clientResponse -> {
						FileResponse fileResponse = new FileResponse();

						if (clientResponse.statusCode().is2xxSuccessful()) {
							MediaType mediaType = clientResponse.headers().contentType()
									.orElse(MediaType.APPLICATION_OCTET_STREAM);
							return clientResponse.bodyToMono(byte[].class).map(fileData -> {
								fileResponse.setFileData(fileData);
								fileResponse.setFileName(fileName);
								fileResponse.setMediaType(mediaType.toString());
								fileResponse.setStatusCode(200);
								return fileResponse;
							});
						} else {
							return clientResponse.bodyToMono(String.class).map(errorMessage -> {
								fileResponse.setMessage("Erro ao buscar anexo: " + errorMessage);
								fileResponse.setStatusCode(clientResponse.statusCode().value());
								return fileResponse;
							});
						}
					}).block();

			// Verificação adicional de falha
			if (response == null) {
				response = new FileResponse();
				response.setMessage("Erro desconhecido ao obter o anexo.");
				response.setStatusCode(500);
			}
		} catch (Exception e) {
			response.setMessage("Erro ao buscar anexo: " + e.getMessage());
			response.setStatusCode(500);
		}
		// System.out.println("GetAnexo FileResponse é " + response);
		return response;
	}

	public FileResponse uploadAnexo(String unid, String fieldName, String fileName, InputStream fileData) {
		FileResponse response = new FileResponse();

		try {
			System.out.println("Iniciando upload para o arquivo: " + fileName);
			String sanitizedFileName = Utils.sanitizeFileName(fileName);
			System.out.println("Nome do arquivo após sanitização: " + sanitizedFileName);

			// Converta o InputStream para ByteArrayResource
			byte[] fileBytes = fileData.readAllBytes();
			ByteArrayResource byteArrayResource = new ByteArrayResource(fileBytes) {
				@Override
				public String getFilename() {
					return fileName; // Retorna o nome do arquivo para o backend
				}
			};

			// Fazendo o upload usando WebClient
			response = webClient.post()
					.uri(uriBuilder -> uriBuilder.path("/attachments/" + unid).queryParam("fieldName", fieldName)
							.queryParam("dataSource", scope).build())
					.header("Authorization", "Bearer " + getUser().getToken())
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.body(BodyInserters.fromMultipartData("filename", byteArrayResource))
					.exchangeToMono(clientResponse -> {
						FileResponse fileResponse = new FileResponse();

						if (clientResponse.statusCode().is2xxSuccessful()) {
							return clientResponse.bodyToMono(String.class).map(successMessage -> {
								fileResponse.setMessage("Upload bem-sucedido: " + successMessage);
								fileResponse.setFileName(fileName);
								fileResponse.setStatusCode(200);
								fileResponse.setSuccess(true);
								return fileResponse;
							});
						} else {
							return clientResponse.bodyToMono(String.class).map(errorMessage -> {
								fileResponse.setMessage("Erro ao fazer upload: " + errorMessage);
								fileResponse.setStatusCode(clientResponse.statusCode().value());
								fileResponse.setSuccess(false);
								return fileResponse;
							});
						}
					}).block();

			if (response == null) {
				response = new FileResponse();
				response.setMessage("Erro desconhecido ao fazer upload.");
				response.setStatusCode(500);
				response.setSuccess(false);
			}

		} catch (Exception e) {
			response.setMessage("Erro ao fazer upload: " + e.getMessage());
			response.setStatusCode(500);
			response.setSuccess(false);
			e.printStackTrace();
		}

		return response;
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

		public boolean isSuccess() {
			return getMeta() != null && getMeta().getUnid() != null;
		}
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
	@JsonInclude(JsonInclude.Include.NON_NULL) // Inclui apenas campos não nulos
	public static class FileResponse {
		@JsonProperty("fileData")
		private byte[] fileData;

		@JsonProperty("mediaType")
		private String mediaType;

		@JsonProperty("fileName")
		private String fileName;

		@JsonProperty("status")
		private String status;

		@JsonProperty("statusCode")
		private Integer statusCode;

		@JsonProperty("statusText")
		private String statusText;

		@JsonProperty("message")
		private String message;

		@JsonProperty("unid")
		private String unid;

		@JsonProperty("success")
		private boolean success;

		@JsonProperty("details")
		private Map<String, String> details;

		@JsonProperty("Files") // Nome do campo conforme a resposta da API
		private List<String> fileNames;

//		// Getter e Setter para o campo $FILES
//		@JsonProperty("$FILES")
//		public List<String> getFileNames() {
//			return fileNames;
//		}

		// Método auxiliar para extrair o fileName de "details"
		public void extractFileNameFromDetails() {
			if (details != null && details.containsKey("0")) {
				try {
					String detailsJson = details.get("0");
					ObjectMapper mapper = new ObjectMapper();
					Map<String, String> detailsMap = mapper.readValue(detailsJson, new TypeReference<>() {
					});
					this.fileName = detailsMap.get("fileName");
				} catch (Exception e) {
					System.err.println("Erro ao processar 'details': " + e.getMessage());
				}
			}
		}

		public boolean isDeleteSuccess() {
			System.out.println("FileResponse status message: " + message);
			if (statusText != null) {
				return statusText.equals("OK");
			} else {
				return false;
			}
		}

		/**
		 * Copiando respostas do Domino RestAPI
		 * 
		 * @return
		 */
		public boolean isUploadSuccess() {
			if (status != null) {
				return status.equals("upload complete");
			} else {
				return false;
			}
		}
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

	public List<T> findAllByCodigo(int offset, int count, List<QuerySortOrder> sortOrders, String search,
			Class<T> model, boolean fulltextsearch) {
		List<T> resultados = new ArrayList<>();
		log.info("🔄 Atualizando Grid - searchText: '{}', fulltextsearch: {}", search, fulltextsearch);
		try {

			// Define valores padrão caso não haja ordenação
			String column = "Codigo"; // Nome padrão
			String direction = "asc"; // Direção padrão

			// Se houver ordenação na Grid
			if (sortOrders != null && !sortOrders.isEmpty()) {
				QuerySortOrder sortOrder = sortOrders.get(0); // Obtém a primeira ordenação
				// column = sortOrder.getSorted(); // Obtém o nome da coluna, mas nao uso pois
				// volta inicio com minuscula
				direction = sortOrder.getDirection() == SortDirection.ASCENDING ? "asc" : "desc"; // Converte para
																									// asc/desc
			}

			log.info("Ordenação recebida: Coluna = " + column + ", Direção = " + direction);

			// Sanitiza o termo de busca com base no tipo de pesquisa
			String searchQuery = "";

			if (search != null && !search.trim().isEmpty()) {
				search = search.trim(); // Remove espaços extras

				if (fulltextsearch) {
					searchQuery = "&ftSearchQuery=" + search;
					log.info("🔍 Pesquisa fulltext: {}", searchQuery);
				} else {
					searchQuery = "&startsWith=" + search;
					log.info("🔍 Pesquisa padrão: {}", searchQuery);
				}
			} else {
				searchQuery = "";
				log.info("🔍 Nenhuma pesquisa aplicada.");
			}

			// Monta a URL para requisição, incluindo o novo parâmetro `column`
			String uri = "/lists/%s?dataSource=%s&mode=%s&count=%d&start=%d&column=%s&direction=%s%s".formatted(
					Utils.getListaNameFromModelName(model.getSimpleName()), // Nome da lista
					scope, // Fonte de dados
					mode, // Modo de consulta
					count, // Quantidade de registros
					offset, // Offset (paginação)
					column, // Coluna usada na ordenação
					direction, // Direção da ordenação (asc/desc)
					searchQuery // Filtro startsWith ou ftSearchQuery, dependendo do `fulltextsearch`
			);

			log.info("URI do findAllByCodigo: " + uri);

			ClientResponse clientResponse = webClient.get().uri(uri)
					.header("Authorization", "Bearer " + getUser().getToken())//
					.exchangeToMono(Mono::just).blockOptional() // Evita possíveis erros se a resposta for nula
					.orElse(null);

			if (clientResponse == null) {
				log.error("Erro: Resposta nula do servidor");
				return resultados;
			}

			// Verifica o status da resposta antes de processar
			if (clientResponse.statusCode().isError()) {
				// log.error("Erro ao chamar API: HTTP " + clientResponse.statusCode().value());
//				String errorBody = clientResponse.bodyToMono(String.class).blockOptional().orElse("Sem detalhes.");			
//				log.error("Erro ao chamar API: HTTP {} - Corpo da resposta: {}", clientResponse.statusCode().value(),
//						errorBody);
				String errorBody = clientResponse.bodyToMono(String.class).block();
				log.error("❌ Erro ao chamar API: HTTP {} - corpo: {}", clientResponse.statusCode().value(), errorBody);
				return resultados;
			}

			// 📌 Obtém o corpo da resposta (JSON) com uma nova requisição para garantir o
			// conteúdo
			String rawResponse = webClient.get().uri(uri).header("Authorization", "Bearer " + getUser().getToken())
					.retrieve()//
					.bodyToMono(String.class)//
					.blockOptional()//
					.orElse("");

			// Loga status HTTP
//			/log.info("Status da resposta: " + clientResponse.statusCode());

			// Loga headers
//			clientResponse.headers().asHttpHeaders()
//					.forEach((key, value) -> log.info("Header: " + key + " = " + value));

			// Verifica se o corpo está vazio
			if (rawResponse.isBlank()) {
				log.error("Erro: Resposta da API vazia para a URI: " + uri);
				return resultados;
			}

			// log.info("rawResponse do findAllByCodigo: " + rawResponse);

			// Desserializa a resposta JSON para Lista<T>
			JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, model);
			resultados = objectMapper.readValue(rawResponse, listType);

			// Captura o cabeçalho `x-totalcount`
			String totalCountHeader = clientResponse.headers().header("x-totalcount").stream().findFirst().orElse(null);

			// Se não há filtro, usa `x-totalcount` da API
			if (search == null || search.isBlank()) {
				if (totalCountHeader != null) {
					this.totalCount = Integer.parseInt(totalCountHeader);
				} else {
					this.totalCount = resultados.size();
				}
			} else {
				// Se há filtro, usa o tamanho da resposta JSON
				this.totalCount = resultados.size();
			}

			log.info("Total de registros disponíveis: " + this.totalCount);

			// log.info("rawResponse do findAllByCodigo: " + rawResponse);

		} catch (CustomWebClientException e) {
			ErrorResponse error = e.getErrorResponse();
			log.error("Erro ao buscar lista (HTTP {}): {}", error.getStatus(), error.getMessage());
			if (error.getStatus() == 302) {
				log.warn("Erro de acesso ao servidor RESTAPI - Possível redirecionamento inesperado.");
			}
		} catch (WebClientResponseException e) {
			log.error("Erro WebClient (HTTP {}): {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
			throw e;
		} catch (Exception e) {
			log.error("Erro inesperado ao buscar lista", e);
		}

		return resultados;
	}

	public FileResponse getAttachmentNames(String unid) {
		FileResponse response = new FileResponse();
		try {
			// Gera a URL para a requisição
			String url = "/attachmentnames/" + unid + "?includeEmbedded=true&dataSource=" + scope;
			System.out.println("URL gerada para getAttachmentNames: " + url);

			// Faz a requisição GET e captura a resposta como String
			String rawResponse = webClient.get()
					.uri(uriBuilder -> uriBuilder.path("/attachmentnames/" + unid).queryParam("includeEmbedded", "true")
							.queryParam("dataSource", scope).build())
					.header("Authorization", "Bearer " + getUser().getToken()).retrieve()
					.onStatus(HttpStatusCode::isError,
							clientResponse -> clientResponse.bodyToMono(String.class).flatMap(errorMessage -> {
								System.err.println("Erro HTTP ao buscar nomes de anexos: " + errorMessage);
								return Mono.error(new CustomWebClientException("Erro ao buscar anexos: " + errorMessage,
										clientResponse.statusCode().value()));
							}))
					.bodyToMono(String.class).block();

			System.out.println("Resposta bruta recebida: " + rawResponse);

			// Verifica se a resposta bruta está vazia
			if (rawResponse == null || rawResponse.isEmpty()) {
				System.err.println("Resposta vazia ao buscar nomes de anexos.");
				response.setMessage("Erro: Resposta vazia da API.");
				response.setStatusCode(500);
				response.setSuccess(false);
				return response;
			}

			// Desserializa a resposta para o objeto FileResponse
			response = objectMapper.readValue(rawResponse, FileResponse.class);

			// Verificação adicional de resposta nula ou falha após a desserialização
			if (response == null) {
				response = new FileResponse();
				response.setMessage("FileResponse é null - Erro ao buscar nomes de anexos.");
				response.setStatusCode(500);
				response.setSuccess(false);
			}

		} catch (WebClientResponseException e) {
			System.err.println("Erro HTTP ao buscar nomes de anexos: " + e.getResponseBodyAsString());
			response.setMessage("Erro HTTP: " + e.getMessage());
			response.setStatusCode(e.getStatusCode().value());
			response.setSuccess(false);
		} catch (Exception e) {
			System.err.println("Erro inesperado ao buscar nomes de anexos: " + e.getMessage());
			e.printStackTrace();
			response.setMessage("Erro inesperado ao buscar nomes de anexos: " + e.getMessage());
			response.setStatusCode(500);
			response.setSuccess(false);
		}

		model.getLogger().info("GetAttachmentNames - Anexos encontrados: " + response);
		return response;
	}

	public FileResponse deleteAnexo(String unid, String fileName) {
		FileResponse response = new FileResponse();
		try {
			String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
			String uri = "/attachments/%s/%s?dataSource=%s".formatted(unid, encoded, scope);

			String rawResponse = webClient.delete().uri(uri).header("Authorization", "Bearer " + getUser().getToken())
					.retrieve()
					.onStatus(HttpStatusCode::isError,
							cr -> cr.bodyToMono(String.class)
									.flatMap(msg -> Mono.error(new CustomWebClientException(
											"Erro ao apagar anexo: " + msg, cr.statusCode().value()))))
					.bodyToMono(String.class).block();

			response = objectMapper.readValue(rawResponse, FileResponse.class);
			if (!response.isDeleteSuccess()) {
				response.setMessage("Falha ao deletar o anexo.");
				response.setStatusCode(500);
			}
			return response;
		} catch (Exception e) {
			response.setMessage("Erro inesperado ao apagar o anexo: " + e.getMessage());
			response.setStatusCode(500);
			response.setSuccess(false);
			return response;
		}
	}

	/**
	 * Apaga todos os anexos de um doc. Uso quando gravo um doc. Apago e gravo todos
	 * os da memória por cima para gerar uma sensação de sync
	 * 
	 * @param unid
	 */
	public void deleteAnexos(String unid) {

	}

	public void loadAnexos(T model, String unid) {
		if (model == null || unid == null || unid.trim().isEmpty()) {
			return; // Não há anexos para carregar
		}

		try {
			// Obter lista de nomes de anexos
			FileResponse fileResponse = getAttachmentNames(unid); // Método para listar anexos
			System.out.println("Anexos encontrados: " + fileResponse.getFileNames());
			if (fileResponse != null && fileResponse.getFileNames() != null && fileResponse.getFileNames().size() > 0) {
				for (String fileName : fileResponse.getFileNames()) {
					// Para cada nome de anexo, busca os dados
					FileResponse anexoResponse = getAnexo(unid, fileName);
					if (anexoResponse != null) {
						byte[] fileData = anexoResponse.getFileData();
						model.adicionarAnexo(new AbstractModelDoc.UploadedFile(fileName, fileData));
					} else {
						System.err.println("Erro ao carregar o anexo: " + fileName);
					}
				}
			} else {
				System.out.println("Nenhum anexo encontrado para o documento com UNID: " + unid);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Erro ao carregar anexos para o documento com UNID: " + unid + ". " + e.getMessage());
		}
	}

	/**
	 * Para listas pequenas como funcionariosAtivos, por exemplo, podemos usar a
	 * funçao para trazer todos os documentos de uma determinada vista
	 * 
	 * @return
	 */
	public List<Response<T>> findAll() {
		List<Response<T>> responseList = new ArrayList<>();
		try {
			// Monta a URI para buscar todos os documentos
			String uri = "/lists/_all?dataSource=" + scope;

			// Faz a requisição GET e captura a resposta como String
			String rawResponse = webClient.get().uri(uri).header("Authorization", "Bearer " + getUser().getToken())
					.retrieve().onStatus(HttpStatusCode::isError,
							clientResponse -> clientResponse.bodyToMono(ErrorResponse.class).flatMap(errorResponse -> {
								return Mono.error(new CustomWebClientException(errorResponse.getMessage(),
										errorResponse.getStatus(), errorResponse));
							}))
					.bodyToMono(String.class).block();

			// Verifica se a resposta está vazia
			if (rawResponse == null || rawResponse.isEmpty()) {
				System.out.println("findAll - Resposta vazia da Web API.");
				return responseList; // Retorna uma lista vazia
			}

			// Exibe a resposta bruta no console para análise (opcional)
			System.out.println("findAll - Resposta bruta da Web API: " + rawResponse);

			// Desserializa a resposta para uma lista de objetos do tipo `T`
			JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, modelClass);
			List<T> models = objectMapper.readValue(rawResponse, listType);

			// Converte os objetos do tipo `T` para `Response<T>` e adiciona à lista
			for (T model : models) {
				Response<T> response = new Response<>(model, "Documento carregado com sucesso.", 200, true);
				responseList.add(response);
			}

		} catch (CustomWebClientException e) {
			ErrorResponse error = e.getErrorResponse();
			System.err.println("Erro ao buscar todos os documentos. Código HTTP: " + error.getStatus());
			System.err.println("Mensagem de erro: " + error.getMessage());
		} catch (WebClientResponseException e) {
			System.err.println("Erro ao buscar todos os documentos. Código HTTP: " + e.getStatusCode());
			System.err.println("Mensagem de erro: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Erro inesperado ao buscar todos os documentos.");
		}

		return responseList;
	}

	public List<T> search(int offset, int count, List<QuerySortOrder> sortOrders, String ftSearchQuery,
			Class<T> model) {
		List<T> resultados = new ArrayList<>();
		try {
			// Define um limite fixo para a contagem, ajustável conforme necessário
			final int limiteRegistros = 50;
			count = Math.min(count, limiteRegistros);

			// Define a ordenação (ascendente ou descendente)
			String direction = "asc";
			if (sortOrders != null && !sortOrders.isEmpty()) {
				direction = sortOrders.get(0).getDirection() == SortDirection.ASCENDING ? "asc" : "desc";
			}

			// Sanitiza o termo de busca
			String searchQuery = (ftSearchQuery != null && !ftSearchQuery.trim().isEmpty())
					? UriUtils.encode(ftSearchQuery, StandardCharsets.UTF_8)
					: "";

			// Monta a URL para requisição com base no cURL fornecido
			String uri = "/api/v1/lists/%s?mode=default&dataSource=" + scope
					+ "&ftSearchQuery=%s&count=%d&direction=%s&start=%d".formatted(
							Utils.getListaNameFromModelName(model.getSimpleName()), searchQuery, count, direction,
							offset);

			log.info("Executando busca com URI: " + uri);

			// Executa a requisição GET
			String rawResponse = webClient.get().uri(uri).header("Authorization", "Bearer " + getUser().getToken())
					.retrieve().onStatus(HttpStatusCode::isError,
							clientResponse -> clientResponse.bodyToMono(ErrorResponse.class).flatMap(errorResponse -> {
								int statusCode = clientResponse.statusCode().value();
								return Mono.<Throwable>error(new CustomWebClientException(errorResponse.getMessage(),
										statusCode, errorResponse));
							}))
					.bodyToMono(String.class).block(); // Bloqueia até receber a resposta

			// Verifica se a resposta está vazia para evitar erro de desserialização
			if (rawResponse == null || rawResponse.trim().isEmpty()) {
				log.warn("A resposta da API veio vazia.");
				return new ArrayList<>();
			}

			// Desserializa a resposta JSON para Lista<T>
			JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, model);
			resultados = objectMapper.readValue(rawResponse, listType);

		} catch (CustomWebClientException e) {
			ErrorResponse error = e.getErrorResponse();
			log.error("Erro ao buscar lista (HTTP {}): {}", error.getStatus(), error.getMessage());
			if (error.getStatus() == 302) {
				log.warn("Erro de acesso ao servidor RESTAPI - Possível redirecionamento inesperado.");
			}
		} catch (WebClientResponseException e) {
			log.error("Erro WebClient (HTTP {}): {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
			throw e;
		} catch (Exception e) {
			log.error("Erro inesperado ao buscar lista", e);
		}
		return resultados;
	}

	protected Response<T> buscarPrimeiroDaLista(String uri) {
		Response<T> response;
		try {
			String rawResponse = webClient.get().uri(uri).header("Authorization", "Bearer " + getUser().getToken())
					.retrieve()
					.onStatus(HttpStatusCode::isError,
							clientResponse -> clientResponse.bodyToMono(ErrorResponse.class)
									.flatMap(errorResponse -> Mono.<Throwable>error(new CustomWebClientException(
											errorResponse.getMessage(), errorResponse.getStatus(), errorResponse))))
					.bodyToMono(String.class).block();

			System.out.println("Resposta bruta: " + rawResponse);

			List<T> resultList = objectMapper.readValue(rawResponse,
					objectMapper.getTypeFactory().constructCollectionType(List.class, modelClass));

			if (!resultList.isEmpty()) {
				T resultModel = resultList.get(0);
				response = new Response<>(resultModel, "Documento encontrado.", 200, true);
			} else {
				response = new Response<>(null, "Nenhum documento encontrado.", 404, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = new Response<>(null, "Erro ao buscar documento.", 500, false);
		}
		return response;
	}

	// dentro do AbstractService
	private void populaMultivalues(Object model, JsonNode root) {
		Class<?> clazz = model.getClass();

		for (Class<?> inner : clazz.getDeclaredClasses()) {
			if (!AbstractModelDocMultivalue.class.isAssignableFrom(inner))
				continue;

			String prefix = inner.getSimpleName().toLowerCase(); // "unidade"
			Field[] innerFields = inner.getDeclaredFields();

			// 1) ler colunas (case-insensitive)
			Map<Field, List<Object>> colunas = new LinkedHashMap<>();
			int tamanho = 0;

			for (Field f : innerFields) {
				String mvKey = prefix + Utils.capitalize(f.getName()); // "unidadeStatus"
				JsonNode arr = getIgnoreCase(root, mvKey);

				List<Object> values = new ArrayList<>();
				if (arr != null && arr.isArray()) {
					for (JsonNode n : arr) {
						values.add(objectMapper.convertValue(n, f.getType()));
					}
				}
				colunas.put(f, values);
				tamanho = Math.max(tamanho, values.size());
			}

			// Se não veio NENHUMA coluna multivalue (todas vazias/ausentes), pula
			if (tamanho == 0) {
				System.out.println(
						"[populaMultivalues] Sem arrays para prefixo '" + prefix + "'. Mantendo wrapper como está.");
				continue;
			}

			// 2) montar linhas
			List<Object> linhas = new ArrayList<>(tamanho);
			try {
				for (int i = 0; i < tamanho; i++) {
					Object innerInstance = inner.getDeclaredConstructor().newInstance();
					for (Map.Entry<Field, List<Object>> e : colunas.entrySet()) {
						Field f = e.getKey();
						List<Object> vals = e.getValue();
						Object val = (i < vals.size()) ? vals.get(i) : null;
						f.setAccessible(true);
						f.set(innerInstance, val);
					}
					linhas.add(innerInstance);
				}
			} catch (Exception ex) {
				// apenas loga, não derruba a tela
				ex.printStackTrace();
				System.out.println(
						"[populaMultivalues] Falha criando linhas para " + inner.getName() + ": " + ex.getMessage());
				continue;
			}

			// 3) injetar no wrapper "<prefixo>s" (ex.: "unidades"), sobrescrevendo conteúdo
			String destinoNome = Utils.addPlurais(prefix);
			Field destino = getFieldByNameDeep(clazz, destinoNome);
			if (destino == null) {
				System.out.println("[populaMultivalues] Wrapper não encontrado: " + destinoNome);
				continue;
			}

			try {
				destino.setAccessible(true);
				Object wrapper = destino.get(model);
				if (wrapper == null) {
					wrapper = destino.getType().getDeclaredConstructor().newInstance();
					destino.set(model, wrapper);
				}

				Field listaField = getFieldByNameDeep(wrapper.getClass(), "lista");
				if (listaField != null && List.class.isAssignableFrom(listaField.getType())) {
					listaField.setAccessible(true);
					// substitui completamente o conteúdo
					listaField.set(wrapper, linhas);
				} else {
					System.out.println("[populaMultivalues] Campo 'lista' não encontrado em "
							+ wrapper.getClass().getSimpleName());
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("[populaMultivalues] Falha injetando linhas no wrapper '" + destinoNome + "': "
						+ e.getMessage());
			}
		}
	}

	private JsonNode getIgnoreCase(JsonNode root, String key) {
		for (Iterator<String> it = root.fieldNames(); it.hasNext();) {
			String k = it.next();
			if (k.equalsIgnoreCase(key)) {
				return root.get(k);
			}
		}
		return null;
	}

	/**
	 * "Achata" os wrappers de listas multivalue (AbstractModelListaMultivalue) em
	 * arrays simples esperados pelo Domino REST API.
	 * 
	 * Exemplo: wrapper "unidades" com campos "status", "codigo" -> gera arrays
	 * "unidadeStatus", "unidadeCodigo"
	 * 
	 * - Nunca envia valores nulos. - Remove arrays vazios (se não há valores). -
	 * Para campos String, substitui null por "".
	 */
	private ObjectNode flattenForDomino(T model) {
		ObjectNode root = objectMapper.valueToTree(model);

		Class<?> clazz = model.getClass();
		for (Class<?> inner : clazz.getDeclaredClasses()) {
			if (!AbstractModelDocMultivalue.class.isAssignableFrom(inner)) {
				continue; // só processa os que estendem AbstractModelDocMultivalue
			}

			String prefix = inner.getSimpleName().toLowerCase(); // ex: "unidade"
			String wrapperName = Utils.addPlurais(prefix); // ex: "unidades"
			Field wrapperField = getFieldByNameDeep(clazz, wrapperName);
			if (wrapperField == null)
				continue;

			try {
				wrapperField.setAccessible(true);
				Object wrapper = wrapperField.get(model);
				if (!(wrapper instanceof AbstractModelListaMultivalue<?> w))
					continue;

				var linhas = w.getLista();
				if (linhas == null)
					continue;

				for (Field f : inner.getDeclaredFields()) {
					f.setAccessible(true);
					String itemName = prefix + Utils.capitalize(f.getName());

					ArrayNode arrayDeValores = root.putArray(itemName);
					boolean temValorReal = false;

					for (Object row : linhas) {
						if (row == null)
							continue;

						Object val = f.get(row);

						if (val == null) {
							// Para String, substitui null por "" (aceito pelo Domino)
							if (f.getType().equals(String.class)) {
								arrayDeValores.add("");
								temValorReal = true;
							}
							// Para outros tipos, não adiciona nada
						} else {
							arrayDeValores.add(objectMapper.valueToTree(val));
							temValorReal = true;
						}
					}

					// Se não houve nenhum valor real, remove o campo
					if (!temValorReal) {
						root.remove(itemName);
					}
				}

				// ⚠️ IMPORTANTE: remove o wrapper do payload,
				// só ficam os arrays no JSON final
				root.remove(wrapperName);

			} catch (Exception e) {
				throw new RuntimeException("Falha ao achatar multivalue para '" + wrapperName + "'", e);
			}
		}
		return root;
	}

	private Response<T> getAndPopulaModelo(String uri, boolean carregarAnexos) {
		try {
			String raw = doGet(uri);
			T model = mapRawToModel(raw, carregarAnexos);
			if (model == null) {
				return new Response<>(null, "Nenhum documento encontrado.", 404, false);
			}
			return new Response<>(model, "Documento carregado com sucesso.", 200, true);
		} catch (CustomWebClientException e) {
			ErrorResponse err = e.getErrorResponse();
			return new Response<>(null, err.getMessage() + " - " + err.getDetails(), err.getStatus(), false);
		} catch (WebClientResponseException e) {
			return new Response<>(null, "Erro ao buscar documento: " + e.getMessage(), e.getStatusCode().value(),
					false);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response<>(null, "Erro inesperado ao buscar documento.", 500, false);
		}
	}

	/**
	 * Executa uma requisição HTTP GET para o Domino REST API e retorna a resposta
	 * JSON como {@link String}.
	 *
	 * 
	 */
	private String doGet(String uri) {
		return webClient.get().uri(uri).header("Content-Type", "application/json; charset=UTF-8")
				.header("Accept-Charset", "UTF-8").header("Authorization", "Bearer " + getUser().getToken()).retrieve()
				.onStatus(HttpStatusCode::isError,
						clientResponse -> clientResponse.bodyToMono(ErrorResponse.class)
								.flatMap(errorResponse -> Mono.<Throwable>error(new CustomWebClientException(
										errorResponse.getMessage(), errorResponse.getStatus(), errorResponse))))
				.bodyToMono(String.class).block();
	}

	/**
	 * Converte a resposta JSON bruta do Domino REST API em um modelo Java,
	 * inicializando e populando corretamente os campos multivalue e, opcionalmente,
	 * carregando anexos associados ao documento.
	 * 
	 * @param raw            String JSON retornada pelo Domino REST API.
	 * @param carregarAnexos Se {@code true}, irá carregar anexos do documento.
	 * @param unidParaAnexos UNID do documento, usado para buscar anexos quando
	 *                       {@code carregarAnexos} for true.
	 * @return Instância de {@code T} mapeada e inicializada a partir do JSON.
	 * @throws Exception Caso ocorra erro de parsing ou inicialização.
	 */
	private T mapRawToModel(String rawJson, boolean carregarAnexos) throws Exception {
		JsonNode root = objectMapper.readTree(rawJson);

		// Se vier array (ex.: lists), pega o primeiro item
		if (root.isArray()) {
			if (root.size() == 0) {
				return null; // lista vazia
			}
			root = root.get(0);
		}

		T model = objectMapper.treeToValue(root, modelClass);
		model.init();
		populaMultivalues(model, root);

		// Se for para carregar anexos e tiver @unid
		if (carregarAnexos) {
			JsonNode unidNode = root.get("@unid");
			if (unidNode != null && !unidNode.asText().isBlank()) {
				loadAnexos(model, unidNode.asText());
			}
		}

		return model;
	}

	@SuppressWarnings("unchecked")
	protected <C extends AbstractModelDoc> AbstractService<C> resolveService(Class<C> childClass) {
		ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
		String simple = childClass.getSimpleName(); // ex.: Aprovacao
		String bean = Character.toLowerCase(simple.charAt(0)) + simple.substring(1) + "Service"; // aprovacaoService
		return (AbstractService<C>) ctx.getBean(bean);
	}

	public <C extends AbstractModelDoc> List<C> findChildrenByIdOrigem(Class<C> childClass, String idOrigem) {
		try {
			String scopeChild = Utils.getScopeFromClass(childClass);

			URI uri = UriComponentsBuilder.fromPath("/lists/{view}") //
					.queryParam("dataSource", scopeChild) //
					.queryParam("mode", this.mode) //
					.queryParam("documents", "true") //
					.queryParam("keyAllowPartial", "false") //
					.queryParam("key", idOrigem) // o builder faz o encode
					.build("_intraIdOrigem"); // ✅ sua view padronizada

			String raw = webClient.get()//
					.uri(uri) //
					.header("Authorization", "Bearer " + getUser().getToken()) //
					.retrieve() //
					.bodyToMono(String.class)//
					.block();

			if (raw == null || raw.isBlank())
				return List.of();

			JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, childClass);

			return objectMapper.readValue(raw, listType);

		} catch (Exception e) {
			log.error("findChildrenByIdOrigem falhou", e);
			return List.of();
		}
	}

	public static class SyncReport {
		public int created, updated, deleted;
		public List<String> errors = new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	protected SyncReport syncChildren(T parent) {
		SyncReport report = new SyncReport();
		String parentId = parent.getId();
		if (parentId == null || parentId.isBlank()) {
			// garanta que o pai tenha id (você já faz no init/createNewDoc)
			parent.init();
			parentId = parent.getId();
		}

		for (Field f : parent.getClass().getDeclaredFields()) {
			f.setAccessible(true);

			// pular campos que não devem cascatear (se quiser, crie um @NoCascade)
			// if (f.isAnnotationPresent(NoCascade.class)) continue;

			try {
				if (List.class.isAssignableFrom(f.getType())) {
					// é uma lista?
					var g = f.getGenericType();
					if (g instanceof ParameterizedType pt) {
						var arg = pt.getActualTypeArguments()[0];
						if (arg instanceof Class<?> c && AbstractModelDoc.class.isAssignableFrom(c)) {
							Class<? extends AbstractModelDoc> childClass = (Class<? extends AbstractModelDoc>) c;
							List<AbstractModelDoc> desired = (List<AbstractModelDoc>) (f.get(parent) != null
									? f.get(parent)
									: List.of());
							SyncReport r = syncOneList(parentId, childClass, desired);
							report.created += r.created;
							report.updated += r.updated;
							report.deleted += r.deleted;
							report.errors.addAll(r.errors);
						}
					}
				} else if (AbstractModelDoc.class.isAssignableFrom(f.getType())) {
					// filho único
					Class<? extends AbstractModelDoc> childClass = (Class<? extends AbstractModelDoc>) f.getType();
					AbstractModelDoc desired = (AbstractModelDoc) f.get(parent);
					SyncReport r = syncOneSingle(parentId, childClass, desired);
					report.created += r.created;
					report.updated += r.updated;
					report.deleted += r.deleted;
					report.errors.addAll(r.errors);
				}
			} catch (Exception e) {
				log.error("Erro no cascade do campo {}", f.getName(), e);
				report.errors.add("Campo " + f.getName() + ": " + e.getMessage());
			}
		}
		return report;
	}

	private <C extends AbstractModelDoc> SyncReport syncOneList(String parentId, Class<C> childClass,
			List<AbstractModelDoc> desiredRaw) {

		SyncReport r = new SyncReport();
		AbstractService<C> svc = resolveService(childClass);

		// 1) carregar existentes do banco
		List<C> existing = findChildrenByIdOrigem(childClass, parentId);

		// 2) indexar por id lógico (não UNID)
		Map<String, C> existingById = existing.stream().filter(c -> c.getId() != null)
				.collect(Collectors.toMap(C::getId, Function.identity(), (a, b) -> a));

		// 3) preparar "desired" como C
		List<C> desired = desiredRaw.stream().map(c -> childClass.cast(c)).toList();

		Set<String> desiredIds = desired.stream().map(C::getId).filter(Objects::nonNull).collect(Collectors.toSet());

		// 4) CREATE / UPDATE
		for (C child : desired) {
			// garantir parentId e id
			child.setIdOrigem(parentId);
			if (child.getId() == null || child.getId().isBlank()) {
				child.setId(Utils.newModelId(Utils.getScopeFromClass(childClass), childClass.getSimpleName())); // sua
																												// função
																												// de id
			}

			C persisted = existingById.get(child.getId());
			try {
				if (persisted == null) {
					// novo
					var sr = svc.save(child);
					if (sr.isSuccess())
						r.created++;
					else
						r.errors.add("Create " + childClass.getSimpleName() + ": " + sr.getMessage());
				} else {
					// update: opcional: comparar diff para evitar update inútil
					// carregar meta/unid do existente para não trombar com revisão
					child.setMeta(persisted.getMeta());
					var sr = svc.save(child);
					if (sr.isSuccess())
						r.updated++;
					else
						r.errors.add("Update " + childClass.getSimpleName() + ": " + sr.getMessage());
				}
			} catch (Exception e) {
				r.errors.add("Save " + childClass.getSimpleName() + ": " + e.getMessage());
			}
		}

		// 5) DELETE (os que existem mas não estão mais no pai)
		for (C old : existing) {
			if (old.getId() == null || !desiredIds.contains(old.getId())) {
				try {
					var dr = svc.delete(old);
					if (dr != null && ("OK".equalsIgnoreCase(dr.getStatusText()) || "200".equals(dr.getStatus()))) {
						r.deleted++;
					} else {
						r.errors.add("Delete " + childClass.getSimpleName() + ": "
								+ (dr != null ? dr.getMessage() : "sem resposta"));
					}
				} catch (Exception e) {
					r.errors.add("Delete " + childClass.getSimpleName() + ": " + e.getMessage());
				}
			}
		}
		return r;
	}

	private <C extends AbstractModelDoc> SyncReport syncOneSingle(String parentId, Class<C> childClass,
			AbstractModelDoc desiredRaw) {

		SyncReport r = new SyncReport();
		AbstractService<C> svc = resolveService(childClass);

		// carregar o único (ou os) existente(s) por parentId
		List<C> existing = findChildrenByIdOrigem(childClass, parentId);

		if (desiredRaw == null) {
			// se não deseja mais ter o filho, apague os existentes
			for (C old : existing) {
				try {
					var dr = svc.delete(old);
					if (dr != null && ("OK".equalsIgnoreCase(dr.getStatusText()) || "200".equals(dr.getStatus())))
						r.deleted++;
					else
						r.errors.add("Delete " + childClass.getSimpleName() + ": "
								+ (dr != null ? dr.getMessage() : "sem resposta"));
				} catch (Exception e) {
					r.errors.add("Delete " + childClass.getSimpleName() + ": " + e.getMessage());
				}
			}
			return r;
		}

		C desired = childClass.cast(desiredRaw);
		desired.setIdOrigem(parentId);
		if (desired.getId() == null || desired.getId().isBlank()) {
			desired.setId(Utils.newModelId(Utils.getScopeFromClass(childClass), childClass.getSimpleName()));
		}

		if (existing.isEmpty()) {
			var sr = svc.save(desired);
			if (sr.isSuccess())
				r.created++;
			else
				r.errors.add("Create " + childClass.getSimpleName() + ": " + sr.getMessage());
		} else {
			// adote a política que preferir — por ex., manter só 1 e apagar excedentes
			C current = existing.get(0);
			desired.setMeta(current.getMeta());
			var sr = svc.save(desired);
			if (sr.isSuccess())
				r.updated++;
			else
				r.errors.add("Update " + childClass.getSimpleName() + ": " + sr.getMessage());

			// apaga excedentes se houver
			for (int i = 1; i < existing.size(); i++) {
				var dr = svc.delete(existing.get(i));
				if (dr != null && ("OK".equalsIgnoreCase(dr.getStatusText()) || "200".equals(dr.getStatus())))
					r.deleted++;
			}
		}
		return r;
	}

}
