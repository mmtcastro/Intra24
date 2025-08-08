package br.com.tdec.intra.empresas.componentes;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.icon.VaadinIcon;
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

public class MultivalueGrid<T extends AbstractModelDocMultivalue> extends Composite<VerticalLayout> {

	private static final long serialVersionUID = 1L;
	private Supplier<T> defaultMultiValueSupplier;
	private final Grid<T> grid;
	private final Binder<T> binder;
	private final Editor<T> editor;
	private final VerticalLayout layout;

	// Fonte dos dados: pegamos SEMPRE daqui (referência viva do modelo)
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

	public MultivalueGrid(Class<T> beanType, List<T> data) {
		this.data = data;
		data = new ArrayList<>(); // inicializa a lista de dados
		this.defaultMultiValueSupplier = () -> { // define qual multivalue será usado por padrão
			try {
				return beanType.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Erro ao criar nova instância de " + beanType.getSimpleName(), e);
			}
		};
		this.grid = new Grid<>(beanType, false);
		this.binder = new Binder<>(beanType);
		this.editor = grid.getEditor();
		this.editor.setBinder(binder);
		this.editor.setBuffered(true);

		this.layout = getContent();
		this.layout.setPadding(false);
		this.layout.setMargin(false);
		this.layout.setWidthFull();

		grid.setItems(data); // usa a lista REAL do modelo (referência viva)

		grid.setAllRowsVisible(true);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		headerRow = grid.prependHeaderRow();

		layout.add(grid);

		this.editor.addSaveListener(event -> {
			syncToMultivalueFields(); // atualiza os multivalue fields ao salvar
		});
	}

	public MultivalueGrid<T> setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;

		if (addButton != null) {
			addButton.setVisible(!readOnly); // aqui o botão aparece ou desaparece corretamente

		}
		if (saveButton != null && cancelButton != null && deleteButton != null && editButton != null) {
			saveButton.setVisible(!readOnly);
			cancelButton.setVisible(!readOnly);
			deleteButton.setVisible(!readOnly);
			editButton.setVisible(!readOnly);
		}

		refresh();

		return this;
	}

	public interface ColumnConfigurator<T> {
		void configure(ColumnBuilder<T> builder);
	}

	public MultivalueGrid<T> withColumns(ColumnConfigurator<T> configurator) {
		configurator.configure(new ColumnBuilder<>(grid, binder, readOnly));
		return this;
	}

	public MultivalueGrid<T> bind(List<T> unidades, List<String> propertyNames, List<?>... multivalueFields) {
		this.data.clear();
		this.data.addAll(unidades);
		this.propertyNames = propertyNames;
		this.multivalueTargets = multivalueFields;
		this.grid.setItems(data);
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
		private final boolean readOnly;

		public ColumnBuilder(Grid<T> grid, Binder<T> binder, boolean readOnly) {
			this.grid = grid;
			this.binder = binder;
			this.readOnly = readOnly;
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

		grid.addComponentColumn((T item) -> {
			editButton = new Button(VaadinIcon.EDIT.create(), e -> {
				if (editor.isOpen()) {
					editor.cancel();
				}
				editor.editItem(item);

			});
			editButton.getElement().setAttribute("title", "Editar");
			editButton.setVisible(!readOnly);

			saveButton = new Button(VaadinIcon.CHECK.create(), e -> editor.save());
			saveButton.getElement().setAttribute("title", "Salvar");
			saveButton.setVisible(!readOnly);

			cancelButton = new Button(VaadinIcon.CLOSE.create(), e -> editor.cancel());
			cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
			cancelButton.getElement().setAttribute("title", "Cancelar");
			cancelButton.setVisible(!readOnly);

			deleteButton = new Button(VaadinIcon.TRASH.create(), e -> deleteItem(item));
			deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
			deleteButton.getElement().setAttribute("title", "Apagar");
			deleteButton.setVisible(!readOnly);

			HorizontalLayout layout = new HorizontalLayout(editButton, saveButton, cancelButton, deleteButton);
			layout.setSpacing(true);
			layout.setVisible(!readOnly);
			return layout;
		}).setHeader("Ações").setWidth("240px").setFlexGrow(0);

		return this;
	}

	public void addAdicionarButton(String label, Runnable onClick) {

		addButton = new Button(label);
		addButton.addClickListener(e -> onClick.run());

		// Usa a última coluna da grid para adicionar o botão na linha de header
		List<Grid.Column<T>> columns = grid.getColumns();
		if (!columns.isEmpty()) {
			Grid.Column<T> lastColumn = columns.get(columns.size() - 1);
			headerRow.getCell(lastColumn).setComponent(addButton);
		}
	}

	public void addItem(T item) {
		this.data.add(item);
		refresh();
		editor.editItem(item);
	}

	public boolean deleteItem(T item) {
		boolean removed = data.remove(item);
		refresh();
		return removed;
	}

	public MultivalueGrid<T> enableAddButton(String label, Supplier<T> itemSupplier) {

		this.defaultMultiValueSupplier = itemSupplier; // se quiser reaproveitar depois
		addAdicionarButton(label, () -> {
			T newItem = itemSupplier.get();
			addItem(newItem); // já adiciona na lista + chama editor.editItem(newItem)
		});
		return this;
	}

	public MultivalueGrid<T> enableAddButton(String label) {
		Supplier<T> defaultMultiValueSupplier = () -> {
			try {
				return grid.getBeanType().getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Não foi possível instanciar " + grid.getBeanType().getSimpleName(), e);
			}
		};
		return enableAddButton(label, defaultMultiValueSupplier);
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

}
