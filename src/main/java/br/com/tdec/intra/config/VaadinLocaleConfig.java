package br.com.tdec.intra.config;

import java.util.Locale;

import org.springframework.stereotype.Component;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinSession;

@Component
public class VaadinLocaleConfig implements VaadinServiceInitListener {

	/**
	 * Serve para aplicar o formato locale brasileiro por todo o Vaadin. O Vaadin
	 * nao usa as variaveis Locale da sessao do Spring Boot
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void serviceInit(ServiceInitEvent event) {
		event.getSource().addSessionInitListener(sessionInitEvent -> {
			// Define o locale padrão para a sessão do Vaadin
			VaadinSession session = sessionInitEvent.getSession();
			session.setLocale(Locale.forLanguageTag("pt-BR"));
		});
	}
}
