package br.com.tdec.intra.directory.services;

import org.springframework.web.reactive.function.client.WebClient;

import br.com.tdec.intra.config.WebClientConfig;
import lombok.Getter;
import lombok.Setter;

//@Service
@Getter
@Setter
public class DirectoryService {
	protected final WebClient webClient;
	protected String token;
	protected final String scope = "names";

	public DirectoryService(WebClientConfig webClientConfig) {
		this.webClient = webClientConfig.getWebClient();
		this.token = webClientConfig.getToken();
	}

}
