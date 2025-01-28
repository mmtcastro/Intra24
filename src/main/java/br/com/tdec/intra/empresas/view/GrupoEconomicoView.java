package br.com.tdec.intra.empresas.view;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.Flex;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

import br.com.tdec.intra.abs.AbstractValidator;
import br.com.tdec.intra.abs.AbstractViewDoc;
import br.com.tdec.intra.empresas.model.GrupoEconomico;
import br.com.tdec.intra.empresas.model.OrigemCliente;
import br.com.tdec.intra.empresas.model.TipoEmpresa;
import br.com.tdec.intra.empresas.services.OrigemClienteService;
import br.com.tdec.intra.empresas.services.TipoEmpresaService;
import br.com.tdec.intra.pessoal.model.Colaborador;
import br.com.tdec.intra.pessoal.service.ColaboradorService;
import br.com.tdec.intra.utils.converters.RemoveSpacesConverter;
import br.com.tdec.intra.utils.converters.UpperCaseConverter;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Route(value = "grupoeconomico", layout = MainLayout.class)
@PageTitle("Grupo Econômico")
@RolesAllowed("ROLE_EVERYONE")
public class GrupoEconomicoView extends AbstractViewDoc<GrupoEconomico> {
	private static final long serialVersionUID = 1L;
	private TextField codigoField = new TextField("Código");
	private TextField descricaoField = new TextField("Descrição");
	private ComboBox<String> gerenteContaComboBox = new ComboBox<>("Gerente de Contas");
	private ComboBox<String> tipoComboBox = new ComboBox<>("Tipo");
	private ComboBox<String> origemClienteComboBox = new ComboBox<>("Origem do Cliente");

	public GrupoEconomicoView(ColaboradorService colaboradorService, TipoEmpresaService tipoEmpresaService,
			OrigemClienteService origemClienteService) {
		super();
		addClassNames("abstract-view-doc.css", Width.FULL, Display.FLEX, Flex.AUTO, Margin.LARGE);
		List<Colaborador> funcionariosAtivos = colaboradorService.getFuncionariosAtivos();
		if (funcionariosAtivos != null) {
			gerenteContaComboBox.setItems(funcionariosAtivos.stream()//
					.map(Colaborador::getFuncionario)//
					.collect(Collectors.toList()));
		}
		gerenteContaComboBox.setPlaceholder("Selecione um Gerente de Contas");
		gerenteContaComboBox.setWidthFull();
		gerenteContaComboBox.setVisible(false);
		List<TipoEmpresa> tiposEmpresas = tipoEmpresaService.getTiposEmpresas();
		if (tiposEmpresas != null) {
			tipoComboBox.setItems(tiposEmpresas.stream()//
					.map(TipoEmpresa::getCodigo)//
					.collect(Collectors.toList()));
		}
		tipoComboBox.setPlaceholder("Selecione um Tipo");
		tipoComboBox.setWidthFull();

		List<OrigemCliente> origensCliente = origemClienteService.getOrigensClientes();
		if (origensCliente != null) {
			origemClienteComboBox.setItems(origensCliente.stream()//
					.map(OrigemCliente::getCodigo)//
					.collect(Collectors.toList()));
		}
		origemClienteComboBox.setPlaceholder("Selecione a Origem do Cliente");
		origemClienteComboBox.setWidthFull();
		origemClienteComboBox.setVisible(false);

		tipoComboBox.addValueChangeListener(event -> {
			String selectedTipo = event.getValue();
			System.out.println("Tipo mudou de estado: " + selectedTipo);

			// Torna o campo visível ou invisível com base no tipo
			origemClienteComboBox.setVisible("Cliente".equalsIgnoreCase(selectedTipo));
			gerenteContaComboBox.setVisible("Cliente".equalsIgnoreCase(selectedTipo));
		});

	}

	protected void initBinder() {
		if (isNovo) {
			binder.forField(codigoField).asRequired("Entre com um código").withNullRepresentation("")
					.withConverter(new UpperCaseConverter()).withConverter(new RemoveSpacesConverter())
					.withValidator(new AbstractValidator.CodigoValidator<>(service))
					.bind(GrupoEconomico::getCodigo, GrupoEconomico::setCodigo);
		} else {
			binder.forField(codigoField).asRequired("Entre com um código").withNullRepresentation("")
					.withConverter(new UpperCaseConverter()).withConverter(new RemoveSpacesConverter())
					.bind(GrupoEconomico::getCodigo, GrupoEconomico::setCodigo);
			readOnlyFields.add(codigoField);
		}

		binder.forField(tipoComboBox).asRequired("Selecione um Tipo").bind(GrupoEconomico::getTipo,
				GrupoEconomico::setTipo);

//		binder.forField(gerenteContaComboBox).asRequired("Selecione um Gerente de Contas")
//				.bind(GrupoEconomico::getGerenteConta, (grupo, value) -> {
//					grupo.setGerenteConta(value);
//				});

		binder.forField(gerenteContaComboBox).withValidator(value -> {
			if ("Cliente".equalsIgnoreCase(tipoComboBox.getValue())) {
				return value != null && !value.trim().isEmpty(); // Validação apenas se for "Cliente"
			}
			return true; // Sem validação se não for "Cliente"
		}, "Selecione um Gerente de Contas").withNullRepresentation("").bind(GrupoEconomico::getGerenteConta,
				GrupoEconomico::setGerenteConta);

		binder.forField(descricaoField).asRequired("Entre com uma descrição").bind(GrupoEconomico::getDescricao,
				GrupoEconomico::setDescricao);

		// Binding do campo opcional "Origem do Cliente" com validação condicional. Caso
		// o campo esteja escondido valida true
		binder.forField(origemClienteComboBox).withValidator(value -> {
			if ("Cliente".equalsIgnoreCase(tipoComboBox.getValue())) {
				return value != null && !value.trim().isEmpty(); // Validação apenas se for "Cliente"
			}
			return true; // Sem validação se não for "Cliente"
		}, "Selecione a Origem do Cliente").withNullRepresentation("").bind(GrupoEconomico::getOrigemCliente,
				GrupoEconomico::setOrigemCliente);

		binder.setBean(model);

		// Adiciona os campos ao binderFields
		binderFields.clear();
		binderFields.add(codigoField);
		binderFields.add(tipoComboBox);
		binderFields.add(gerenteContaComboBox);
		binderFields.add(origemClienteComboBox);
		binderFields.add(descricaoField);

		// Adiciona o campo opcional com base no tipo inicial
		if ("Cliente".equalsIgnoreCase(model.getTipo())) {
			binderFields.add(binderFields.indexOf(descricaoField), origemClienteComboBox);
		}

	}

}
