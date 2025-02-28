package br.com.tdec.intra.api.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.tdec.intra.api.model.ApolloLinkedinModel;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ApolloLinkedinService {
	private static final String API_KEY = "9g9vvSyBRYpoPZoo0zTZLg";
	private static final String API_URL = "https://api.apollo.io/api/v1/people/bulk_match";
	private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

	private final WebClient webClient;

	public ApolloLinkedinService() {
		this.webClient = WebClient.builder().baseUrl(API_URL).defaultHeader("Content-Type", "application/json")
				.defaultHeader("Cache-Control", "no-cache").defaultHeader("User-Agent", "PostmanRuntime/7.32.2")
				.defaultHeader("Accept", "application/json").build();
	}

	/**
	 * 🔹 Busca os dados do LinkedIn em até 3 tentativas: 1️⃣ Apenas pelo email 2️⃣
	 * Apenas pelo nome e sobrenome 3️⃣ Email + nome + sobrenome
	 */
	public Mono<ApolloLinkedinModel> buscarApolloLinkedinModel(String email, String firstName, String lastName) {
		if (email == null || !email.matches(EMAIL_REGEX)) {
			return Mono.error(new IllegalArgumentException("Email inválido: " + email));
		}

		// 🔹 Tentativa 1: Apenas pelo email
		return buscarDadosLinkedin(email, null, null)
				.flatMap(response -> transformarResposta(response, email, firstName, lastName))

				// 🔹 Tentativa 2: Nome + Sobrenome (se email sozinho não encontrou nada)
				.switchIfEmpty(Mono.defer(() -> {
					if (firstName != null && lastName != null) {
						return buscarDadosLinkedin(null, firstName, lastName)
								.flatMap(response -> transformarResposta(response, email, firstName, lastName));
					}
					return Mono.empty();
				}))

				// 🔹 Tentativa 3: Email + Nome + Sobrenome
				.switchIfEmpty(Mono.defer(() -> buscarDadosLinkedin(email, firstName, lastName)
						.flatMap(response -> transformarResposta(response, email, firstName, lastName))))

				// 🔹 Se nada foi encontrado, retorna um modelo vazio
				.defaultIfEmpty(new ApolloLinkedinModel(email, firstName, lastName));
	}

	/**
	 * 🔹 Faz a chamada à API Apollo para buscar os dados do LinkedIn.
	 */
	private Mono<ApolloLinkedinModel.LinkedInResponse> buscarDadosLinkedin(String email, String firstName,
			String lastName) {
		ApolloLinkedinModel.Body requestBody = criarRequestBody(email, firstName, lastName);

		System.out.println("🔍 Enviando requisição para Apollo API com: " + requestBody);

		return webClient.post().bodyValue(requestBody).retrieve().bodyToMono(ApolloLinkedinModel.LinkedInResponse.class)
				.doOnNext(response -> {
					System.out.println("✅ Resposta da Apollo API: " + response);
					try {
						System.out.println("🔎 JSON recebido: " + new ObjectMapper().writeValueAsString(response));
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}).doOnError(error -> System.err.println("❌ Erro ao buscar dados do LinkedIn: " + error.getMessage()))
				.onErrorResume(error -> Mono.empty()); // Se falhar, tenta a próxima abordagem
	}

	/**
	 * 🔹 Transforma a resposta da API em `ApolloLinkedinModel`
	 */
	private Mono<ApolloLinkedinModel> transformarResposta(ApolloLinkedinModel.LinkedInResponse response, String email,
			String firstName, String lastName) {
		if (response == null || response.getMatches() == null || response.getMatches().isEmpty()) {
			System.out.println("⚠ Nenhuma correspondência encontrada para " + email);
			return Mono.empty();
		}

		ApolloLinkedinModel.Match match = response.getMatches().get(0);
		ApolloLinkedinModel.Organization organization = match.getOrganization();

		System.out.println("🔹 Match encontrado: " + match);
		System.out.println("🔹 Organização encontrada: " + (organization != null ? organization : "Nenhuma"));

		// 🔹 Retorno atualizado garantindo que email, firstName e lastName sejam
		// preenchidos corretamente
		return Mono.just(new ApolloLinkedinModel(match.getEmail() != null ? match.getEmail() : email, // Usa o email do
																										// match ou do
																										// request
				match.getFirstName() != null ? match.getFirstName() : firstName,
				match.getLastName() != null ? match.getLastName() : lastName, match.getId(), match.getTitle(),
				match.getLinkedinUrl(), organization != null ? organization.getId() : null,
				organization != null ? organization.getName() : null,
				organization != null ? organization.getLinkedinUrl() : null));
	}

	/**
	 * 🔹 Cria um corpo de requisição adequado, evitando valores nulos.
	 */
	private ApolloLinkedinModel.Body criarRequestBody(String email, String firstName, String lastName) {
		List<ApolloLinkedinModel.Detail> details = new ArrayList<>();

		if (email != null) {
			details.add(new ApolloLinkedinModel.Detail(email, firstName, lastName));
		} else if (firstName != null && lastName != null) {
			details.add(new ApolloLinkedinModel.Detail(null, firstName, lastName));
		}

		return new ApolloLinkedinModel.Body(API_KEY, details);
	}
}
