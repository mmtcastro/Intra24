package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractViewLista;
import br.com.tdec.intra.empresas.model.OrigemCliente;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
@PageTitle("Origens dos Clientes")
@Route(value = "origensCliente", layout = MainLayout.class)
@RolesAllowed("ROLE_EVERYONE")
public class OrigensClientesView extends AbstractViewLista<OrigemCliente> {

    @Serial
    private static final long serialVersionUID = 1L;

	public OrigensClientesView() {
		super();

	}

}
