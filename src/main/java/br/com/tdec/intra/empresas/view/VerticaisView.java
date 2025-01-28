package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractViewLista;
import br.com.tdec.intra.empresas.model.Vertical;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;

@PageTitle("Verticais")
@Route(value = "verticais", layout = MainLayout.class)
@Getter
@Setter
@RolesAllowed("ROLE_EVERYONE")
public class VerticaisView extends AbstractViewLista<Vertical> {

	private static final long serialVersionUID = 1L;

	public VerticaisView() {
		super();
	}

}
