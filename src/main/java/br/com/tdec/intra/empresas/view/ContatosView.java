package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractViewLista;
import br.com.tdec.intra.empresas.model.Contato;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
@Route(value = "contatos", layout = MainLayout.class)
@PageTitle("Contatos")
@RolesAllowed("ROLE_EVERYONE")
public class ContatosView extends AbstractViewLista<Contato> {

    @Serial
    private static final long serialVersionUID = 1L;

}
