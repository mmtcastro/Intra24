package br.com.tdec.intra.empresas.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.JavaType;

import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.empresas.model.TipoEmpresa;
import br.com.tdec.intra.utils.exceptions.CustomWebClientException;
import br.com.tdec.intra.utils.exceptions.ErrorResponse;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

@Getter
@Setter
@Service
public class TipoEmpresaService extends AbstractService<TipoEmpresa> {

	public TipoEmpresaService() {
		super();
	}

	public List<TipoEmpresa> getTiposEmpresas() {
		List<TipoEmpresa> tiposEmpresa = new ArrayList<>();

		try {
			// Monta a URI para buscar todos os documentos de tipos de empresa
			String uri = "/lists/TiposEmpresas?dataSource=" + scope + "&mode=" + mode;

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
				System.out.println("getTiposEmpresa - Resposta vazia da Web API.");
				return tiposEmpresa; // Retorna uma lista vazia
			}

			// Desserializa a resposta para uma lista de objetos do tipo `TipoEmpresa`
			JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, TipoEmpresa.class);
			tiposEmpresa = objectMapper.readValue(rawResponse, listType);

		} catch (CustomWebClientException e) {
			ErrorResponse error = e.getErrorResponse();
			System.err.println("Erro ao buscar tipos de empresa. Código HTTP: " + error.getStatus());
			System.err.println("Mensagem de erro: " + error.getMessage());
		} catch (WebClientResponseException e) {
			System.err.println("Erro ao buscar tipos de empresa. Código HTTP: " + e.getStatusCode());
			System.err.println("Mensagem de erro: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Erro inesperado ao buscar tipos de empresa.");
		}

		return tiposEmpresa;
	}

}
