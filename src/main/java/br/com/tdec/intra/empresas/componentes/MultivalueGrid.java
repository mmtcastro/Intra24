package br.com.tdec.intra.empresas.componentes;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Binder.BindingBuilder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.function.ValueProvider;

import br.com.tdec.intra.abs.AbstractModelDocMultivalue;
import br.com.tdec.intra.abs.AbstractModelListaMultivalue;

public class MultivalueGrid<T extends AbstractModelDocMultivalue> extends Composite<VerticalLayout> {

	private static final long serialVersionUID = 1L;
	private Supplier<T> defaultMultiValueSupplier;
	private final Grid<T> grid;
	private final Binder<T> binder;
	private final Editor<T> editor;
	private final VerticalLayout layout;

	// Fonte dos dados: pegamos SEMPRE daqui (referência viva do modelo)
	private final AbstractModelListaMultivalue<T> source;
	private final List<T> data;
	private boolean readOnly = true; // padrão é somente leitura
	private HeaderRow headerRow;

	private List<String> propertyNames;
	private List<?>[] multivalueTargets;

	private Button addButton;
	private Button saveButton;
	private Button cancelButton;
	private Button deleteButton;
	private Button editButton;

	private Runnable pendingAddButtonAction;
	private Grid.Column<T> actionColumn;

	private Span labelComponent; // titulo do multivalue (ex: "Unidades")

	public MultivalueGrid(Class<T> beanType, AbstractModelListaMultivalue<T> source) {
		this.source = Objects.requireNonNull(source, "source não pode ser nulo");

		// garante uma lista viva (não nula)
		List<T> live = source.getLista();
		if (live == null) {
			live = new ArrayList<>();
			source.setLista(live);
		}
		this.data = live;

		// fábrica default do item
		this.defaultMultiValueSupplier = () -> {
			try {
				return beanType.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Erro ao criar nova instância de " + beanType.getSimpleName(), e);
			}
		};

		// grid/binder/editor
		this.grid = new Grid<>(beanType, false);

		this.binder = new Binder<>(beanType);
		this.editor = grid.getEditor();
		this.editor.setBinder(binder);
		this.editor.setBuffered(true);
		grid.setSelectionMode(Grid.SelectionMode.NONE);

		// layout
		this.layout = getContent();
		this.layout.setPadding(false);
		this.layout.setMargin(false);
		this.layout.setWidthFull();

		// cria o label (inicialmente invisível)
		this.labelComponent = new Span();
		this.labelComponent.getStyle().set("margin-top", "var(--lumo-space-m)");
		this.labelComponent.getStyle().set("margin-bottom", "0.1rem"); // mesmo gap dos campos
		this.labelComponent.getStyle().set("font-size", "var(--lumo-font-size-s)");
		this.labelComponent.getStyle().set("font-weight", "500");
		this.labelComponent.getStyle().set("color", "var(--lumo-secondary-text-color)");
		this.labelComponent.setVisible(false);

		// itens e aparência
		grid.setItems(data); // referência viva ao modelo
		grid.setAllRowsVisible(true);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		// header antes das colunas de ações (vamos poder colocar o botão Adicionar
		// aqui)
		headerRow = grid.prependHeaderRow();

		// listeners do editor – simples e sem duplicação
		this.editor.addOpenListener(e -> {

			System.out.println("Editor aberto para item: " + e.getItem());

		});

		this.editor.addSaveListener(e -> {
			System.out.println("Editor salvou item: " + e.getItem());
			syncToMultivalueFields();
		});

		this.editor.addCloseListener(e -> {
			// Refresh simples após fechar o editor
			grid.getDataProvider().refreshAll();
		});

		// evite entrar em edição por duplo clique — apenas pelo botão Editar
		grid.addItemDoubleClickListener(e -> editor.cancel());

		layout.add(labelComponent, grid);

	}

	// MÉTODO PARA RECRIAR O HEADER quando readOnly mudar
	public MultivalueGrid<T> setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;

		// Se a coluna de ações já existe, recria o header
		if (actionColumn != null) {
			createCustomActionHeader();
		}

		refresh();
		return this;
	}

	public interface ColumnConfigurator<T> {
		void configure(ColumnBuilder<T> builder);
	}

	public MultivalueGrid<T> withColumns(ColumnConfigurator<T> configurator) {
		configurator.configure(new ColumnBuilder<>(grid, binder, readOnly));

		long editaveis = grid.getColumns()//
				.stream()//
				.filter(c -> c.getEditorComponent() != null).count();

		System.out.println("[MVGrid] colunas com editor (após withColumns): " + editaveis);

		return this;
	}

	public MultivalueGrid<T> bind(List<T> unidades, List<String> propertyNames) {
		this.propertyNames = propertyNames;
		return this;
	}

	@SuppressWarnings("unchecked")
	public void syncToMultivalueFields() {
		if (propertyNames == null || multivalueTargets == null || propertyNames.size() != multivalueTargets.length)
			return;

		for (int i = 0; i < multivalueTargets.length; i++) {
			List<Object> list = (List<Object>) multivalueTargets[i];
			if (list == null) {
				list = new ArrayList<>();
				multivalueTargets[i] = list; // atualiza a referência para evitar null em add()
			} else {
				list.clear();
			}
		}

		for (T item : data) {
			for (int i = 0; i < propertyNames.size(); i++) {
				try {
					String methodName = "get" + capitalize(propertyNames.get(i));
					Object value = item.getClass().getMethod(methodName).invoke(item);
					((List<Object>) multivalueTargets[i]).add(value);
				} catch (Exception e) {
					throw new RuntimeException("Erro ao sincronizar multivalue fields", e);
				}
			}
		}
	}

	public void refresh() {
		grid.getDataProvider().refreshAll();
	}

	private String capitalize(String name) {
		if (name == null || name.isEmpty())
			return name;
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	public static class ColumnBuilder<T> {
		private final Grid<T> grid;
		private final Binder<T> binder;

		public ColumnBuilder(Grid<T> grid, Binder<T> binder, boolean readOnly) {
			this.grid = grid;
			this.binder = binder;
		}

		public void addTextFieldColumn(//
				String header, //
				ValueProvider<T, String> getter, //
				Setter<T, String> setter, //
				Converter<String, String> converter, // .withConverter(Converter.identity()) caso não queira
														// converter)usar
				Validator<String>... validators) {
			// Cria uma coluna de texto com o getter e o header fornecidos
			Grid.Column<T> column = grid.addColumn(getter)//
					.setHeader(header)//
					.setAutoWidth(true)//
					.setSortable(true);

			TextField editorComponent = new TextField();

			// Conversão de efeito visual ao sair do campo (efeito imediato)
			editorComponent.addValueChangeListener(event -> {
				String value = event.getValue();
				if (value != null) {
					editorComponent.setValue(value.toUpperCase());
				}
			});

			BindingBuilder<T, String> binding = binder.forField(editorComponent)//
					.withNullRepresentation("") //
					.withConverter(converter);

			for (Validator<String> validator : validators) {
				binding = binding.withValidator(validator);
			}

			binding.bind(getter, setter);
			column.setEditorComponent(editorComponent);

		}

		// VERSÃO SIMPLIFICADA (sem converter)
		public void addTextFieldColumn(String header, //
				ValueProvider<T, String> getter, //
				Setter<T, String> setter, Validator<String>... validators) {

			addTextFieldColumn(header, getter, setter, Converter.identity(), validators);
		}

		public void addDoubleFieldColumn(String header, //
				ValueProvider<T, Double> getter, //
				Setter<T, Double> setter, //
				Converter<String, Double> converter, //
				Validator<Double>... validators) {
			NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
			numberFormat.setMinimumFractionDigits(2);
			numberFormat.setMaximumFractionDigits(2);

			Grid.Column<T> column = grid.addColumn(item -> {
				Double value = getter.apply(item);
				return value != null ? numberFormat.format(value) : "";
			}).setHeader(header)//
					.setAutoWidth(true)//
					.setSortable(true);

			TextField editorComponent = new TextField();
			BindingBuilder<T, Double> binding = binder.forField(editorComponent)//
					.withNullRepresentation("").withConverter(new StringToDoubleConverter("Valor inválido"));
			for (Validator<Double> validator : validators) {
				binding = binding.withValidator(validator);
			}
			binding.bind(getter, setter);
			column.setEditorComponent(editorComponent);

		}

		// Double - VERSÃO SIMPLIFICADA (sem converter)
		public void addDoubleFieldColumn(String header, //
				ValueProvider<T, Double> getter, //
				Setter<T, Double> setter, Validator<Double>... validators) {

			addDoubleFieldColumn(header, getter, setter, new StringToDoubleConverter("Número inválido"), validators);
		}

		public void addDateFieldColumn(String header, //
				ValueProvider<T, LocalDate> getter, //
				Setter<T, LocalDate> setter, //
				Converter<LocalDate, LocalDate> converter, //
				Validator<LocalDate>... validators) {

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			Grid.Column<T> column = grid.addColumn(item -> {
				LocalDate value = getter.apply(item);
				return value != null ? formatter.format(value) : "";
			}).setHeader(header)//
					.setAutoWidth(true)//
					.setSortable(true);

			DatePicker editorComponent = new DatePicker();
			BindingBuilder<T, LocalDate> binding = binder.forField(editorComponent)//
					.withConverter(converter);

			for (Validator<LocalDate> validator : validators) {
				binding = binding.withValidator(validator);
			}
			binding.bind(getter, setter);
			column.setEditorComponent(editorComponent);

		}

		// Date - VERSÃO SIMPLIFICADA (sem converter)
		public void addDateFieldColumn(String header, //
				ValueProvider<T, LocalDate> getter, //
				Setter<T, LocalDate> setter, //
				Validator<LocalDate>... validators) {

			addDateFieldColumn(header, getter, setter, Converter.identity(), validators);
		}

		public <V> void addComboBoxColumn(String header, ValueProvider<T, V> getter, Setter<T, V> setter,
				Collection<V> options, Validator<V>... validators) {
			Grid.Column<T> column = grid.addColumn(item -> {
				V value = getter.apply(item);
				return value != null ? value.toString() : "";
			}).setHeader(header).setAutoWidth(true).setSortable(true);

			ComboBox<V> editorComponent = new ComboBox<>();
			editorComponent.setItems(options);
			editorComponent.setAllowCustomValue(false);

			BindingBuilder<T, V> binding = binder.forField(editorComponent);
			for (Validator<V> validator : validators) {
				binding = binding.withValidator(validator);
			}
			binding.bind(getter, setter);
			column.setEditorComponent(editorComponent);

		}
	}

	public MultivalueGrid<T> addEditColumn() {
		grid.addComponentColumn(item -> {
			Button btn = new Button(VaadinIcon.EDIT.create());
			btn.addClickListener(e -> {
				if (editor.isOpen()) {
					editor.cancel();
				}
				editor.editItem(item);
			});
			return btn;
		}).setHeader("Editar").setWidth("100px").setFlexGrow(0);
		return this;
	}

	public MultivalueGrid<T> addDeleteColumn(Consumer<T> onDelete) {
		grid.addComponentColumn(item -> {
			cancelButton = new Button(VaadinIcon.TRASH.create());
			cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
			cancelButton.addClickListener(e -> onDelete.accept(item));
			return cancelButton;
		}).setHeader("Remover").setWidth("100px").setFlexGrow(0);
		return this;
	}

	public MultivalueGrid<T> addSaveCancelButtonsColumn() {
		grid.addComponentColumn((T item) -> {
			saveButton = new Button("Salvar", e -> editor.save());
			cancelButton = new Button(VaadinIcon.CLOSE.create(), e -> editor.cancel());
			cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);

			HorizontalLayout actions = new HorizontalLayout(saveButton, cancelButton);
			return actions;
		}).setHeader("Ações").setWidth("160px").setFlexGrow(0);
		return this;
	}

	public MultivalueGrid<T> addActionColumn() {
		actionColumn = grid.addComponentColumn((T item) -> {
			boolean docReadOnly = this.readOnly;
			boolean rowIsBeingEdited = editor.isOpen() && Objects.equals(editor.getItem(), item);

			// Layout que conterá os botões da linha
			HorizontalLayout actions = new HorizontalLayout();
			actions.setSpacing(true);
			actions.setPadding(false);
			actions.setAlignItems(Alignment.CENTER);

			if (docReadOnly) {
				// Documento em leitura -> nenhuma ação por linha
				actions.setVisible(false);
				return actions;
			}

			if (!rowIsBeingEdited) {
				// MODO NORMAL: apenas botão Editar
				Button editBtn = new Button(VaadinIcon.EDIT.create());
				editBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);
				editBtn.getElement().setAttribute("title", "Editar");

				// LISTENER SIMPLIFICADO - sem beforeClientResponse
				editBtn.addClickListener(e -> {
					System.out.println("Clicou em editar item: " + item); // debug

					if (editor.isOpen()) {
						editor.cancel(); // fecha a atual
					}

					// Edita o item diretamente
					editor.editItem(item);
				});

				actions.add(editBtn);
				return actions;
			}

			// MODO EDIÇÃO: Salvar / Cancelar / Apagar
			Button saveBtn = new Button(VaadinIcon.CHECK.create());
			saveBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);
			saveBtn.getElement().setAttribute("title", "Salvar");
			saveBtn.addClickListener(e -> {
				System.out.println("Clicou em salvar item: " + item); // debug
				editor.save();
			});

			Button cancelBtn = new Button(VaadinIcon.CLOSE.create());
			cancelBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);
			cancelBtn.getElement().setAttribute("title", "Cancelar");
			cancelBtn.addClickListener(e -> {
				System.out.println("Clicou em cancelar item: " + item); // debug
				editor.cancel();
			});

			Button deleteBtn = new Button(VaadinIcon.TRASH.create());
			deleteBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
			deleteBtn.getElement().setAttribute("title", "Apagar");
			deleteBtn.addClickListener(e -> {
				System.out.println("Clicou em deletar item: " + item); // debug
				deleteItem(item);
			});

			actions.add(saveBtn, cancelBtn, deleteBtn);
			return actions;

		});// .setHeader("Ações").setWidth("140px").setFlexGrow(0);

		// CRIA O HEADER CUSTOMIZADO para a coluna Ações
		createCustomActionHeader();

		// Define propriedades da coluna
		actionColumn.setWidth("140px").setFlexGrow(0);

		return this;
	}

	private void createCustomActionHeader() {
		if (actionColumn == null || headerRow == null) {
			System.out.println(
					"[DEBUG] Não pode criar header: actionColumn=" + actionColumn + ", headerRow=" + headerRow);
			return;
		}

		// Botão + separado
		addButton = new Button(VaadinIcon.PLUS.create());
		addButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);
		addButton.getElement().setAttribute("title", "Adicionar novo item");

		// Se tem ação pendente, adiciona
		if (pendingAddButtonAction != null) {
			addButton.addClickListener(e -> pendingAddButtonAction.run());
		}

		// Layout: [+] Ações
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setSpacing(true);
		headerLayout.setPadding(false);
		headerLayout.setAlignItems(Alignment.CENTER);

		Span acaoText = new Span("Ações");
		acaoText.getStyle().set("font-weight", "bold");

		headerLayout.add(addButton, acaoText);

		// Visibilidade baseada no readOnly
		headerLayout.setVisible(!readOnly);

		// Define no header da coluna
		headerRow.getCell(actionColumn).setComponent(headerLayout);

		System.out.println("[DEBUG] Header customizado criado. Visível: " + !readOnly);
	}

	public void addAdicionarButton(String label, Runnable onClick) {
		// Se o botão ainda não foi criado, não faz nada
		// (ele será criado quando addActionColumn() for chamado)
		if (addButton == null) {
			// Armazena o onClick para usar depois
			this.pendingAddButtonAction = onClick;
			return;
		}

		// Se já existe, apenas adiciona o listener
		addButton.addClickListener(e -> onClick.run());
	}

	public void addItem(T item) {
		this.data.add(item);
		refresh();
		editor.editItem(item);
	}

	public boolean deleteItem(T item) {
		// Se essa linha está em edição, cancela primeiro (agora seguro porque não temos
		// listener de cancel que refresca)
		if (editor.isOpen() && Objects.equals(editor.getItem(), item)) {
			editor.cancel();
		}
		boolean removed = data.remove(item);

		// SUBSTITUA refreshAllLater() por refresh() simples:
		refresh(); // ao invés de refreshAllLater()

		return removed;
	}

	public MultivalueGrid<T> enableAddButton(String label, Supplier<T> itemSupplier) {
		this.defaultMultiValueSupplier = itemSupplier;

		Runnable addAction = () -> {
			T newItem = itemSupplier.get();
			addItem(newItem);
		};

		// Se o botão já foi criado, adiciona o listener diretamente
		if (addButton != null) {
			addButton.addClickListener(e -> addAction.run());
		} else {
			// Senão, armazena para usar depois
			this.pendingAddButtonAction = addAction;
		}

		return this;
	}

	// NOVO MÉTODO: controla visibilidade do header da coluna Ações
	private void updateActionColumnHeaderVisibility() {
		if (headerRow == null)
			return;

		// Procura pela coluna de ações
		List<Grid.Column<T>> columns = grid.getColumns();
		for (Grid.Column<T> column : columns) {
			HeaderRow.HeaderCell cell = headerRow.getCell(column);

			// Verifica se a célula tem um componente
			Component component = cell.getComponent();
			if (component != null) {
				// Se é o nosso HorizontalLayout do header de ações
				if (component instanceof HorizontalLayout) {
					HorizontalLayout headerLayout = (HorizontalLayout) component;

					// Verifica se tem o botão "+" dentro (para confirmar que é o header correto)
					boolean isActionHeader = headerLayout.getChildren()
							.anyMatch(child -> child instanceof Button && ((Button) child).getIcon() != null);

					if (isActionHeader) {
						headerLayout.setVisible(!readOnly);
						break; // encontrou, pode parar
					}
				}
			}
		}
	}

	public void syncAndSaveIfEditing() {
		syncToMultivalueFields();
		if (getEditor().isOpen()) {
			getEditor().save();
		}
	}

	public void setLabel(String label) {
		if (label != null && !label.isBlank()) {
			this.labelComponent.setText(label);
			this.labelComponent.setVisible(true);
		} else {
			this.labelComponent.setText("");
			this.labelComponent.setVisible(false);
		}
	}

	public MultivalueGrid<T> setItems(List<T> items) {
		grid.setItems(items);
		return this;
	}

	public Supplier<T> getDefaultMultiValueSupplier() {
		return defaultMultiValueSupplier;
	}

	public void setDefaultMultiValueSupplier(Supplier<T> defaultMultiValueSupplier) {
		this.defaultMultiValueSupplier = defaultMultiValueSupplier;
	}

	public HeaderRow getHeaderRow() {
		return headerRow;
	}

	public void setHeaderRow(HeaderRow headerRow) {
		this.headerRow = headerRow;
	}

	public List<String> getPropertyNames() {
		return propertyNames;
	}

	public void setPropertyNames(List<String> propertyNames) {
		this.propertyNames = propertyNames;
	}

	public List<?>[] getMultivalueTargets() {
		return multivalueTargets;
	}

	public void setMultivalueTargets(List<?>[] multivalueTargets) {
		this.multivalueTargets = multivalueTargets;
	}

	public Grid<T> getGrid() {
		return grid;
	}

	public Binder<T> getBinder() {
		return binder;
	}

	public Editor<T> getEditor() {
		return editor;
	}

	public VerticalLayout getLayout() {
		return layout;
	}

	public List<T> getData() {
		return data;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public Button getAddButton() {
		return addButton;
	}

	public void setAddButton(Button addButton) {
		this.addButton = addButton;
	}

	public Button getSaveButton() {
		return saveButton;
	}

	public void setSaveButton(Button saveButton) {
		this.saveButton = saveButton;
	}

	public Button getCancelButton() {
		return cancelButton;
	}

	public void setCancelButton(Button cancelButton) {
		this.cancelButton = cancelButton;
	}

	public Button getDeleteButton() {
		return deleteButton;
	}

	public void setDeleteButton(Button deleteButton) {
		this.deleteButton = deleteButton;
	}

	public Button getEditButton() {
		return editButton;
	}

	public void setEditButton(Button editButton) {
		this.editButton = editButton;
	}

	public AbstractModelListaMultivalue<T> getSource() {
		return source;
	}

	public Runnable getPendingAddButtonAction() {
		return pendingAddButtonAction;
	}

	public void setPendingAddButtonAction(Runnable pendingAddButtonAction) {
		this.pendingAddButtonAction = pendingAddButtonAction;
	}

	public Grid.Column<T> getActionColumn() {
		return actionColumn;
	}

	public void setActionColumn(Grid.Column<T> actionColumn) {
		this.actionColumn = actionColumn;
	}

	public Span getLabelComponent() {
		return labelComponent;
	}

	public void setLabelComponent(Span labelComponent) {
		this.labelComponent = labelComponent;
	}

}
