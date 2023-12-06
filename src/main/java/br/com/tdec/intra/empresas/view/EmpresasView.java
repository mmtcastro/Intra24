package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

import br.com.tdec.intra.abs.AbstractView;
import br.com.tdec.intra.empresas.model.Empresa;
import br.com.tdec.intra.empresas.repositories.EmpresaRepository;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Getter;
import lombok.Setter;

@PageTitle("Empresas")
@Route(value = "empresas", layout = MainLayout.class)
@PermitAll
@Getter
@Setter
public class EmpresasView extends AbstractView {

	private static final long serialVersionUID = 1L;
	private final EmpresaRepository repository;

	public EmpresasView(EmpresaRepository repository) {
		this.repository = repository;
		System.out.println(repository.toString());
		add(new H1("Empresas"));

		Button buttonGetAllEmpresas = new Button("getAllEmpresas", e -> repository.getAllEmpresas());
		add(buttonGetAllEmpresas);
		initBasicGrid();
	}

	public void initBasicGrid() {
		var grid = new Grid<>(Empresa.class);
		grid.setColumns("codigo", "id");
		// grid.setItems(repository.getAllEmpresas());
		grid.setItems(VaadinSpringDataHelpers.fromPagingRepository(repository));
		add(grid);

	}

}
