package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.empresas.services.GrupoEconomicoService;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Getter;
import lombok.Setter;

@PermitAll
@Getter
@Setter
@Route(value = "gruposeconomico", layout = MainLayout.class)
@PageTitle("Grupo Econômico")
public class GrupoEconomicoView extends FormLayout {
	private static final long serialVersionUID = 1L;
	private GrupoEconomicoService service;
	private String id;

	public GrupoEconomicoView(GrupoEconomicoService service) {
		this.service = service;
	}

	public void beforeEnter(BeforeEnterEvent event) {
		// Extracting the URL parameter id
		event.getRouteParameters().get("id").ifPresentOrElse(id -> {
			this.id = id;
			service.findById(id); // Method to load the GrupoEconomico details
		}, () -> {
			Notification.show("Grupo Econômico ID not provided.", 3000, Notification.Position.MIDDLE);
			event.forwardTo(MainLayout.class); // Redirect or handle missing id case
		});
	}

}
