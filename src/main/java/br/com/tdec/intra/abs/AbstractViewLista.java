package br.com.tdec.intra.abs;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.stream.Stream;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;

import br.com.tdec.intra.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbstractViewLista extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	// protected AbstractRepository repository;
	protected Grid<AbstractModelDoc> defaultGrid;
	protected AbstractViewDoc form;
	protected AbstractModelDoc model;
	// protected FormLayout form = new FormLayout();
	protected DefaultForm defaultForm;

	public void initDefaultGrid() {
		setSizeFull();
		defaultGrid = new Grid<>();
		defaultGrid.setSizeFull();
		defaultGrid.addClassName("abstract-view-lista-grid");

		Button criarDocumento = new Button("Criar Documento", e -> addModel());

		Column<AbstractModelDoc> codigoColumn = defaultGrid.addColumn(AbstractModelDoc::getCodigo).setHeader("Código")
				.setSortable(true);
		codigoColumn.setComparator(Comparator.comparing(AbstractModelDoc::getCodigo)).setKey("codigo");
		Column<AbstractModelDoc> descricaoColumn = defaultGrid.addColumn(AbstractModelDoc::getDescricao)
				.setHeader("Descrição");
		Grid.Column<AbstractModelDoc> autorColumn = defaultGrid.addColumn(AbstractModelDoc::getAutor)
				.setHeader("Autor");
		autorColumn.setComparator(Comparator.comparing(AbstractModelDoc::getAutor)).setKey("autor");
//		Grid.Column<AbstractModelDoc> criacaoLocalDateTime = gridDefault
//				.addColumn(new LocalDateTimeRenderer<>(AbstractModelDoc::getCriacao, "dd/MM/yyyy HH:mm:ss"))
//				.setHeader("Criação");
		Grid.Column<AbstractModelDoc> criacaoColumn = defaultGrid.addColumn(new TextRenderer<>(item -> {
			if (item.getCriacao() != null) {
				return item.getCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
			} else {
				return null; // Or any placeholder text you prefer
			}
		})).setHeader("Criação");
//		criacaoColumn.setComparator(Comparator.comparing(AbstractModelDoc::getCriacao)).setKey("criacao");
//
//		Grid.Column<Vertical> valorColumn = gridVertical.addColumn(AbstractModelDoc::getValor).setHeader("Valor");
//		valorColumn.setComparator(Comparator.comparing(Vertical::getId)).setKey("valor");

		TextField searchText = new TextField();
		searchText.setPlaceholder("buscar...");
		searchText.setClearButtonVisible(true);
		searchText.setValueChangeMode(ValueChangeMode.LAZY);
		searchText.addValueChangeListener(e -> updateListDefault(defaultGrid, searchText.getValue()));

		updateListDefault(defaultGrid, searchText.getValue());
		HorizontalLayout toolbar = new HorizontalLayout(searchText, criarDocumento);

		defaultForm = new DefaultForm();
		defaultForm.setWidth("25cm");
		add(toolbar, getDefaultContent());
		closeEditor();

		defaultGrid.asSingleSelect().addValueChangeListener(evt -> editModel(evt.getValue()));

	}

	private void closeEditor() {
		defaultForm.setModel(null);
		defaultForm.setVisible(false);
		removeClassName("abstract-view-lista-editing");
	}

	private Component getDefaultContent() {
		HorizontalLayout content = new HorizontalLayout(defaultGrid, defaultForm);
		content.setFlexGrow(2, defaultGrid);
		content.setFlexGrow(1, defaultForm);
		content.addClassNames("abstract-view-lista-content");
		content.setSizeFull();
		return content;
	}

	public LocalDate twoDaysBeforeToday() {
		return LocalDate.now().minusDays(2);
	}

	public void editModel(AbstractModelDoc model) {
		this.model = model;
		if (model == null) {
			closeFormDefault();
		} else {
			defaultForm.setModel(model);
			defaultForm.setVisible(true);
			addClassName("abstract-view-lista-editing");
		}

	}

	/**
	 * Designa um DataView com LazyLoading para o grid
	 */
	public void updateListDefault(Grid<AbstractModelDoc> grid, String searchText) {
		System.out.println("Search eh " + searchText);
//		LazyDataView<Cargo> dataView = grid.setItems(q -> captureWildcard(this.service
//				.findAllByCodigo(q.getOffset(), q.getLimit(), q.getSortOrders(), q.getFilter(), searchText).stream()));
//
//		dataView.setItemCountEstimate(8000);
	}

	@SuppressWarnings({ "unchecked" })
	private Stream<AbstractModelDoc> captureWildcard(Stream<? extends AbstractModelDoc> stream) {
		// This casting operation captures the wildcard and returns a stream of
		// AbstractModelDoc - por causa do <E> no AbstractRepository
		return (Stream<AbstractModelDoc>) stream;
	}

	public void closeFormDefault() {
		removeClassName("editing");
		// formLayoutDefault.setVisible(false);
	}

	/**
	 * Adiciona um novo modelo ao grid
	 * 
	 */
	private void addModel() {
		defaultGrid.asSingleSelect().clear();
		Class<?> classModel = Utils.getModelClassFromViewListaClass(this.getClass());
		try {
			model = (AbstractModelDoc) classModel.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		editModel(model);
	}

	public class DefaultForm extends FormLayout {

		private static final long serialVersionUID = 1L;
		TextField codigo = new TextField("Código");
		TextField descricao = new TextField("Descrição");
		ComboBox<String> status = new ComboBox<>("Status");
		TextField autor = new TextField("Autor");
		TextField criacao = new TextField("Criação");

		Button save = new Button("Salvar");
		Button delete = new Button("Apagar");
		Button close = new Button("Cancelar");

		BeanValidationBinder<AbstractModelDoc> binder;

		public DefaultForm() {
//			addClassName("default-form");
//			binder = new BeanValidationBinder<>(AbstractModelDoc.class);
//			binder.forField(criacao).withConverter(new UtilsConverter.ZonedDateTimeToStringConverter())
//					.bind(AbstractModelDoc::getCriacao, AbstractModelDoc::setCriacao);
//			autor.setReadOnly(true);
//			criacao.setReadOnly(true);
//			status.setItems("Ativo", "Inativo");
//	        status.setPlaceholder("Selecione o status");
//			
//			binder.bindInstanceFields(this);
//			save.addClickListener(e -> save());
//
//			add(codigo, descricao, status, autor, criacao, createButtonsLayout());

		}

//		private boolean save() {
//			return repository.saveModel(model, null);
//		}

		public void setModel(AbstractModelDoc model) {
			binder.setBean(model);

		}

		private HorizontalLayout createButtonsLayout() {
			save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
			delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
			close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

			save.addClickShortcut(Key.ENTER);
			close.addClickShortcut(Key.ESCAPE);

			close.addClickListener(e -> closeEditor());

			return new HorizontalLayout(save, delete, close);

		}

	}

}
