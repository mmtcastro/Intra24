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
import br.com.tdec.intra.empresas.model.Vertical;
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
	private VerticalRepository repository;
	private Grid<Vertical> gridVertical;
	private TextField filterTextVertical;
	private Button criarVertical;
	private HorizontalLayout toolbarVertical;

	public VerticaisView(VerticalRepository repository) {
		// super(repository);
		// super();
		this.repository = repository;
		System.out.println(repository);
		add(new H1("Verticais"));
		criarVertical = new Button("Criar Documento", e -> criarVertical());
		initGridVertical();
		initFilterVertical();

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

		Column<Vertical> codigoColumn = gridVertical.addColumn(Vertical::getCodigo).setHeader("Código")
				.setSortable(true);
		codigoColumn.setComparator(Comparator.comparing(Vertical::getCodigo)).setKey("codigo");
		Grid.Column<Vertical> idColumn = gridVertical.addColumn(Vertical::getId).setHeader("Id");
		idColumn.setComparator(Comparator.comparing(Vertical::getId)).setKey("id");
//		Grid.Column<AbstractModelDoc> criacaoLocalDateTime = gridVertical
//				.addColumn(new LocalDateTimeRenderer<>(AbstractModelDoc::getCriacao, "dd/MM/yyyy HH:mm:ss"))
//				.setHeader("Criação");
//		Grid.Column<Vertical> criacaoColumn = gridVertical
//				.addColumn(new TextRenderer<>(
//						item -> item.getCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss Z"))))
//				.setHeader("Criação");
//		criacaoColumn.setComparator(Comparator.comparing(Vertical::getCriacao)).setKey("criacao");
//
//		Grid.Column<Vertical> valorColumn = gridVertical.addColumn(AbstractModelDoc::getValor).setHeader("Valor");
//		valorColumn.setComparator(Comparator.comparing(Vertical::getId)).setKey("valor");

		gridVertical.asSingleSelect().addValueChangeListener(evt -> editVertical(evt.getValue()));
	}

	public void updateGrid(Grid<?> grid) {
		LazyDataView<Vertical> dataView = gridVertical.setItems(q -> this.repository.findAllVerticais(q.getOffset(),
				q.getLimit(), q.getSortOrders(), q.getFilter(), filterTextVertical.getValue()).stream());

		dataView.setItemCountEstimate(8000);
	}

	public void criarVertical() {

	}

	private void editVertical(AbstractModelDoc abstractModelDoc) {
		if (abstractModelDoc == null) {
			// closeEditor();
		} else {
			// grupoEconomicoForm.setGrupoEconomico(grupoEconomico);
			// grupoEconomicoForm.setVisible(true);
			// addClassName("editing-grupoEconomico");
		}
	}

}
