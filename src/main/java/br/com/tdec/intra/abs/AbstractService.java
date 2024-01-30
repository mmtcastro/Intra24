package br.com.tdec.intra.abs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.web.reactive.function.client.WebClient;

import br.com.tdec.intra.config.WebClientConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractService {
	protected final WebClient webClient;
	protected String token;
	protected static ExecutorService executorService = Executors.newFixedThreadPool(10);

	public AbstractService(WebClientConfig webClientConfig) {
		System.out.println("AbstractService constructor - " + this.getClass().getName());
		this.webClient = webClientConfig.getWebClient();
		this.token = webClientConfig.getToken();
		webClientConfig.getTokenMono().subscribe(t -> this.token = t);
	}

}
