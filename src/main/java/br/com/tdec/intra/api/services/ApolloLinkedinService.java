package br.com.tdec.intra.api.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
	 * 🔹 Tenta buscar os dados do LinkedIn em até 3 tentativas: 1️⃣ Apenas pelo
	 * email 2️⃣ Apenas pelo nome e sobrenome 3️⃣ Email + nome + sobrenome
	 */
	public Mono<ApolloLinkedinModel> buscarApolloLinkedinModel(String email, String firstName, String lastName) {
		if (email == null || !email.matches(EMAIL_REGEX)) {
			return Mono.error(new IllegalArgumentException("Email inválido: " + email));
		}

		return buscarDadosLinkedin(email, null, null)
				.flatMap(response -> processarResposta(response, email, firstName, lastName))
				.switchIfEmpty(Mono.defer(() -> buscarDadosLinkedin(null, firstName, lastName)
						.flatMap(response -> processarResposta(response, email, firstName, lastName))))
				.switchIfEmpty(Mono.defer(() -> buscarDadosLinkedin(email, firstName, lastName)
						.flatMap(response -> processarResposta(response, email, firstName, lastName))))
				.defaultIfEmpty(new ApolloLinkedinModel(email, firstName, lastName));
	}

	/**
	 * 🔹 Chama a API da Apollo para buscar dados do LinkedIn.
	 */
	private Mono<ApolloLinkedinModel.LinkedInResponse> buscarDadosLinkedin(String email, String firstName,
			String lastName) {
		ApolloLinkedinModel.Body requestBody = criarRequestBody(email, firstName, lastName);

		return webClient.post().bodyValue(requestBody).retrieve().bodyToMono(ApolloLinkedinModel.LinkedInResponse.class)
				.doOnNext(response -> System.out.println("✅ Sucesso: Dados do LinkedIn recebidos."))
				.doOnError(error -> System.err.println("❌ Erro ao buscar dados do LinkedIn: " + error.getMessage()))
				.onErrorResume(error -> Mono.empty()); // Evita crash e continua tentando
	}

	/**
	 * 🔹 Converte a resposta da API em um `ApolloLinkedinModel`.
	 */
	private Mono<ApolloLinkedinModel> processarResposta(ApolloLinkedinModel.LinkedInResponse response, String email,
			String firstName, String lastName) {
		if (response.getMatches() == null || response.getMatches().isEmpty()) {
			return Mono.empty(); // Retorna vazio para permitir novas tentativas
		}

		ApolloLinkedinModel.Match match = response.getMatches().get(0);
		ApolloLinkedinModel.Organization organization = match.getOrganization();

		return Mono.just(new ApolloLinkedinModel(email, match.getFirstName(), match.getLastName(), match.getId(),
				match.getTitle(), match.getLinkedinUrl(), (organization != null) ? organization.getId() : null,
				(organization != null) ? organization.getName() : null,
				(organization != null) ? organization.getLinkedinUrl() : null));
	}

	/**
	 * 🔹 Gera o corpo da requisição, evitando valores nulos.
	 */
	private ApolloLinkedinModel.Body criarRequestBody(String email, String firstName, String lastName) {
		return new ApolloLinkedinModel.Body(API_KEY, email,
				Optional.ofNullable(firstName).filter(s -> !s.isEmpty()).orElse(null),
				Optional.ofNullable(lastName).filter(s -> !s.isEmpty()).orElse(null));
	}
}
