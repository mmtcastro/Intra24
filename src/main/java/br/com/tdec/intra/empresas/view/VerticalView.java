package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractViewDoc;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@PageTitle("Vertical")
@Route(value = "vertical", layout = MainLayout.class)
@PermitAll
public class VerticalView extends AbstractViewDoc {

	private static final long serialVersionUID = 1L;

//	public VerticalView(AbstractModelDoc model) {
//		super(model);
//	}

}
