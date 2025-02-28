package br.com.tdec.intra.api.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import br.com.tdec.intra.api.model.Viacep;

@Service
public class ViacepService {

	private static final String BASE_URL = "https://viacep.com.br/ws/";

	public static Viacep findCep(String cep) {
		try {
			WebClient webClient = WebClient.create(BASE_URL);

			return webClient.get().uri("/{cep}/json/", cep.replaceAll("[^0-9]", "")) // Remove caracteres não numéricos
					.retrieve().bodyToMono(Viacep.class).block(); // Bloqueia e aguarda a resposta
		} catch (Exception e) {
			e.printStackTrace();
			return null; // Retorna null se houver erro na requisição
		}
	}
}
