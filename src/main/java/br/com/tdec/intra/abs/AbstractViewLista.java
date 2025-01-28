package br.com.tdec.intra.abs;

import java.lang.reflect.ParameterizedType;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.LazyDataView;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.value.ValueChangeMode;

import br.com.tdec.intra.config.ApplicationContextProvider;
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

	@SuppressWarnings("unchecked")
	public AbstractViewLista() {
		this.modelType = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		model = createModel(modelType);
		// Buscar dinamicamente o Service correspondente ao modelo
		this.service = findService();

		setSizeFull();
		setSearch();
		setGrid();
		updateGrid(grid, "");

	}

	@SuppressWarnings("unchecked")
	private AbstractService<T> findService() {
		// Obter o nome do modelo
		String modelName = modelType.getSimpleName();

		// Construir o nome do serviço a partir do nome do modelo
		String serviceName = modelName + "Service";

		// Converter para o formato camelCase (primeira letra minúscula)
		serviceName = Character.toLowerCase(serviceName.charAt(0)) + serviceName.substring(1);

		// Use the ApplicationContextProvider to get the ApplicationContext
		ApplicationContext context = ApplicationContextProvider.getApplicationContext();
		if (context == null) {
			throw new IllegalStateException("ApplicationContext is not initialized.");
		}

		Object serviceBean = context.getBean(serviceName);

		if (serviceBean instanceof AbstractService) {
			return (AbstractService<T>) serviceBean;
		} else {
			throw new IllegalStateException("Serviço não encontrado ou não é do tipo AbstractService: " + serviceName);
		}
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

	public void initGrid() {
		// Coluna para Código
		Grid.Column<T> codigoColumn = grid.addColumn(model -> model.getCodigo()).setHeader("Código").setSortable(true);
		codigoColumn.setComparator(Comparator.comparing(T::getCodigo)).setKey("codigo");

		// Coluna para Descrição
		Grid.Column<T> descricaoColumn = grid.addColumn(model -> model.getDescricao()).setHeader("Descrição")
				.setSortable(true);
		descricaoColumn.setComparator(Comparator.comparing(T::getDescricao)).setKey("descricao");

		// Coluna para Autor
		Grid.Column<T> autorColumn = grid.addColumn(model -> model.getAutor()).setHeader("Autor").setSortable(true);
		autorColumn.setComparator(Comparator.comparing(T::getAutor)).setKey("autor");

		// Coluna para Criação, com formatação de data
		Grid.Column<T> criacaoColumn = grid.addColumn(model -> {
			if (model.getCriacao() != null) {
				return model.getCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
			}
			return null;
		}).setHeader("Criação").setSortable(true);
		criacaoColumn.setComparator(
				Comparator.comparing(model -> model.getCriacao(), Comparator.nullsLast(Comparator.naturalOrder())))
				.setKey("criacao");

		// Coluna para Unid
		Grid.Column<T> unidColumn = grid.addColumn(model -> model.getUnid()).setHeader("Unid").setSortable(true);
		unidColumn.setComparator(Comparator.comparing(T::getUnid)).setKey("unid");
	}

	private void openPage(T model) {
		getUI().ifPresent(ui -> ui.navigate(model.getClass().getSimpleName().toLowerCase() + "/" + model.getUnid()));
	}

	private void novo() {
		getUI().ifPresent(ui -> ui.navigate(model.getClass().getSimpleName().toLowerCase() + "/"));
	}

	public void updateGrid(Grid<T> grid, String searchText) {
		LazyDataView<T> dataView = grid.setItems(query -> {
			// Pegando o offset, limite e ordens de ordenação da query
			int offset = query.getOffset();
			int limit = query.getLimit();
			List<QuerySortOrder> sortOrders = query.getSortOrders();

			// Chama o serviço com os parâmetros apropriados, incluindo o searchText
			return this.service.findAllByCodigo(offset, limit, sortOrders, searchText, getModelClass()).stream();
		});

		dataView.setItemCountEstimate(8000); // Estimativa de total de itens
	}

//	@SuppressWarnings("unchecked")
//	private Stream<T> captureWildcard(Stream<? extends AbstractModelDoc> stream) {
//		// This casting operation captures the wildcard and returns a stream of
//		// AbstractModelDoc - por causa do <E> no AbstractRepository
//		return (Stream<T>) stream;
//	}

	protected T createModel(Class<T> modelType) {
		try {
			return modelType.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Nao foi possivel criar o modelo - " + modelType, e);
		}
	}

	@SuppressWarnings("unchecked")
	protected Class<T> getModelClass() {
		return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

}
