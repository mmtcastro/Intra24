package br.com.tdec.intra.empresas.view;

import java.io.Serial;
import java.time.LocalDate;
import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractValidator;
import br.com.tdec.intra.abs.AbstractViewDoc;
import br.com.tdec.intra.empresas.componentes.MultivalueGrid;
import br.com.tdec.intra.empresas.model.Vertical;
import br.com.tdec.intra.utils.converters.ChainedConverter;
import br.com.tdec.intra.utils.converters.ProperCaseConverter;
import br.com.tdec.intra.utils.converters.RemoveSimbolosEAcentos;
import br.com.tdec.intra.utils.converters.UpperCaseConverter;
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

	@Serial
	private static final long serialVersionUID = 1L;
	private DatePicker dataField = new DatePicker("Data");
	private TextField codigoField = new TextField("Código");
	private TextField descricaoField = new TextField("Descrição");
	private VerticalLayout bodyFieldLayout;
	// private VerticalLayout verticalLayoutGrid = new VerticalLayout();
	private Button buttonAdicionarUnidade;

	private MultivalueGrid<Vertical.Unidade> unidadeGrid;

	public VerticalView() {
		super();
		addClassNames("abstract-view-doc");

	}

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

		binder.setBean(model);

		// Adicionar o campo ao binderFields para controle de readOnly
		form.addFormRow(codigoField);
		form.addFormRow(dataField);
		form.addFormRow(descricaoField);
		// binderFields.add(bodyFieldLayout); // Adiciona o campo diretamente para
		// controle de readOnly
		// form.addFormRow(bodyFieldLayout);

		// Inicialização do grid multivalue
		var unidades = model.getUnidades();

		System.out.println("Unidades no VerticalView: " + unidades);

		unidadeGrid = new MultivalueGrid<>(Vertical.Unidade.class, unidades).withColumns(config -> {
			config.addComboBoxColumn("Responsável", Vertical.Unidade::getResponsavel, Vertical.Unidade::setResponsavel,
					List.of("Marcelo", "Júnior", "Fabossi", "Dante", "Fernando")); // valores do ComboBox);

			config.addTextFieldColumn("Status", Vertical.Unidade::getStatus, Vertical.Unidade::setStatus);

			config.addTextFieldColumn("Estado", Vertical.Unidade::getEstado, Vertical.Unidade::setEstado,
					new ChainedConverter(//
							new br.com.tdec.intra.utils.converters.RemoveSimbolosEAcentos(), //
							new UpperCaseConverter()), //

					new StringLengthValidator("2 caracteres", 2, 2) // <-- validator
			);

			config.addDateFieldColumn("Criação", //
					Vertical.Unidade::getCriacao, //
					Vertical.Unidade::setCriacao, //
					(value, context) -> {
						if (value == null || value.equals("")) {
							return ValidationResult.error("Data é obrigatória");
						}
						if (value.isBefore(LocalDate.now())) {
							return ValidationResult.error("A data não pode ser anterior a hoje");
						}
						return ValidationResult.ok();
					});

			config.addDoubleFieldColumn("Valor", Vertical.Unidade::getValor, Vertical.Unidade::setValor);
		}).bind(model.getUnidades().getLista(), List.of("responsavel", "status", "estado", "criacao", "valor"))
				.setReadOnly(isReadOnly)//
				.setReadOnly(isReadOnly) // <<==== AQUI VOCÊ INFORMA O ESTADO
				.enableAddButton("Adicionar", () -> {
					Vertical.Unidade nova = new Vertical.Unidade();
					nova.setStatus("Ativo");
					nova.setResponsavel("");
					nova.setCriacao(LocalDate.now());
					nova.setValor(0.0);
					return nova;
				})//
				.addActionColumn()//
				.setReadOnly(false);

		unidadeGrid.setItems(unidades.getLista());
		unidadeGrid.refresh();

		// Adicione ao formulário
		form.addFormRow(unidadeGrid);

	}

	private boolean isEstadoDuplicado(String estado) {
		// Se a lista de unidades for nula ou vazia, não há duplicação
		if (model.getUnidades() == null || model.getUnidades().isEmpty()) {
			return false;
		}

		// Contar quantas vezes o estado aparece na lista
		long count = model.getUnidades().getLista().stream()
				.filter(unidade -> estado.equalsIgnoreCase(unidade.getEstado())).count();

		// Se o estado aparecer mais de uma vez, é duplicado
		return count > 0;
	}

	@Override
	public void updateReadOnlyState() {
		super.updateReadOnlyState(); // já aplica nos campos padrão
		unidadeGrid.setReadOnly(isReadOnly); // aplica a lógica de readOnly no grid customizado
	}

	public void save() {
		if (unidadeGrid != null) {
			unidadeGrid.syncToMultivalueFields();
			if (unidadeGrid.getEditor().isOpen()) {
				unidadeGrid.getEditor().save();
			}
		}
		super.save();
	}

}
