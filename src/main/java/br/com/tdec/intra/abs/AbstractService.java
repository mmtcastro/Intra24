package br.com.tdec.intra.abs;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

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
@Service
public abstract class AbstractService<T extends AbstractModelDoc> {

	protected WebClientService webClientService;
	protected WebClient webClient;
	@Autowired
	protected ObjectMapper objectMapper; // jackson datas
	protected String scope;
	protected String form;
	protected String mode; // usado do Domino Restapi para definir se pode ou não deletar e rodar agentes
	protected T model;
	protected Class<T> modelClass;
	protected Integer totalCount;

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@SuppressWarnings("unchecked")
	public AbstractService() {
		// Infere o tipo genérico no tempo de execução
		this.modelClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		scope = Utils.getScopeFromClass(this.getClass());

		this.mode = "default"; // tem que trocar para DQL ou outro mode caso necessário. Esta aqui para //
								// simplificar.
		this.model = createModel();
		form = model.getClass().getSimpleName(); // Vertical
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
		// Verifica se o código é nulo ou vazio
		if (unid == null || unid.trim().isEmpty()) {
			return new Response<>(null, "Unid não pode ser nulo ou vazio.", 400, false);
		}
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
			System.out.println("FindByUnid - Resposta bruta da Web API: " + rawResponse);

			// Desserializa a resposta bruta manualmente para o tipo esperado (T)
			// ObjectMapper objectMapper = new ObjectMapper(); // cuidado para nao pegar o
			// objectMapper padrao
			T model = objectMapper.readValue(rawResponse, modelClass);

			model.init(); // Inicializa o modelo, se necessário depois de carregar os dados

			// Chamada para carregar anexos
			loadAnexos(model, unid);

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

	public Response<T> findById(String id) {
		Response<T> response = null;
		// Verifica se o código é nulo ou vazio
		if (id == null || id.trim().isEmpty()) {
			return new Response<>(null, "Id não pode ser nulo ou vazio.", 400, false);
		}
		try {
			// Montando a URI com os parâmetros para buscar por vista
			String apiUrl = "/lists/_intraIds?mode=default&dataSource=" + scope
					+ "keyAllowPartial=false&documents=true&richTextAs=mime&key=" + id + "&scope=documents";

			// Capturando a resposta do WebClient
			String rawResponse = webClient.get().uri(apiUrl).header("Content-Type", "application/json; charset=UTF-8")
					.header("Accept-Charset", "UTF-8").header("Authorization", "Bearer " + getUser().getToken())
					.retrieve().onStatus(HttpStatusCode::isError,
							clientResponse -> clientResponse.bodyToMono(ErrorResponse.class).flatMap(errorResponse -> {
								return Mono.<Throwable>error(new CustomWebClientException(errorResponse.getMessage(),
										errorResponse.getStatus(), errorResponse));
							}))
					.bodyToMono(String.class).block();

			// Verificando se a resposta está vazia
			if (rawResponse == null || rawResponse.isEmpty()) {
				return new Response<>(null, "Resposta vazia da Web API.", 204, false);
			}

			System.out.println("findById - Resposta bruta da Web API: " + rawResponse);

			// Desserializar a resposta para o tipo esperado (T)
			T model = objectMapper.readValue(rawResponse, modelClass);
			model.init(); // Inicializa o modelo, se necessário

			// Retorna o modelo em um Response de sucesso
			response = new Response<>(model, "Documento carregado com sucesso.", 200, true);

		} catch (CustomWebClientException e) {
			ErrorResponse error = e.getErrorResponse();
			System.out.println("Erro ao tentar buscar documento. Código HTTP: " + error.getStatus());
			response = new Response<>(null, error.getMessage() + " - " + error.getDetails(), error.getStatus(), false);

		} catch (WebClientResponseException e) {
			HttpStatusCode statusCode = e.getStatusCode();
			System.out.println("Erro ao tentar buscar documento. Código HTTP: " + statusCode);
			response = new Response<>(null, "Erro ao buscar documento: " + e.getMessage(), statusCode.value(), false);

		} catch (Exception e) {
			e.printStackTrace();
			response = new Response<>(null, "Erro inesperado ao buscar documento.", 500, false);
		}

		return response;
	}

	/**
	 * Esta funcao retorna um Response com apenas o conteudo da vista (ViewEntry)
	 * sem mime. com apenas um documento pois apenas um codigo pode existir no banco
	 * 
	 * @param codigo
	 * @return
	 */
	public Response<T> findByCodigo(String codigo) {
		System.out.println("findByCodigo - INICIO - codigo = " + codigo + " - form = " + form);
		// Verifica se o código é nulo ou vazio
		if (codigo == null || codigo.trim().isEmpty()) {
			return new Response<>(null, "Código não pode ser nulo ou vazio.", 400, false);
		}

		Response<T> response = null;
		try {
			// Monta a URI com o código e form
			String uri = ("/lists/_intraCodigos?mode=" + mode + "&dataSource=" + scope
					+ "&keyAllowPartial=false&documents=false&richTextAs=mime&key=" + codigo + "&key=" + form)
					.formatted();
			System.out.println("URI findByCodigo: " + uri);

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
			System.out.println("findByCodigo - Resposta bruta da Web API: " + rawResponse);

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
			model.extrairCamposMultivalueGenerico(); // Extrai campos multivalorados
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

//	@SuppressWarnings("hiding")
//	@Getter
//	@Setter
//	public class Response<T> {
//		private T model;
//		private String message;
//		private int status;
//		private boolean success;
//
//		public Response(T body, String message, int status, boolean success) {
//			this.model = body;
//			this.message = message;
//			this.status = status;
//			this.success = success;
//		}
//
//	}

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
			// Normaliza o nome do arquivo para NFC
			String normalizedFileName = Normalizer.normalize(fileName, Normalizer.Form.NFC);
			String encodedFileName = URLEncoder.encode(normalizedFileName, StandardCharsets.UTF_8);

			String uri = "/attachments/" + unid + "/" + encodedFileName + "?dataSource=" + scope;

			System.out.println("URI para deletar anexo: " + uri);

			// Realiza a requisição DELETE e captura a resposta como String

			String rawResponse = webClient.delete()
					.uri(uriBuilder -> uriBuilder
							.path("/attachments/" + unid + "/" + URLEncoder.encode(fileName, StandardCharsets.UTF_8))
							.queryParam("dataSource", scope).build())
					.header("Authorization", "Bearer " + getUser().getToken()).retrieve()
					.onStatus(HttpStatusCode::isError,
							clientResponse -> clientResponse.bodyToMono(String.class).flatMap(errorMessage -> {
								System.err.println("Erro HTTP ao apagar anexo: " + errorMessage);
								return Mono.error(new CustomWebClientException("Erro ao apagar anexo: " + errorMessage,
										clientResponse.statusCode().value()));
							}))
					.bodyToMono(String.class).block();

			// Log da resposta bruta para depuração
			System.out.println("Raw Response: " + rawResponse);

			// Valida se a resposta está vazia
			if (rawResponse == null || rawResponse.isEmpty()) {
				response.setMessage("Resposta da API é nula ou vazia.");
				response.setStatusCode(500);
				response.setSuccess(false);
				return response;
			}

			// Desserializa a resposta para FileResponse
			response = objectMapper.readValue(rawResponse, FileResponse.class);

			// Verifica o status da operação
			if (!response.isDeleteSuccess()) {
				response.setMessage("Falha ao deletar o anexo.");
				response.setStatusCode(500);
			}

		} catch (CustomWebClientException e) {
			System.err.println("Erro do WebClient: " + e.getMessage());
			response.setMessage("Erro ao apagar o anexo: " + e.getMessage());

			response.setSuccess(false);
		} catch (Exception e) {
			System.err.println("Erro inesperado ao apagar o anexo: " + e.getMessage());
			response.setMessage("Erro inesperado ao apagar o anexo: " + e.getMessage());
			response.setStatusCode(500);
			response.setSuccess(false);
		}

		// Log final da resposta
		System.out.println("deleteAnexo - Response: " + response);
		return response;
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
			String uri = "/api/v1/lists/%s?mode=default&dataSource=empresas&ftSearchQuery=%s&count=%d&direction=%s&start=%d"
					.formatted(Utils.getListaNameFromModelName(model.getSimpleName()), searchQuery, count, direction,
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

}
