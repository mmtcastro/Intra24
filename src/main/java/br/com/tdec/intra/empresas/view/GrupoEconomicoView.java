package br.com.tdec.intra.empresas.view;

import java.io.Serial;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractService.DeleteResponse;
import br.com.tdec.intra.abs.AbstractValidator;
import br.com.tdec.intra.abs.AbstractViewDoc;
import br.com.tdec.intra.empresas.componentes.EmpresasGrid;
import br.com.tdec.intra.empresas.model.Empresa;
import br.com.tdec.intra.empresas.model.GrupoEconomico;
import br.com.tdec.intra.empresas.model.OrigemCliente;
import br.com.tdec.intra.empresas.model.TipoEmpresa;
import br.com.tdec.intra.empresas.services.GrupoEconomicoService;
import br.com.tdec.intra.empresas.services.OrigemClienteService;
import br.com.tdec.intra.empresas.services.TipoEmpresaService;
import br.com.tdec.intra.inter.view.HasTopActions;
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
public class GrupoEconomicoView extends AbstractViewDoc<GrupoEconomico> implements HasTopActions {
	@Serial
	private static final long serialVersionUID = 1L;
	private TextField codigoField = new TextField("Código");
	private TextField descricaoField = new TextField("Descrição");
	private ComboBox<String> gerenteContaComboBox = new ComboBox<>("Gerente de Contas");
	private ComboBox<String> tipoComboBox = new ComboBox<>("Tipo");
	private ComboBox<String> origemClienteComboBox = new ComboBox<>("Origem do Cliente");
	private EmpresasGrid empresasGrid;

	public GrupoEconomicoView(ColaboradorService colaboradorService, TipoEmpresaService tipoEmpresaService,
			OrigemClienteService origemClienteService) {
		super();
		// showUploads = false;
		// showObs = false;

		List<Colaborador> funcionariosAtivos = colaboradorService.getFuncionariosAtivos();
		if (funcionariosAtivos != null) {
			gerenteContaComboBox.setItems(funcionariosAtivos.stream()//
					.map(Colaborador::getFuncionario)//
					.collect(Collectors.toList()));
		}
		gerenteContaComboBox.setPlaceholder("Selecione um Gerente de Contas");
		gerenteContaComboBox.setVisible(false);
		List<TipoEmpresa> tiposEmpresas = tipoEmpresaService.getTiposEmpresas();
		if (tiposEmpresas != null) {
			tipoComboBox.setItems(tiposEmpresas.stream()//
					.map(TipoEmpresa::getCodigo)//
					.collect(Collectors.toList()));
		}
		tipoComboBox.setPlaceholder("Selecione um Tipo");
		List<OrigemCliente> origensCliente = origemClienteService.getOrigensClientes();
		if (origensCliente != null) {
			origemClienteComboBox.setItems(origensCliente.stream()//
					.map(OrigemCliente::getCodigo)//
					.collect(Collectors.toList()));
		}
		origemClienteComboBox.setPlaceholder("Selecione a Origem do Cliente");
		origemClienteComboBox.setVisible(false);

		tipoComboBox.addValueChangeListener(event -> {
			String selectedTipo = event.getValue();

			// Torna o campo visível ou invisível com base no tipo
			origemClienteComboBox.setVisible("Cliente".equalsIgnoreCase(selectedTipo));
			gerenteContaComboBox.setVisible("Cliente".equalsIgnoreCase(selectedTipo));
		});

	}

	protected void initBinder() {
		if (isNovo) {
			binder.forField(codigoField).asRequired("Entre com um código").withNullRepresentation("")
					.withConverter(new UpperCaseConverter()).withConverter(new RemoveSpacesConverter())
					.withValidator(codigo -> codigo.matches("^[0-9A-Za-z]*[A-Za-z]$"),
							"O código pode começar com número, mas deve terminar com letra (sem acentos ou símbolos)")
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

		form.add(codigoField);
		form.addFormRow(tipoComboBox, origemClienteComboBox);
		form.add(gerenteContaComboBox);
		form.addFormRow(descricaoField);
		form.setColspan(descricaoField, 2);

//		// Adiciona os campos ao binderFields
//		binderFields.clear();
//		binderFields.add(codigoField);
//		binderFields.add(tipoComboBox);
//		binderFields.add(gerenteContaComboBox);
//		binderFields.add(origemClienteComboBox);
//		binderFields.add(descricaoField);
//
//		// Adiciona o campo opcional com base no tipo inicial
//		if ("Cliente".equalsIgnoreCase(model.getTipo())) {
//			binderFields.add(binderFields.indexOf(descricaoField), origemClienteComboBox);
//		}
//
//		if (!this.isNovo) {
//			empresasGrid = new EmpresasGrid(model.getCodigo(), ((GrupoEconomicoService) service));
//			if (empresasGrid != null && empresasGrid.getEmpresas().size() > 0) {
//				// Garante que o grid ocupa toda a largura
//				empresasGrid.setWidthFull();
//				addComponentToBinderFields(empresasGrid, 1);
//			}
//		}

	}

	public DeleteResponse delete() {
		/**
		 * antes de apagar, tenho que ver se o grupo econômico tem empresas dentro dele.
		 * 
		 */
		List<Empresa> empresas = ((GrupoEconomicoService) service).findEmpresasByGrupoEconomico(model.getCodigo());
		if (!empresas.isEmpty()) {
			// Exibe diálogo de erro
			Dialog dialog = new Dialog();
			dialog.setHeaderTitle("Erro ao Excluir");

			VerticalLayout content = new VerticalLayout();
			content.add(new Span("Este Grupo Econômico não pode ser apagado, pois há empresas vinculadas a ele."));
			content.add(new Span("Remova as empresas antes de excluir o Grupo Econômico."));

			Button fechar = new Button("Fechar", event -> dialog.close());
			fechar.getStyle().set("margin-top", "10px");

			dialog.add(content, fechar);
			dialog.open();

			// Interrompe a exclusão
			// Retorna um DeleteResponse indicando erro
			DeleteResponse response = new DeleteResponse();
			response.setStatus("error");
			response.setStatusCode(400);
			response.setStatusText("Operação bloqueada");
			response.setMessage("Grupo Econômico com empresas vinculadas não pode ser excluído.");
			response.setDetails("Remova as empresas antes de excluir o grupo.");
			return response;
		}

		return super.delete();
	}

	@Override
	public Component getTopActions() {
		MenuBar menuBar = new MenuBar();

		// Criar Empresa com ícone
		Span textCriar = new Span("Criar Empresa");
		HorizontalLayout criarLayout = new HorizontalLayout(textCriar);
		criarLayout.setPadding(false);
		criarLayout.setSpacing(true);
		menuBar.addItem(criarLayout, e -> {
			criarEmpresa();
		});

		return menuBar;
	}

	public void criarEmpresa() {
		if (model.getMeta().getUnid() == null || model.getId() == null || model.getCodigo() == null) {
			Notification.show("Dados do Grupo Econômico estão incompletos. Verifique antes de criar a empresa.");
			return;
		}

		Map<String, List<String>> param = Map.of(//
				"unidGrupoEconomico", List.of(model.getMeta().getUnid()), //
				"codigoGrupoEconomico", List.of(model.getCodigo()), //
				"idGrupoEconomico", List.of(model.getId()));
		UI.getCurrent().navigate(EmpresaView.class, new QueryParameters(param));

	}

}
