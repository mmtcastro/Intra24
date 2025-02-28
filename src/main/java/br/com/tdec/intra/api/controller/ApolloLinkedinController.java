package br.com.tdec.intra.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.tdec.intra.api.model.ApolloLinkedinModel;
import br.com.tdec.intra.api.services.ApolloLinkedinService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/linkedin")
public class ApolloLinkedinController {

	private final ApolloLinkedinService apolloLinkedinService;

	public ApolloLinkedinController(ApolloLinkedinService apolloLinkedinService) {
		this.apolloLinkedinService = apolloLinkedinService;
	}

	/**
	 * 🔹 Endpoint para buscar um contato no LinkedIn pelo email (opcionalmente nome
	 * e sobrenome)
	 */
	@GetMapping("/buscar")
	public Mono<ResponseEntity<ApolloLinkedinModel>> buscarLinkedin(@RequestParam String email,
			@RequestParam(required = false) String firstName, @RequestParam(required = false) String lastName) {

		return apolloLinkedinService.buscarApolloLinkedinModel(email, firstName, lastName).map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.notFound().build())
				.doOnError(error -> System.err.println("❌ Erro no Controller: " + error.getMessage()));
	}

}
