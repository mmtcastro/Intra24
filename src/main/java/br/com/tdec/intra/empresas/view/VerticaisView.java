package br.com.tdec.intra.empresas.view;

import java.util.Comparator;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.LazyDataView;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractModelDoc;
import br.com.tdec.intra.abs.AbstractViewLista;
import br.com.tdec.intra.empresas.repositories.VerticalRepository;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Data;
import lombok.EqualsAndHashCode;

@PageTitle("Verticais")
@Route(value = "verticais", layout = MainLayout.class)
@PermitAll
@Data
@EqualsAndHashCode(callSuper = false)
public class VerticaisView extends AbstractViewLista {

	private static final long serialVersionUID = 1L;
	// private VerticalRepository repository;
	private Grid<AbstractModelDoc> gridVertical;
	private TextField filterTextVertical;
	private Button criarVertical;
	private HorizontalLayout toolbarVertical;

	public VerticaisView(VerticalRepository repository) {
		super(repository);
		// this.repository = repository;
		System.out.println(repository);
		add(new H1("Verticais"));
		criarVertical = new Button("Criar Documento", e -> criarVertical());
		initGridVertical();
		initFilterVertical();

		toolbarVertical = new HorizontalLayout(filterTextVertical, criarVertical);
		toolbarVertical = new HorizontalLayout(filterTextVertical, criarVertical);

		add(toolbarVertical, gridVertical);
		updateGrid(gridVertical);

	}

	public void initFilterVertical() {
		filterTextVertical = new TextField();
		filterTextVertical.setPlaceholder("filtro...");
		filterTextVertical.setClearButtonVisible(true);
		filterTextVertical.setValueChangeMode(ValueChangeMode.LAZY);
		filterTextVertical.addValueChangeListener(e -> updateGrid(gridVertical));
	}

	public void initGridVertical() {
		gridVertical = new Grid<>();

		Column<AbstractModelDoc> codigoColumn = gridVertical.addColumn(AbstractModelDoc::getCodigo).setHeader("Código");
		codigoColumn.setComparator(Comparator.comparing(AbstractModelDoc::getCodigo)).setKey("codigo");
		Grid.Column<AbstractModelDoc> idColumn = gridVertical.addColumn(AbstractModelDoc::getId).setHeader("Id");
		idColumn.setComparator(Comparator.comparing(AbstractModelDoc::getId)).setKey("id");

	}

	public void updateGrid(Grid<AbstractModelDoc> grid) {
		LazyDataView<AbstractModelDoc> dataView = grid.setItems(q -> this.repository
				.findAll(q.getOffset(), q.getLimit(), q.getSortOrders(), q.getFilter(), filterTextVertical.getValue())
				.stream());

		dataView.setItemCountEstimate(8000);
	}

	public void criarVertical() {

	}

}
