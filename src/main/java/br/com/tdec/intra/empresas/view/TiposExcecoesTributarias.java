package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractViewLista;
import br.com.tdec.intra.empresas.model.TipoExcecaoTributaria;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PageTitle("Tipos de Exceção Tributária")
@Route(value = "tiposexcecoestributarias", layout = MainLayout.class)
@RolesAllowed("ROLE_EVERYONE")
public class TiposExcecoesTributarias extends AbstractViewLista<TipoExcecaoTributaria> {

	private static final long serialVersionUID = 1L;

	public TiposExcecoesTributarias() {
		super();
	}

}
