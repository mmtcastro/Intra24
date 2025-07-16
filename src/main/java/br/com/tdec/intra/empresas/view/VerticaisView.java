package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractViewLista;
import br.com.tdec.intra.empresas.model.Vertical;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@PageTitle("Verticais")
@Route(value = "verticais", layout = MainLayout.class)
@Getter
@Setter
@RolesAllowed("ROLE_EVERYONE")
public class VerticaisView extends AbstractViewLista<Vertical> {

    @Serial
    private static final long serialVersionUID = 1L;

	public VerticaisView() {
		super();
	}

	public void initGrid() {
		grid.addColumn(new ComponentRenderer<Anchor, Vertical>(vertical -> {
			Anchor link = new Anchor("vertical/" + vertical.getUnid(), vertical.getCodigo());
			link.getElement().setAttribute("router-link", true); // Permite navegação sem recarregar
			return link;
		}))//
				.setHeader("Código")//
				.setSortable(true)//
				.setKey("codigo")//
				.setResizable(true);//
		grid.addColumn(Vertical::getDescricao).setHeader("Descrição");
	}

}
