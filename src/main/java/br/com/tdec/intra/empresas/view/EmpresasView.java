package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.empresas.repositories.EmpresaRepository;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Data;

@PageTitle("Empresas")
@Route(value = "empresas", layout = MainLayout.class)
@Data
@PermitAll
public class EmpresasView extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	private final EmpresaRepository empresasDao;

	public EmpresasView(EmpresaRepository empresasDao) {
		this.empresasDao = empresasDao;
		System.out.println(empresasDao.toString());
		add(new H1("Empresas"));

		Button buttonGetAllEmpresas = new Button("getAllEmpresas", e -> empresasDao.getAllEmpresas());
		add(buttonGetAllEmpresas);
	}

}
