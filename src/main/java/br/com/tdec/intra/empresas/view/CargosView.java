package br.com.tdec.intra.empresas.view;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractViewLista;
import br.com.tdec.intra.empresas.model.Cargo;
import br.com.tdec.intra.empresas.services.CargoService;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;

@PageTitle("Cargos")
@Route(value = "cargos", layout = MainLayout.class)
@Getter
@Setter
@RolesAllowed("ROLE_EVERYONE")
public class CargosView extends AbstractViewLista<Cargo> {

	private static final long serialVersionUID = 1L;

	public CargosView(CargoService service) {
		super(Cargo.class, service);
	}

	@SuppressWarnings("unused")
	public void initGrid() {
		Column<Cargo> codigoColumn = grid.addColumn(Cargo::getCodigo).setHeader("Código").setSortable(true);
		codigoColumn.setComparator(Comparator.comparing(Cargo::getCodigo)).setKey("codigo");
		Column<Cargo> descricaoColumn = grid.addColumn(Cargo::getDescricao).setHeader("Descrição");
		Grid.Column<Cargo> autorColumn = grid.addColumn(Cargo::getAutor).setHeader("Autor");
		// autorColumn.setComparator(Comparator.comparing(Cargo::getAutor)).setKey("autor");
		Grid.Column<Cargo> criacaoColumn = grid.addColumn(new TextRenderer<>(item -> {
			if (item.getCriacao() != null) {
				return item.getCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			} else {
				return null; // Or any placeholder text you prefer
			}
		})).setHeader("Criação");

		// grid.asSingleSelect().addValueChangeListener(evt ->
		// openPageCargo(evt.getValue()));
	}

//	private void openPageCargo(Cargo cargo) {
//		getUI().ifPresent(ui -> ui.navigate("cargo/" + cargo.getUnid()));
//	}

//	public void updateGrid(Grid<Cargo> grid, String searchText) {
//		System.out.println("Search eh " + searchText);
//		LazyDataView<Cargo> dataView = grid.setItems(q -> captureWildcard(this.service
//				.findAllByCodigo(q.getOffset(), q.getLimit(), q.getSortOrders(), q.getFilter(), searchText).stream()));
//
//		dataView.setItemCountEstimate(8000);
//	}
//
//	@SuppressWarnings("unchecked")
//	private Stream<Cargo> captureWildcard(Stream<? extends AbstractModelDoc> stream) {
//		// This casting operation captures the wildcard and returns a stream of
//		// AbstractModelDoc - por causa do <E> no AbstractRepository
//		return (Stream<Cargo>) stream;
//	}

}
