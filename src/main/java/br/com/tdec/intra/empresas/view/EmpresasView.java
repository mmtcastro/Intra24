package br.com.tdec.intra.empresas.view;

import org.springframework.web.reactive.function.client.WebClient;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractViewLista;
import br.com.tdec.intra.config.WebClientConfig;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Data;
import lombok.EqualsAndHashCode;

@PageTitle("Empresas")
@Route(value = "empresas", layout = MainLayout.class)
@PermitAll
@Data
@EqualsAndHashCode(callSuper = true)
public class EmpresasView extends AbstractViewLista {

	private static final long serialVersionUID = 1L;
	private WebClient webClient;
	private String token;

	public EmpresasView(WebClientConfig webClientConfig) {
		this.webClient = webClientConfig.getWebClient();
		this.token = webClientConfig.getToken();

	}

}
