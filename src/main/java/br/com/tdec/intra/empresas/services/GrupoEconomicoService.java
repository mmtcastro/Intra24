package br.com.tdec.intra.empresas.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.JavaType;

import br.com.tdec.intra.abs.AbstractModelDoc;
import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.compras.service.CompraService;
import br.com.tdec.intra.empresas.model.Empresa;
import br.com.tdec.intra.empresas.model.GrupoEconomico;
import br.com.tdec.intra.sales.service.NegocioService;
import br.com.tdec.intra.services.Response;
import br.com.tdec.intra.utils.exceptions.CustomWebClientException;
import br.com.tdec.intra.utils.exceptions.ErrorResponse;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

@Service
@Getter
@Setter
public class GrupoEconomicoService extends AbstractService<GrupoEconomico> {

	public GrupoEconomicoService() {
		super();
	}

	@Autowired
	private NegocioService negocioService; // Repositório que verifica se existem negócios ligados ao grupo

	@Autowired
	private CompraService compraService; // Repositório que verifica se existem compras ligadas ao grupo

//	public GrupoEconomicoService() {
//		super();
//	}

	@Override
	public DeleteResponse delete(AbstractModelDoc grupoEconomico) {
		// Verifica se há Empresas associadas ao Grupo Econômico
		if (!findEmpresasByGrupoEconomico(grupoEconomico.getCodigo()).isEmpty()) {
			return createDeleteErrorResponse(grupoEconomico,
					"Este Grupo Econômico não pode ser apagado porque há Empresas associadas a ele.");
		}

		// Verifica se há negócios vinculados ao Grupo Econômico
		if (negocioService.grupoEconomicoFezNegocio(grupoEconomico.getCodigo())) {
			return createDeleteErrorResponse(grupoEconomico,
					"Este Grupo Econômico não pode ser apagado porque existem negócios associados a ele.");
		}

		// Verifica se há negócios vinculados a Parcerias associadas ao Grupo Econômico
		if (negocioService.parceriaFezNegocio(grupoEconomico.getCodigo())) {
			return createDeleteErrorResponse(grupoEconomico,
					"Este Grupo Econômico não pode ser apagado porque há Parcerias com negócios vinculados.");
		}

		// Verifica se há negócios vinculados a Fornecedores associados ao Grupo
		// Econômico
		if (compraService.fornecedorFezNegocio(grupoEconomico.getCodigo())) {
			return createDeleteErrorResponse(grupoEconomico,
					"Este Grupo Econômico não pode ser apagado porque há Fornecedores com negócios vinculados.");
		}

		// Se passou por todas as verificações, tenta excluir o documento
		try {
			DeleteResponse response = super.delete(grupoEconomico); // Tentativa de exclusão real

			// Adiciona a mensagem de sucesso se a exclusão ocorreu corretamente
			if (response != null
					&& ("200".equals(response.getStatus()) || "OK".equalsIgnoreCase(response.getStatus()))) {
				response.setMessage("Grupo Econômico excluído com sucesso!");
			}

			return response;
		} catch (Exception e) {
			// Caso ocorra um erro inesperado na exclusão
			return createDeleteErrorResponse(grupoEconomico,
					"Erro inesperado ao tentar excluir o Grupo Econômico: " + e.getMessage());
		}
	}

	/**
	 * Método auxiliar para criar uma resposta de erro padronizada ao tentar excluir
	 * um Grupo Econômico.
	 */
	private DeleteResponse createDeleteErrorResponse(AbstractModelDoc grupoEconomico, String errorMessage) {
		DeleteResponse response = new DeleteResponse();
		response.setStatus("403"); // Código HTTP 403 - Forbidden
		response.setStatusText("Erro ao excluir");
		response.setMessage(errorMessage);
		response.setUnid(grupoEconomico.getUnid());
		return response;
	}

	public List<Empresa> findEmpresasByGrupoEconomico(String codigoGrupoEconomico) {
		List<Empresa> empresas = new ArrayList<>();

		// Validação do código do grupo econômico
		if (codigoGrupoEconomico == null || codigoGrupoEconomico.trim().isEmpty()) {
			throw new IllegalArgumentException("Código do Grupo Econômico não pode ser nulo ou vazio.");
		}

		try {
			int count = 50; // Limite de registros retornados
			String direction = "asc";
			int offset = 0;

			// Construção da URL para buscar empresas associadas ao Grupo Econômico
			String uri = "/lists/C9B265D6D3C015C7832580F800657EA8?mode=" + mode + "&dataSource=empresas&key="
					+ codigoGrupoEconomico + "&count=" + count + "&direction=" + direction + "&start=" + offset;

			// Realiza a requisição GET e captura a resposta como String
			String rawResponse = webClient.get().uri(uri).header("Authorization", "Bearer " + getUser().getToken())
					.retrieve().onStatus(HttpStatusCode::isError,
							clientResponse -> clientResponse.bodyToMono(ErrorResponse.class).flatMap(errorResponse -> {
								if (clientResponse.statusCode().value() == 302) {
									return Mono.<Throwable>error(new CustomWebClientException(
											"Erro de acesso ao servidor RESTAPI", 302, errorResponse));
								}
								return Mono.<Throwable>error(new CustomWebClientException(errorResponse.getMessage(),
										errorResponse.getStatus(), errorResponse));
							}))
					.bodyToMono(String.class).block(); // Aguarda a resposta síncrona

			// Se a resposta for nula ou vazia, retorna uma lista vazia
			if (rawResponse == null || rawResponse.trim().isEmpty()) {
				System.err.println("Erro: Resposta da API veio vazia!");
				return new ArrayList<>();
			}

			// System.out.println("Resposta da API GrupoEconomicoByCodigo: " + rawResponse);

			// Desserializa a resposta JSON para uma lista de objetos Empresa
			JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, Empresa.class);
			empresas = objectMapper.readValue(rawResponse, listType);

		} catch (CustomWebClientException e) {
			ErrorResponse error = e.getErrorResponse();
			System.err.println("Erro ao buscar lista de empresas. Código HTTP: " + error.getStatus());
			System.err.println("Mensagem de erro: " + error.getMessage());
			if (error.getStatus() == 302) {
				System.err.println("Erro de acesso ao servidor RESTAPI");
			}
		} catch (WebClientResponseException e) {
			System.err.println("Erro: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return empresas;
	}

	public Response<GrupoEconomico> findGrupoEconomicoSemMime() {
		return buscarPrimeiroDaLista(
				"/lists/_intraCodigos?mode=nomime&dataSource=empresas&keyAllowPartial=false&documents=true&key=123TESTE&key=GrupoEconomico&scope=documents");

	}

}
