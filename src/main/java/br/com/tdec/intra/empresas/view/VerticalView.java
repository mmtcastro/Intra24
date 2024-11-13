package br.com.tdec.intra.empresas.view;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractValidator;
import br.com.tdec.intra.abs.AbstractViewDoc;
import br.com.tdec.intra.empresas.model.Vertical;
import br.com.tdec.intra.empresas.model.Vertical.Unidade;
import br.com.tdec.intra.empresas.services.VerticalService;
import br.com.tdec.intra.utils.converters.ProperCaseConverter;
import br.com.tdec.intra.utils.converters.RemoveSimbolosEAcentos;
import br.com.tdec.intra.utils.converters.RichTextToMimeConverter;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PageTitle("Vertical")
@Route(value = "vertical", layout = MainLayout.class)
@RolesAllowed("ROLE_EVERYONE")
public class VerticalView extends AbstractViewDoc<Vertical> {

	private static final long serialVersionUID = 1L;
	private DatePicker dataField = new DatePicker("Data");
	private TextField codigoField = new TextField("Código");
	private TextField descricaoField = new TextField("Descrição");
	private RichTextEditor bodyField = new RichTextEditor();
	private VerticalLayout verticalLayoutGrid = new VerticalLayout();
	private Grid<Unidade> gridUnidades;
	private Button buttonAdicionarUnidade;
	HorizontalLayout actionButtons;
	Button actionEditButton;
	Button actionDeleteButton;
	Button actionSaveButton;

	public VerticalView(VerticalService service) {
		super(Vertical.class, service);
		addClassNames("abstract-view-doc");

	}

	public void initBinder() {

		if (isNovo) {
			// model.setData(ZonedDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT,
			// ZoneId.systemDefault()));
			model.setData(LocalDate.now());
			binder.forField(codigoField).asRequired("Entre com um código").withNullRepresentation("")
					.withConverter(new ProperCaseConverter())//
					.withConverter(new RemoveSimbolosEAcentos())
					.withValidator(new AbstractValidator.CodigoValidator<>(service))
					.bind(Vertical::getCodigo, Vertical::setCodigo);
		} else {
			binder.forField(codigoField).asRequired("Entre com um código").withNullRepresentation("")
					.bind(Vertical::getCodigo, Vertical::setCodigo);
			readOnlyFields.add(codigoField);
		}
		binder.forField(dataField)//
				.asRequired("Formato esperado: DD/MM/AAAA")//
				// .withConverter(new ZonedDateTimeToIso8601Converter())//
				.bind(Vertical::getData, Vertical::setData);

		binder.forField(descricaoField).asRequired("Entre com uma descrição").bind(Vertical::getDescricao,
				Vertical::setDescricao);

		VerticalLayout bodyFieldLayout = new VerticalLayout();
		bodyFieldLayout.setWidthFull();
		bodyFieldLayout.setPadding(false);

		Span bodyFieldLabel = new Span("Observações:");
		bodyFieldLabel.getStyle().set("font-weight", "bold");
		bodyFieldLabel.getStyle().set("margin-top", "10px"); // Aumenta o espaçamento em relação ao campo acima
		bodyFieldLabel.getStyle().set("margin-bottom", "0px"); // Reduz a distância entre o rótulo e o campo

		binder.forField(bodyField).withNullRepresentation("") // Representação nula para o campo de entrada
				.withConverter(new RichTextToMimeConverter()) // Aplicando o converter
				.bind(Vertical::getBody, Vertical::setBody);

		// Configuração do RichTextEditor
		bodyField.setWidthFull(); // Alinha o RichTextEditor com os outros campos
		bodyField.getStyle().set("min-height", "300px"); // Ajusta a altura mínima para melhor visualização

		bodyFieldLayout.add(bodyFieldLabel, bodyField);
		setColspan(bodyFieldLayout, 2);

		binder.setBean(model);

		// Adicione os campos na ordem correta (caso contrario o updateView troca a
		// ordem)
		binderFields.add(codigoField);
		binderFields.add(dataField);
		binderFields.add(descricaoField);
		binderFields.add(bodyFieldLayout);

	}

	private void initGrid() {
		if (verticalLayoutGrid != null) {
			verticalLayoutGrid.removeAll();
		} else {
			verticalLayoutGrid = new VerticalLayout();
		}

		gridUnidades = new Grid<>(Unidade.class, false);
		Editor<Unidade> editor = gridUnidades.getEditor();
		editor.setBuffered(true);

		// Criação de um Binder específico para o editor do Grid
		Binder<Unidade> gridBinder = new Binder<>(Unidade.class);
		editor.setBinder(gridBinder);

		// Coluna para o campo "Estado"
		Grid.Column<Unidade> estadoColumn = gridUnidades.addColumn(Unidade::getEstado).setHeader("Estado")
				.setSortable(true).setAutoWidth(true);

		TextField estadoField = new TextField();
		gridBinder.forField(estadoField).asRequired("Estado não pode ser vazio").bind(Unidade::getEstado,
				Unidade::setEstado);
		estadoColumn.setEditorComponent(estadoField);

		// Coluna para o campo "Criação"
		Grid.Column<Unidade> criacaoColumn = gridUnidades.addColumn(unidade -> {
			LocalDate criacao = unidade.getCriacao();
			return criacao != null ? criacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
		}).setHeader("Criação").setSortable(true).setAutoWidth(true);

		DatePicker criacaoField = new DatePicker();
		gridBinder.forField(criacaoField).asRequired("Data de criação é obrigatória").bind(Unidade::getCriacao,
				Unidade::setCriacao);
		criacaoColumn.setEditorComponent(criacaoField);

		// Coluna para o campo "Valor"
		Grid.Column<Unidade> valorColumn = gridUnidades.addColumn(unidade -> {
			NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
			return currencyFormat.format(unidade.getValor());
		}).setHeader("Valor").setSortable(true).setAutoWidth(true);

		// Campo de edição para "Valor"
		TextField valorField = new TextField();
		valorField.setWidthFull();

		// Configurando o editor da coluna
		valorColumn.setEditorComponent(valorField);

		// Botões de ação para edição
		Button saveButton = new Button("Salvar", e -> editor.save());
		Button cancelButton = new Button(VaadinIcon.CLOSE.create(), e -> editor.cancel());
		cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);

		HorizontalLayout actions = new HorizontalLayout(saveButton, cancelButton);
		actions.setPadding(false);

		// Coluna de edição
		Grid.Column<Unidade> editColumn = gridUnidades.addComponentColumn(unidade -> {
			Button editButton = new Button(VaadinIcon.EDIT.create());
			editButton.addClickListener(e -> {
				if (editor.isOpen()) {
					editor.cancel();
				}
				gridUnidades.getEditor().editItem(unidade);
				estadoField.focus();
			});
			return editButton;
		}).setWidth("150px").setFlexGrow(0);

		// Define o componente de edição
		editColumn.setEditorComponent(actions);

		// Carregar os dados no Grid
		if (model.getUnidades() != null && !model.getUnidades().isEmpty()) {
			gridUnidades.setItems(model.getUnidades());
		} else {
			Notification.show("Nenhuma unidade encontrada.");
		}

		verticalLayoutGrid.add(gridUnidades);
		add(verticalLayoutGrid);
	}

	private void createActionButtons() {
		// Botões de ação
		actionEditButton = new Button(VaadinIcon.EDIT.create());
		actionDeleteButton = new Button(VaadinIcon.TRASH.create());
		actionSaveButton = new Button(VaadinIcon.CHECK.create());

		// Configuração dos botões
		actionEditButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
		actionDeleteButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
		actionSaveButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_PRIMARY);
		actionSaveButton.setVisible(false);
	}

	private void adicionarUnidade() {
		Unidade novaUnidade = new Unidade();
		// novaUnidade.setCriacao(LocalDate.now());
		// novaUnidade.setEstado("Novo Estado");
		// novaUnidade.setValor(0.0);

		if (model.getUnidades() == null) {
			model.setUnidades(new ArrayList<>());
		}

		model.getUnidades().add(novaUnidade);
		gridUnidades.getDataProvider().refreshAll();
		Notification.show("Nova Unidade adicionada.");
	}

	@Override
	protected void addCustomComponents() {
		initGrid();

	}
}
