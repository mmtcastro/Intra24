package br.com.tdec.intra.empresas.view;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.stream.Stream;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.LazyDataView;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractModelDoc;
import br.com.tdec.intra.empresas.model.Vertical;
import br.com.tdec.intra.empresas.services.VerticalService;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;

@PageTitle("Verticais")
@Route(value = "verticais", layout = MainLayout.class)
@Getter
@Setter
@RolesAllowed("ROLE_EVERYONE")
public class VerticaisView extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	private VerticalService service = new VerticalService();
	private Grid<Vertical> grid = new Grid<>(Vertical.class, false);

	public VerticaisView() {
		setSizeFull();
		// this.service = service;
		setGrid();
		updateGrid(grid, "");
		add(grid);

	}

	private void setGrid() {
		grid.setSizeFull();
		Column<Vertical> codigoColumn = grid.addColumn(Vertical::getCodigo).setHeader("Código").setSortable(true);
		codigoColumn.setComparator(Comparator.comparing(Vertical::getCodigo)).setKey("codigo");
		Column<Vertical> descricaoColumn = grid.addColumn(Vertical::getDescricao).setHeader("Descrição");
		Grid.Column<Vertical> autorColumn = grid.addColumn(Vertical::getAutor).setHeader("Autor");
		// autorColumn.setComparator(Comparator.comparing(Vertical::getAutor)).setKey("autor");
		Grid.Column<Vertical> criacaoColumn = grid.addColumn(new TextRenderer<>(item -> {
			if (item.getCriacao() != null) {
				return item.getCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			} else {
				return null; // Or any placeholder text you prefer
			}
		})).setHeader("Criação");

		grid.asSingleSelect().addValueChangeListener(evt -> openPage(evt.getValue()));
	}

	private void openPage(Vertical vertical) {
		getUI().ifPresent(ui -> ui.navigate("vertical/" + vertical.getUnid()));
	}

	public void updateGrid(Grid<Vertical> grid, String searchText) {
		LazyDataView<Vertical> dataView = grid.setItems(q -> captureWildcard(this.service
				.findAllByCodigo(q.getOffset(), q.getLimit(), q.getSortOrders(), q.getFilter(), searchText).stream()));

		dataView.setItemCountEstimate(8000);
	}

	@SuppressWarnings("unchecked")
	private Stream<Vertical> captureWildcard(Stream<? extends AbstractModelDoc> stream) {
		// This casting operation captures the wildcard and returns a stream of
		// AbstractModelDoc - por causa do <E> no AbstractRepository
		return (Stream<Vertical>) stream;
	}

}
