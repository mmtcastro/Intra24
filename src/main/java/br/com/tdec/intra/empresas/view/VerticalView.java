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
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
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
	private VerticalLayout bodyFieldLayout;
	private VerticalLayout verticalLayoutGrid = new VerticalLayout();
	private Grid<Unidade> gridUnidades;
	private Button buttonAdicionarUnidade;

	public VerticalView(VerticalService service) {
		super(Vertical.class, service);
		addClassNames("abstract-view-doc");

	}

//	@Override
//	public void updateReadOnlyState() {
//		// Chama o método da classe abstrata para aplicar o estado de readOnly a todos
//		// os componentes
//		super.updateReadOnlyState();
//
//		// Atualize o grid ao mudar o estado de readOnly
//		atualizarGrid();
//	}

	public void initBinder() {

		if (isNovo) {
			// model.setData(ZonedDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT,
			// ZoneId.systemDefault()));
			model.setData(LocalDate.now());
			binder.forField(codigoField).asRequired("Entre com um código")//
					.withNullRepresentation("")//
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

		binder.forField(bodyField).withNullRepresentation("") // Representação nula para o campo de entrada
				.withConverter(new RichTextToMimeConverter()) // Aplicando o converter
				.bind(Vertical::getBody, Vertical::setBody);

		// Remove o layout existente antes de recriá-lo
		binderFields.remove(bodyFieldLayout);
		// Configuração do RichTextEditor
		bodyField.setWidthFull(); // Alinha o RichTextEditor com os outros campos
		bodyField.getStyle().set("min-height", "300px"); // Ajusta a altura mínima para melhor visualização

		// Layout para o campo de texto rico
		bodyFieldLayout = new VerticalLayout();
		bodyFieldLayout.setWidthFull();
		bodyFieldLayout.setPadding(false);

		// Rótulo para o campo de observações
		Span bodyFieldLabel = new Span("Body:");
		bodyFieldLabel.getStyle().set("font-weight", "bold");
		bodyFieldLabel.getStyle().set("margin-top", "10px"); // Aumenta o espaçamento em relação ao campo acima
		bodyFieldLabel.getStyle().set("margin-bottom", "0px"); // Reduz a distância entre o rótulo e o campo

		// Adicionar o rótulo e o campo ao layout
		bodyFieldLayout.add(bodyFieldLabel, bodyField);
		setColspan(bodyFieldLayout, 2);

		binder.setBean(model);

		// Adicionar o campo ao binderFields para controle de readOnly
		binderFields.add(codigoField);
		binderFields.add(dataField);
		binderFields.add(descricaoField);
		binderFields.add(bodyFieldLayout); // Adiciona o campo diretamente para controle de readOnly

		// Grid Unidades
		initGrid();
		binderFields.add(verticalLayoutGrid);

		// Atualize o grid aqui para refletir o estado correto
		atualizarGrid();

		print("Obs no model eh " + model.getObs().toString());

	}

	private void initGrid() {
		if (verticalLayoutGrid != null) {
			verticalLayoutGrid.removeAll();
		} else {
			verticalLayoutGrid = new VerticalLayout();
		}

		gridUnidades = new Grid<>(Unidade.class, false);
		gridUnidades.setAllRowsVisible(true);
		gridUnidades.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		Editor<Unidade> editor = gridUnidades.getEditor();
		editor.setBuffered(true);

		// Criação de um Binder específico para o editor do Grid
		Binder<Unidade> gridBinder = new Binder<>(Unidade.class);
		editor.setBinder(gridBinder);

		// Coluna para o campo "Estado"
		Grid.Column<Unidade> estadoColumn = gridUnidades.addColumn(Unidade::getEstado).setHeader("Estado")
				.setSortable(true).setAutoWidth(true);

		TextField estadoField = new TextField();
		gridBinder.forField(estadoField).asRequired("Estado não pode ser vazio")//
				.withConverter(String::toUpperCase, String::toUpperCase)
				.withValidator(estado -> !isEstadoDuplicado(estado), "Estado já existe na lista")//
				.bind(Unidade::getEstado, Unidade::setEstado);
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
			NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
			return currencyFormat.format(unidade.getValor());
		}).setHeader("Valor")//
				.setSortable(true)//
				.setAutoWidth(true)//
				.setResizable(true)//
				.setWidth("150px")//
				.setFlexGrow(1);

		// Campo de edição para "Valor"
		TextField valorField = new TextField();
		valorField.setWidthFull();

		// Configuração de validação e conversão para o campo "Valor"
		gridBinder.forField(valorField).asRequired("Valor não pode ser vazio")
				.withConverter(new StringToDoubleConverter("Valor inválido"))
				.bind(Unidade::getValor, Unidade::setValor);

		valorColumn.setEditorComponent(valorField);

		// Botões de ação para edição
		Button saveButton = new Button("Salvar", e -> editor.save());
		Button cancelButton = new Button(VaadinIcon.CLOSE.create(), e -> editor.cancel());
		cancelButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
		HorizontalLayout actions = new HorizontalLayout(saveButton, cancelButton);
		actions.setPadding(false);
		// Coluna de edição
		if (!isReadOnly) {
			Grid.Column<Unidade> editColumn = gridUnidades.addComponentColumn(unidade -> {
				Button editButton = new Button(VaadinIcon.EDIT.create()); // criar uma nova instancia para cada item do
																			// grid
				editButton.addClickListener(e -> {
					if (editor.isOpen()) {
						editor.cancel();
					}
					gridUnidades.getEditor().editItem(unidade);
					estadoField.focus();
				});
				return editButton;
			}).setWidth("150px")//
					.setFlexGrow(0)//
					.setHeader(new Button("Adicionar", e -> adicionarUnidade()));

			// Define o componente de edição
			editColumn.setEditorComponent(actions);
		}

		// Coluna de apagar
		if (!isReadOnly) {
			Grid.Column<Unidade> deleteColumn = gridUnidades.addComponentColumn(unidade -> {
				Button deleteButton = new Button(VaadinIcon.TRASH.create());
				deleteButton.addClickListener(e -> apagarUnidade(unidade));
				deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
				return deleteButton;
			}).setWidth("100px").setFlexGrow(0);

			// Carregar os dados no Grid
			atualizarGrid();
		}

//		// Carregar os dados no Grid
//		if (model.getUnidades() != null && !model.getUnidades().isEmpty()) {
//			gridUnidades.setItems(model.getUnidades());
//		} else {
//			Notification.show("Nenhuma unidade encontrada.");
//		}

		HeaderRow extraHeader = gridUnidades.prependHeaderRow();
		extraHeader.getCell(estadoColumn).setText("Unidades");

		verticalLayoutGrid.add(gridUnidades);

		// add(verticalLayoutGrid);
	}

	private void adicionarUnidade() {
		Unidade novaUnidade = new Unidade();
		novaUnidade.setEstado("");
		novaUnidade.setCriacao(LocalDate.now());
		novaUnidade.setValor(0.0);

		if (model.getUnidades() == null) {
			model.setUnidades(new ArrayList<>());
		}

		// Adiciona a nova unidade à lista e atualiza o Grid
		model.getUnidades().add(novaUnidade);
		atualizarGrid();

		// Coloca o novo item em modo de edição
		gridUnidades.getEditor().editItem(novaUnidade);
	}

	private void apagarUnidade(Unidade unidade) {
		if (model.getUnidades() != null) {
			model.getUnidades().remove(unidade);
			atualizarGrid();
		}
	}

	private void atualizarGrid() {
		if (model.getUnidades() != null && !model.getUnidades().isEmpty()) {
			gridUnidades.setItems(model.getUnidades());
		} else {
			// Notification.show("Nenhuma unidade encontrada.");
		}
	}

	private boolean isEstadoDuplicado(String estado) {
		// Se a lista de unidades for nula ou vazia, não há duplicação
		if (model.getUnidades() == null || model.getUnidades().isEmpty()) {
			return false;
		}

		// Contar quantas vezes o estado aparece na lista
		long count = model.getUnidades().stream().filter(unidade -> estado.equalsIgnoreCase(unidade.getEstado()))
				.count();

		// Se o estado aparecer mais de uma vez, é duplicado
		return count > 0;
	}

}
