package br.com.tdec.intra.empresas.services;

import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.tdec.intra.config.WebClientConfig;
import br.com.tdec.intra.empresas.model.GrupoEconomico;
import lombok.Data;
import reactor.core.publisher.Mono;

@Service
@Data
public class GrupoEconomicoService {
	private final WebClient webClient;
	private final String token;

	public GrupoEconomicoService(WebClientConfig webClientConfig) {
		this.webClient = webClientConfig.getWebClient();
		this.token = webClientConfig.getToken();
	}

	public Mono<List<GrupoEconomico>> getGruposEconomicos() {
		return webClient.get().uri("/lists/GruposEconomicos?dataSource=empresasscope&count=10")
				.header("Authorization", "Bearer " + token).retrieve().bodyToMono(String.class)
				.doOnNext(json -> System.out.println("Received JSON: " + json)) // Log it
				.map(json -> {
					try {
						ObjectMapper objectMapper = new ObjectMapper();
						return objectMapper.readValue(json, new TypeReference<List<GrupoEconomico>>() {
						});
					} catch (JsonProcessingException e) {
						throw new RuntimeException("Error parsing JSON", e);
					}
				});
	}

	public Mono<List<GrupoEconomico>> processGruposEconomicos() {
		return getGruposEconomicos().flatMapIterable(Function.identity()).map(grupoEconomico -> {
			// You can process each GrupoEconomico here if needed
			return grupoEconomico;
		}).collectList();
	}
}
