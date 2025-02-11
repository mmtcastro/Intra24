package br.com.tdec.intra.sales.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.JavaType;
import com.vaadin.flow.data.provider.QuerySortOrder;

import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.sales.model.Negocio;
import br.com.tdec.intra.utils.exceptions.CustomWebClientException;
import br.com.tdec.intra.utils.exceptions.ErrorResponse;
import reactor.core.publisher.Mono;

@Service
public class NegocioService extends AbstractService<Negocio> {

	public boolean grupoEconomicoFezNegocio(String codigoGrupoEconomico) {
		// Verifica se o código é válido
		if (codigoGrupoEconomico == null || codigoGrupoEconomico.trim().isEmpty()) {
			throw new IllegalArgumentException("Código do Grupo Econômico não pode ser nulo ou vazio.");
		}

		// Chama o método findAllByCodigoGrupoEconomico para verificar se há negócios
		List<Negocio> negocios = findAllByCodigoGrupoEconomico(0, 1, null, codigoGrupoEconomico);

		// Retorna true se houver pelo menos um negócio
		return negocios != null && !negocios.isEmpty();
	}

	public boolean parceriaFezNegocio(String codigoParceria) {
		// Verifica se o código é válido
		if (codigoParceria == null || codigoParceria.trim().isEmpty()) {
			throw new IllegalArgumentException("Código da Parceria não pode ser nulo ou vazio.");
		}

		// Chama o método findAllByCodigoParceria para verificar se há negócios
		List<Negocio> negocios = findAllByCodigoParceria(0, 1, null, codigoParceria);

		// Retorna true se houver pelo menos um negócio
		return negocios != null && !negocios.isEmpty();
	}

	public List<Negocio> findAllByCodigoGrupoEconomico(int offset, int count, List<QuerySortOrder> sortOrders,
			String codigoGrupoEconomico) {
		List<Negocio> ret = new ArrayList<>();
		try {
			count = 50; // Define um limite fixo
			String direction = "asc";

			if (sortOrders != null && !sortOrders.isEmpty()) {
				QuerySortOrder sortOrder = sortOrders.get(0);
				if (sortOrder.getDirection() != null
						&& sortOrder.getDirection().toString().equalsIgnoreCase("ASCENDING")) {
					direction = "asc";
				} else {
					direction = "desc";
				}
			}

			// Construção da URL com o campo específico "codigoGrupoEconomico"
			String uri = "/lists/0BFF47D8A0323DD80325879200776344?mode=" + mode + "&dataSource=sales&key="
					+ codigoGrupoEconomico + "&count=" + count + "&direction=" + direction + "&start=" + offset;
			// System.out.println("findAllByCodigoGrupoEconomico - URI: " + uri);

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
			// System.out.println("rawResponse: " + rawResponse);
			if (rawResponse == null || rawResponse.trim().isEmpty()) {
				System.err.println("Erro: Resposta da API veio vazia!");
				return new ArrayList<>();
			}

			// Desserializa a resposta JSON para uma lista de objetos Negocio
			JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, Negocio.class);
			ret = objectMapper.readValue(rawResponse, listType);

		} catch (CustomWebClientException e) {
			ErrorResponse error = e.getErrorResponse();
			System.err.println("Erro ao buscar lista. Código HTTP: " + error.getStatus());
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
		return ret;
	}

	public List<Negocio> findAllByCodigoParceria(int offset, int count, List<QuerySortOrder> sortOrders,
			String codigoParceria) {
		List<Negocio> ret = new ArrayList<>();
		try {
			count = 50; // Define um limite fixo
			String direction = "asc";

			if (sortOrders != null && !sortOrders.isEmpty()) {
				QuerySortOrder sortOrder = sortOrders.get(0);
				if (sortOrder.getDirection() != null
						&& sortOrder.getDirection().toString().equalsIgnoreCase("ASCENDING")) {
					direction = "asc";
				} else {
					direction = "desc";
				}
			}

			// Construção da URL com o campo específico "codigoParceria"
			String uri = "/lists/77EE4BA629CD74FE03258C2D0075D9E9?mode=" + mode + "&dataSource=sales&key="
					+ codigoParceria + "&count=" + count + "&direction=" + direction + "&start=" + offset;

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

			// Desserializa a resposta JSON para uma lista de objetos Negocio
			JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, Negocio.class);
			ret = objectMapper.readValue(rawResponse, listType);

		} catch (CustomWebClientException e) {
			ErrorResponse error = e.getErrorResponse();
			System.err.println("Erro ao buscar lista. Código HTTP: " + error.getStatus());
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
		return ret;
	}

}
