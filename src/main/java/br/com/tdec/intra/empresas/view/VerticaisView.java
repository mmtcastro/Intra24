package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractViewLista;
import br.com.tdec.intra.empresas.services.VerticalService;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Getter;
import lombok.Setter;

@PageTitle("Verticais")
@Route(value = "verticais", layout = MainLayout.class)
@PermitAll
@Getter
@Setter
public class VerticaisView extends AbstractViewLista {

	private static final long serialVersionUID = 1L;
	private VerticalService service;

	public VerticaisView(VerticalService service) {

		this.service = service;

	}

}
