package br.com.tdec.intra.pessoal.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.JavaType;

import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.pessoal.model.Colaborador;
import br.com.tdec.intra.utils.exceptions.CustomWebClientException;
import br.com.tdec.intra.utils.exceptions.ErrorResponse;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

@Getter
@Setter
@Service
public class ColaboradorService extends AbstractService<Colaborador> {

	public ColaboradorService() {
		super();
	}

	public List<String> getFuncionariosAtivos() {
		List<String> funcionarios = new ArrayList<>();

		try {
			// Monta a URI para buscar todos os documentos de funcionários ativos

			String uri = "/lists/FuncionariosAtivos?dataSource=" + scope + "&mode=" + mode;

			System.out.println("getFuncionariosAtivos - URI: " + uri);

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
				System.out.println("getFuncionariosAtivos - Resposta vazia da Web API.");
				// return funcionarios; // Retorna uma lista vazia
			}

			// Exibe a resposta bruta no console para análise (opcional)
			System.out.println("getFuncionariosAtivos - Resposta bruta da Web API: " + rawResponse);

			// Desserializa a resposta para uma lista de objetos do tipo `Colaborador`
			JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, Colaborador.class);
			List<Colaborador> colaboradores = objectMapper.readValue(rawResponse, listType);

			// Filtra os colaboradores ativos e adiciona os nomes à lista `funcionarios`
			for (Colaborador colaborador : colaboradores) {
				funcionarios.add(colaborador.getFuncionario());
			}

		} catch (CustomWebClientException e) {
			ErrorResponse error = e.getErrorResponse();
			System.err.println("Erro ao buscar funcionários ativos. Código HTTP: " + error.getStatus());
			System.err.println("Mensagem de erro: " + error.getMessage());
		} catch (WebClientResponseException e) {
			System.err.println("Erro ao buscar funcionários ativos. Código HTTP: " + e.getStatusCode());
			System.err.println("Mensagem de erro: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Erro inesperado ao buscar funcionários ativos.");
		}

		return funcionarios;
	}

}
