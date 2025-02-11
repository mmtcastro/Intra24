package br.com.tdec.intra.compras.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.JavaType;
import com.vaadin.flow.data.provider.QuerySortOrder;

import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.compras.model.Compra;
import br.com.tdec.intra.utils.exceptions.CustomWebClientException;
import br.com.tdec.intra.utils.exceptions.ErrorResponse;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

@Service
@Getter
@Setter
public class CompraService extends AbstractService<Compra> {

	public boolean fornecedorFezNegocio(String codigoFornecedor) {
		// Verifica se o código é válido
		if (codigoFornecedor == null || codigoFornecedor.trim().isEmpty()) {
			throw new IllegalArgumentException("Código do Fornecedor não pode ser nulo ou vazio.");
		}

		// Chama o método findAllByCodigoFornecedor para verificar se há compras
		// associadas
		List<Compra> compras = findAllByCodigoFornecedor(0, 1, null, codigoFornecedor);

		// Retorna true se houver pelo menos uma compra associada ao fornecedor
		return compras != null && !compras.isEmpty();
	}

	public List<Compra> findAllByCodigoFornecedor(int offset, int count, List<QuerySortOrder> sortOrders,
			String codigoFornecedor) {
		List<Compra> ret = new ArrayList<>();
		try {
			count = 50; // Define um limite fixo de registros retornados
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

			// Construção da URL para buscar compras associadas ao fornecedor
			String uri = "/lists/3CF0D8B448A3C39883257B570074D097?mode=" + mode + "&dataSource=compras&key="
					+ codigoFornecedor + "&count=" + count + "&direction=" + direction + "&start=" + offset;

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

			// Desserializa a resposta JSON para uma lista de objetos Compra
			JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, Compra.class);
			ret = objectMapper.readValue(rawResponse, listType);

		} catch (CustomWebClientException e) {
			ErrorResponse error = e.getErrorResponse();
			System.err.println("Erro ao buscar lista de compras. Código HTTP: " + error.getStatus());
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
