package br.com.tdec.intra.abs;

import java.util.stream.Stream;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.LazyDataView;
import com.vaadin.flow.data.value.ValueChangeMode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractViewLista<T extends AbstractModelDoc> extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	protected AbstractService<T> service;
	protected TextField search = new TextField("Buscar");
	protected Grid<T> grid;
	protected T model;
	protected Class<T> modelType;
	protected Button novoButton;

	public AbstractViewLista(Class<T> modelType, AbstractService<T> service) {
		this.service = service;
		this.modelType = modelType;
		model = createModel(modelType);
		setSizeFull();
		setSearch();
		setGrid();
		updateGrid(grid, "");

	}

	private void setSearch() {
		search.setPlaceholder("buscar...");
		search.setClearButtonVisible(true);
		search.setValueChangeMode(ValueChangeMode.LAZY);
		search.addValueChangeListener(e -> updateGrid(grid, search.getValue()));
		novoButton = new Button("Criar " + model.getClass().getSimpleName(), e -> novo());
		var horizontalLayout = new HorizontalLayout();
		horizontalLayout.add(search, novoButton);
		horizontalLayout.setVerticalComponentAlignment(Alignment.END, novoButton);
		add(horizontalLayout);
	}

	public void setGrid() {
		setSizeFull();
		grid = new Grid<>(modelType, false);
		grid.setSizeFull();
		grid.addClassName("abstract-view-lista-grid");
		grid.asSingleSelect().addValueChangeListener(evt -> openPage(evt.getValue()));
		initGrid();
		add(grid);
	}

	public abstract void initGrid();

	private void openPage(T model) {
		getUI().ifPresent(ui -> ui.navigate(model.getClass().getSimpleName().toLowerCase() + "/" + model.getUnid()));
	}

	private void novo() {
		getUI().ifPresent(ui -> ui.navigate(model.getClass().getSimpleName().toLowerCase() + "/"));
	}

	public void updateGrid(Grid<T> grid, String searchText) {
		LazyDataView<T> dataView = grid.setItems(q -> captureWildcard(this.service
				.findAllByCodigo(q.getOffset(), q.getLimit(), q.getSortOrders(), q.getFilter(), searchText).stream()));

		dataView.setItemCountEstimate(8000);
	}

	@SuppressWarnings("unchecked")
	private Stream<T> captureWildcard(Stream<? extends AbstractModelDoc> stream) {
		// This casting operation captures the wildcard and returns a stream of
		// AbstractModelDoc - por causa do <E> no AbstractRepository
		return (Stream<T>) stream;
	}

	protected T createModel(Class<T> modelType) {
		try {
			return modelType.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Nao foi possivel criar o modelo - " + modelType, e);
		}
	}

}
