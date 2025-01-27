package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractViewLista;
import br.com.tdec.intra.empresas.model.Vertical;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;

@PageTitle("Verticais")
@Route(value = "verticais", layout = MainLayout.class)
@Getter
@Setter
@RolesAllowed("ROLE_EVERYONE")
public class VerticaisView extends AbstractViewLista<Vertical> {

	private static final long serialVersionUID = 1L;

	public VerticaisView() {
		super();
	}

//	private static final long serialVersionUID = 1L;
//	private final VerticalService service;
//	private Button novoButton = new Button("Nova Vertical", e -> novo());
//	private Grid<Vertical> grid = new Grid<>(Vertical.class, false);
//	private TextField search = new TextField("Buscar");
//
//	public VerticaisView(VerticalService service) {
//		setSizeFull();
//		this.service = service;
//		setSearch();
//		setGrid();
//		updateGrid(grid, "");
//	}
//
//	private void setSearch() {
//		search.setPlaceholder("buscar...");
//		search.setClearButtonVisible(true);
//		search.setValueChangeMode(ValueChangeMode.LAZY);
//		search.addValueChangeListener(e -> updateGrid(grid, search.getValue()));
//		var horizontalLayout = new HorizontalLayout();
//		horizontalLayout.add(search, novoButton);
//		horizontalLayout.setVerticalComponentAlignment(Alignment.END, novoButton);
//		add(horizontalLayout);
//	}
//
//	private void novo() {
//		getUI().ifPresent(ui -> ui.navigate("vertical/"));
//	}
//
//	public void initGrid() {
//		// grid.setSizeFull();
//		Column<Vertical> codigoColumn = grid.addColumn(Vertical::getCodigo).setHeader("Código").setSortable(true);
//		codigoColumn.setComparator(Comparator.comparing(Vertical::getCodigo)).setKey("codigo");
//		Column<Vertical> descricaoColumn = grid.addColumn(Vertical::getDescricao).setHeader("Descrição");
//		Grid.Column<Vertical> autorColumn = grid.addColumn(Vertical::getAutor).setHeader("Autor");
//		// autorColumn.setComparator(Comparator.comparing(Vertical::getAutor)).setKey("autor");
//		Grid.Column<Vertical> criacaoColumn = grid.addColumn(new TextRenderer<>(item -> {
//			if (item.getCriacao() != null) {
//				return item.getCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
//			} else {
//				return null; // Or any placeholder text you prefer
//			}
//		})).setHeader("Criação");
//		Grid.Column<Vertical> unidColumn = grid.addColumn(Vertical::getUnid).setHeader("Unid");
//
////		grid.asSingleSelect().addValueChangeListener(evt -> openPage(evt.getValue()));
//		// add(grid);
//
//	}
//
//	private void openPage(Vertical vertical) {
//		getUI().ifPresent(ui -> ui.navigate("vertical/" + vertical.getUnid()));
//	}
//
//	public void updateGrid(Grid<Vertical> grid, String searchText) {
//		LazyDataView<Vertical> dataView = grid.setItems(q -> captureWildcard(this.service
//				.findAllByCodigo(q.getOffset(), q.getLimit(), q.getSortOrders(), q.getFilter(), searchText).stream()));
//
//		dataView.setItemCountEstimate(8000);
//	}
//
//	@SuppressWarnings("unchecked")
//	private Stream<Vertical> captureWildcard(Stream<? extends AbstractModelDoc> stream) {
//		// This casting operation captures the wildcard and returns a stream of
//		// AbstractModelDoc - por causa do <E> no AbstractRepository
//		return (Stream<Vertical>) stream;
//	}

}
