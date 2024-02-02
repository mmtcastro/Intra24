package br.com.tdec.intra.empresas.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.data.provider.QuerySortOrder;

import br.com.tdec.intra.config.WebClientConfig;
import br.com.tdec.intra.empresas.model.GrupoEconomico;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

@Service
@Getter
@Setter
public class GrupoEconomicoService {

	protected final WebClient webClient;
	protected String token;

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

	public List<GrupoEconomico> getGruposEconomicosSync() {
		return webClient.get().uri("/lists/GruposEconomicos?dataSource=empresasscope&count=10")
				.header("Authorization", "Bearer " + token).retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<GrupoEconomico>>() {
				})//
				.block();
		// .doOnNext(list -> System.out.println("Received GrupoEconomico list: " +
		// list)); // Log the list
	}

	public List<GrupoEconomico> getGruposEconomicosSync(String count) {
		long tempoInicio = System.nanoTime();
		List<GrupoEconomico> ret = webClient.get()
				.uri("/lists/GruposEconomicos?dataSource=empresasscope&count=" + count)
				.header("Authorization", "Bearer " + token).retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<GrupoEconomico>>() {
				})//
				.block();
		// .doOnNext(list -> System.out.println("Received GrupoEconomico list: " +
		// list)); // Log the list
		long tempoFim = System.nanoTime();
		double duracaoSegundos = (tempoFim - tempoInicio) / 1_000_000_000.0; // Convertendo de nanossegundos para
																				// segundos

		System.out.println("Tempo de execução: " + duracaoSegundos + " segundos");
		return ret;
	}

	public List<GrupoEconomico> findAllByCodigo(int offset, int count, List<QuerySortOrder> sortOrders,
			Optional<Void> filter, String search) {
		List<GrupoEconomico> ret = new ArrayList<GrupoEconomico>();
		count = 50; // nao consegui fazer funcionar o limit automaticamente.
		String direction = "";
		if (sortOrders != null) {
			for (QuerySortOrder sortOrder : sortOrders) {
				System.out.println("--- Sorting ----");
				System.out.println("Sorted: " + sortOrder.getSorted());
				System.out.println("Direction:  " + sortOrder.getDirection());
			}
			if (sortOrders.size() > 0) {
				if (sortOrders.get(0).getDirection() != null && sortOrders.get(0).getDirection().equals("ASCENDING")) {
					direction = "&direction=asc";
				} else {
					direction = "&direction=desc";
				}
			}
		}

		ret = webClient.get()
				.uri("/lists/GruposEconomicos?dataSource=empresasscope&count=" + count + direction
						+ "&column=Codigo&start=" + offset + "&startsWith=" + search)
				.header("Authorization", "Bearer " + token).retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<GrupoEconomico>>() {
				})//
				.block();

		return ret;
	}

	public GrupoEconomico findById(String id) {
		GrupoEconomico grupoEconomico = null;

		return grupoEconomico;

	}

	public GrupoEconomico findByUnid(String unid) {
		GrupoEconomico grupoEconomico = null;
		// /document/+49E2429B9AD2507B832580AE0063377C?dataSource=empresasscope&computeWithForm=false&richTextAs=markdown&mode=default
		try {
			grupoEconomico = webClient.get()
					.uri("/document/" + unid
							+ "?dataSource=empresasscope&computeWithForm=false&richTextAs=markdown&mode=default")
					.header("Authorization", "Bearer " + token).retrieve().bodyToMono(GrupoEconomico.class).block();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return grupoEconomico;

	}

}
