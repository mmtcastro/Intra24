package br.com.tdec.intra.empresas.services;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.tdec.intra.config.WebClientConfig;
import br.com.tdec.intra.empresas.model.GrupoEconomico;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Getter
@Setter
public class GrupoEconomicoService {
	private final WebClient webClient;
	private  String token;
	private static ExecutorService executorService = Executors.newFixedThreadPool(10);

	public GrupoEconomicoService(WebClientConfig webClientConfig) {
		this.webClient = webClientConfig.getWebClient();
		this.token = webClientConfig.getToken();
		webClientConfig.getTokenMono().subscribe(t -> this.token = t);
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
	    return webClient.get()
	            .uri("/lists/GruposEconomicos?dataSource=empresasscope&count=10")
	            .header("Authorization", "Bearer " + token)
	            .retrieve()
	            .bodyToMono(new ParameterizedTypeReference<List<GrupoEconomico>>() {})//
	            .block();
	           // .doOnNext(list -> System.out.println("Received GrupoEconomico list: " + list)); // Log the list
	}
	
	public List<GrupoEconomico> getGruposEconomicosSync(String count) {
		long tempoInicio = System.nanoTime();
		 List<GrupoEconomico> ret = webClient.get()
	            .uri("/lists/GruposEconomicos?dataSource=empresasscope&count="+count)
	            .header("Authorization", "Bearer " + token)
	            .retrieve()
	            .bodyToMono(new ParameterizedTypeReference<List<GrupoEconomico>>() {})//
	            .block();
	           // .doOnNext(list -> System.out.println("Received GrupoEconomico list: " + list)); // Log the list
		 long tempoFim = System.nanoTime();
	        double duracaoSegundos = (tempoFim - tempoInicio) / 1_000_000_000.0; // Convertendo de nanossegundos para segundos

	        System.out.println("Tempo de execução: " + duracaoSegundos + " segundos");
		 return ret;
	}
	
	public Mono<List<GrupoEconomico>> getGruposEconomicosReactive() {
		return (Mono<List<GrupoEconomico>>) webClient.get()
				.uri("/lists/GruposEconomicos?dataSource=empresasscope&count=10")
				.header("Authorization", "Bearer " + token).retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<GrupoEconomico>>() {})//
				.publishOn(Schedulers.fromExecutor(executorService));
				//.doOnNext(list -> System.out.println("Received GrupoEconomico list: " + list)); // Log the list
	}
	
	

}
