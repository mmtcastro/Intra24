package br.com.tdec.intra.empresas.view;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.stream.Stream;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.LazyDataView;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractModelDoc;
import br.com.tdec.intra.abs.AbstractViewLista;
import br.com.tdec.intra.empresas.model.Empresa;
import br.com.tdec.intra.empresas.services.EmpresaService;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Getter;
import lombok.Setter;

@PageTitle("Empresas")
@Route(value = "empresas", layout = MainLayout.class)
@PermitAll
@Getter
@Setter
public class EmpresasView extends AbstractViewLista {

	private static final long serialVersionUID = 1L;
	private EmpresaService service = new EmpresaService();
	private Grid<Empresa> grid = new Grid<>(Empresa.class, false);
	private TextField search = new TextField("Buscar");

	public EmpresasView() {
		setSizeFull();
		// this.service = service;
		setSearch();
		setGrid();
		updateGrid(grid, "");

	}

	private void setGrid() {
		grid.setSizeFull();

		Column<Empresa> codigoColumn = grid.addColumn(Empresa::getCodigo).setHeader("Código").setSortable(true);
		codigoColumn.setComparator(Comparator.comparing(Empresa::getCodigo)).setKey("codigo");
		Column<Empresa> nomeColumn = grid.addColumn(Empresa::getNome).setHeader("Nome");
		Column<Empresa> statusColumn = grid.addColumn(Empresa::getStatus).setHeader("Status");
		Column<Empresa> estadoColumn = grid.addColumn(Empresa::getEstado).setHeader("UF");
		Column<Empresa> cgcColumn = grid.addColumn(Empresa::getCgc).setHeader("CNPJ");
		Grid.Column<Empresa> autorColumn = grid.addColumn(Empresa::getAutor).setHeader("Autor");
		// autorColumn.setComparator(Comparator.comparing(Empresa::getAutor)).setKey("autor");
		Grid.Column<Empresa> criacaoColumn = grid.addColumn(new TextRenderer<>(item -> {
			if (item.getCriacao() != null) {
				return item.getCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			} else {
				return null; // Or any placeholder text you prefer
			}
		})).setHeader("Criação");

		grid.asSingleSelect().addValueChangeListener(evt -> openPage(evt.getValue()));
		add(grid);
	}

	private void setSearch() {
		search.setPlaceholder("buscar...");
		search.setClearButtonVisible(true);
		search.setValueChangeMode(ValueChangeMode.LAZY);
		search.addValueChangeListener(e -> updateGrid(grid, search.getValue()));
		add(search);

	}

	private void openPage(Empresa empresa) {
		getUI().ifPresent(ui -> ui.navigate("vertical/" + empresa.getUnid()));
	}

	public void updateGrid(Grid<Empresa> grid, String searchText) {
		LazyDataView<Empresa> dataView = grid.setItems(q -> captureWildcard(this.service
				.findAllByCodigo(q.getOffset(), q.getLimit(), q.getSortOrders(), q.getFilter(), searchText).stream()));

		dataView.setItemCountEstimate(8000);
	}

	@SuppressWarnings("unchecked")
	private Stream<Empresa> captureWildcard(Stream<? extends AbstractModelDoc> stream) {
		// This casting operation captures the wildcard and returns a stream of
		// AbstractModelDoc - por causa do <E> no AbstractRepository
		return (Stream<Empresa>) stream;
	}

}
