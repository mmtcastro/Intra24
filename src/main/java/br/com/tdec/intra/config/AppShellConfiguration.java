package br.com.tdec.intra.config;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

@Component
@Theme(value = "intra24")
@PWA(name = "My Application", shortName = "App", iconPath = "icons/icons8-mapa-mental-48.png")
public class AppShellConfiguration implements AppShellConfigurator {
	/*
	 * no final eu crie esta classe só para mudar o favicon
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void configurePage(AppShellSettings settings) {
		settings.setPageTitle("Intranet TDec");
		settings.addFavIcon("icon", "icons/icons8-mapa-mental-48.png", "48x48");
	}

}
