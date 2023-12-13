package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractViewLista;
import br.com.tdec.intra.empresas.repositories.EmpresaRepository;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Data;
import lombok.EqualsAndHashCode;

@PageTitle("Empresas")
@Route(value = "empresas", layout = MainLayout.class)
@PermitAll
@Data
@EqualsAndHashCode(callSuper = true)
public class EmpresasView extends AbstractViewLista {

	private static final long serialVersionUID = 1L;

	public EmpresasView(EmpresaRepository repository) {
		super(repository);
		add(new H1("Empresas"));
		initGridDefault();

	}

}
