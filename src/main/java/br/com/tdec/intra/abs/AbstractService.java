package br.com.tdec.intra.abs;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.web.reactive.function.client.WebClient;

import com.vaadin.flow.data.provider.QuerySortOrder;

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
		this.webClient = webClientConfig.getWebClient();
		this.token = webClientConfig.getToken();
		webClientConfig.getTokenMono().subscribe(t -> this.token = t);
	}

	protected abstract Collection<AbstractModelDoc> findAllByCodigo(int offset, int limit,
			List<QuerySortOrder> sortOrders, Optional<Void> filter, String searchText);

}
