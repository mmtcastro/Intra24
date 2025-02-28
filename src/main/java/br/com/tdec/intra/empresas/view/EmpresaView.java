package br.com.tdec.intra.empresas.view;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.Flex;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

import br.com.tdec.intra.abs.AbstractValidator;
import br.com.tdec.intra.abs.AbstractViewDoc;
import br.com.tdec.intra.api.model.Receitaws;
import br.com.tdec.intra.api.model.Viacep;
import br.com.tdec.intra.api.services.ReceitawsService;
import br.com.tdec.intra.api.services.ViacepService;
import br.com.tdec.intra.empresas.componentes.EnderecoForm;
import br.com.tdec.intra.empresas.model.Empresa;
import br.com.tdec.intra.empresas.model.GrupoEconomico;
import br.com.tdec.intra.empresas.model.TipoEmpresa;
import br.com.tdec.intra.empresas.services.GrupoEconomicoService;
import br.com.tdec.intra.empresas.services.TipoEmpresaService;
import br.com.tdec.intra.pessoal.model.Colaborador;
import br.com.tdec.intra.pessoal.service.ColaboradorService;
import br.com.tdec.intra.services.Response;
import br.com.tdec.intra.utils.Utils;
import br.com.tdec.intra.utils.converters.RemoveSpacesConverter;
import br.com.tdec.intra.utils.converters.UpperCaseConverter;
import br.com.tdec.intra.utils.validators.CepValidator;
import br.com.tdec.intra.utils.validators.CnpjValidator;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Route(value = "empresa", layout = MainLayout.class)
@PageTitle("Empresa")
@RolesAllowed("ROLE_EVERYONE")
public class EmpresaView extends AbstractViewDoc<Empresa> {
	private static final long serialVersionUID = 1L;
	private GrupoEconomico grupoEconomico;
	private ComboBox<String> codigoGrupoEconomicoComboBox = new ComboBox<>("Grupo Econômico");
	private TextField codigoField = new TextField("Código");
	private ComboBox<String> tipoComboBox = new ComboBox<>("Tipo");
	private TextField nomeField = new TextField("Razão Social");
	private ComboBox<String> gerenteContaComboBox = new ComboBox<>("Gerente de Contas");
	private ComboBox<String> paisComboBox = new ComboBox<>("País");
	private ComboBox<String> ufComboBox = new ComboBox<>("Estado");
	private TextField cgcField = new TextField("CNPJ");
	private TextField telefoneField = new TextField("Telefone");
	private TextField emailField = new TextField("Email");
	private TextField enderecoField = new TextField("Endereço");
	private TextField numeroField = new TextField("Número");
	private TextField bairroField = new TextField("Bairro");
	private TextField cidadeField = new TextField("Cidade");
	private TextField cepField = new TextField("CEP");
	private TextField statusCnpjField = new TextField("Status CNPJ");
	private TextField bancoField = new TextField("Banco");
	private TextField agenciaField = new TextField("Agência");
	private TextField contaField = new TextField("Conta");
	@Autowired
	private GrupoEconomicoService grupoEconomicoService;
	private TipoEmpresaService tipoEmpresaService;
	private ColaboradorService colaboradorService;
	private ReceitawsService cnpjService;
	private ViacepService viacepService;
	private EnderecoForm<Empresa> enderecoForm;

	public EmpresaView(GrupoEconomicoService grupoEconomicoService, TipoEmpresaService tipoEmpresaService,
			ColaboradorService colaboradorService, ReceitawsService cnpjService, ViacepService viacepService) {
		super();
		this.tipoEmpresaService = tipoEmpresaService;
		this.colaboradorService = colaboradorService;
		this.cnpjService = cnpjService;
		this.viacepService = viacepService;

		addClassNames("abstract-view-doc.css", Width.FULL, Display.FLEX, Flex.AUTO, Margin.LARGE);

		configureComboBoxes();
		configureFieldActions();
	}

	@Override
	public void updateView() {
		System.out.println("Model é: " + model); // Teste para saber se o modelo está chegando

		if (model == null) {
			System.err.println("🚨 ERRO: Model ainda está NULL em updateView!");
			return;
		}

		// 🚀 Criar EnderecoForm antes de chamar super.updateView()
		if (enderecoForm == null) {
			enderecoForm = new EnderecoForm<>(binder, model);
			binderFields.add(enderecoForm); // 🚀 Adicionar à lista antes de updateView()
		}

		super.updateView(); // Agora o binderFields.forEach(this::add) já terá enderecoForm

	}

	private void configureComboBoxes() {
		// Define a ordenação ASC para a coluna "Codigo"
		List<QuerySortOrder> sortOrders = Collections
				.singletonList(new QuerySortOrder("Codigo", SortDirection.ASCENDING));
		// Configura ComboBox com Lazy Loading
		codigoGrupoEconomicoComboBox.setItems(query -> grupoEconomicoService.findAllByCodigo(query.getOffset(), //
				query.getLimit(), //
				sortOrders, //
				query.getFilter().orElse(""), GrupoEconomico.class //
				, false).stream().map(GrupoEconomico::getCodigo));

		codigoGrupoEconomicoComboBox.setPlaceholder("Selecione um Grupo Econômico");
		codigoGrupoEconomicoComboBox.setWidthFull();

		// Copia o grupo econômico para o campo "código" automaticamente quando for novo
		codigoGrupoEconomicoComboBox.addValueChangeListener(event -> {
			if (isNovo && event.getValue() != null) {
				codigoField.setValue(event.getValue() + "..."); // Adiciona "..." para indicar que o código será
																// copiado");
				Response<GrupoEconomico> response = grupoEconomicoService.findByCodigo(event.getValue());
				grupoEconomico = response.getModel();
			}
		});
		List<TipoEmpresa> tiposEmpresas = tipoEmpresaService.getTiposEmpresas();
		if (tiposEmpresas != null) {
			tipoComboBox.setItems(tiposEmpresas.stream()//
					.map(TipoEmpresa::getCodigo)//
					.collect(Collectors.toList()));
		}
		tipoComboBox.setPlaceholder("Selecione um Tipo");
		tipoComboBox.setWidthFull();

		List<Colaborador> funcionariosAtivos = colaboradorService.getFuncionariosAtivos();
		if (funcionariosAtivos != null) {
			gerenteContaComboBox.setItems(funcionariosAtivos.stream()//
					.map(Colaborador::getFuncionario)//
					.collect(Collectors.toList()));
		}
		gerenteContaComboBox.setPlaceholder("Selecione um Gerente de Contas");
		gerenteContaComboBox.setWidthFull();
		if (grupoEconomico != null) {
			gerenteContaComboBox.setValue(grupoEconomico.getGerenteConta());
		}

		ufComboBox.setItems(Utils.getUfs());
		ufComboBox.setPlaceholder("Selecione um Estado");
		ufComboBox.setWidthFull();

		paisComboBox.setItems(Utils.getPaises());
		paisComboBox.setValue("Brasil");
		paisComboBox.setPlaceholder("Selecione um País");
		paisComboBox.setWidthFull();
	}

	public void configureFieldActions() {

		cgcField.addValueChangeListener(event -> {
			String cnpj = cgcField.getValue();
			System.out.println("O CNPJ eh: " + cnpj);

			try {
				Receitaws cnpjData = ReceitawsService.findCnpj(cnpj);
				System.out.println("CNPJ: " + cnpjData);
				if (cnpjData != null) {
					enderecoField.setValue(cnpjData.getLogradouro()); // Preenche o logradouro
				} else {
					enderecoField.setValue(null);
				}
			} catch (Exception e) {
				e.printStackTrace();
				enderecoField.setValue("Erro ao buscar CNPJ");
			}
		});

		cepField.addValueChangeListener(event -> {
			String cep = cepField.getValue();
			System.out.println("O CEP eh: " + cep);

			try {
				Viacep viacepData = ViacepService.findCep(cep);
				System.out.println("CEP: " + viacepData);
				if (viacepData != null) {
					enderecoField.setValue(viacepData.getLogradouro()); // Preenche o logradouro
				} else {
					enderecoField.setValue(null);
				}
			} catch (Exception e) {
				e.printStackTrace();
				enderecoField.setValue("Erro ao buscar CNPJ");
			}
		});

		tipoComboBox.addValueChangeListener(event -> {
			if (event.getValue() != null) {
				Notification.show("Tipo selecionado: " + event.getValue());
			}
		});

	}

	protected void initBinder() {
		if (isNovo) {
			binder.forField(codigoGrupoEconomicoComboBox).asRequired("Selecione um Grupo Econômico")
					.bind(Empresa::getCodigoGrupoEconomico, Empresa::setCodigoGrupoEconomico);
			binder.forField(codigoField).asRequired("Entre com um código").withNullRepresentation("")
					.withConverter(new UpperCaseConverter()).withConverter(new RemoveSpacesConverter())
					.withValidator(new AbstractValidator.CodigoValidator<>(service))
					.bind(Empresa::getCodigo, Empresa::setCodigo);
		} else {
			binder.forField(codigoGrupoEconomicoComboBox).asRequired("Selecione um Grupo Econômico")
					.bind(Empresa::getCodigoGrupoEconomico, Empresa::setCodigoGrupoEconomico);
			readOnlyFields.add(codigoGrupoEconomicoComboBox);
			binder.forField(codigoField).asRequired("Entre com um código").withNullRepresentation("")
					.withConverter(new UpperCaseConverter()).withConverter(new RemoveSpacesConverter())
					.bind(Empresa::getCodigo, Empresa::setCodigo);
			readOnlyFields.add(codigoField);
		}
		binder.forField(tipoComboBox).asRequired("Selecione um Tipo").bind(Empresa::getTipo, Empresa::setTipo);

		binder.forField(nomeField).asRequired("Informe a Razão Social").bind(Empresa::getNome, Empresa::setNome);
		binder.forField(gerenteContaComboBox).asRequired("Informe o Gerente de Conta").bind(Empresa::getGerenteConta,
				Empresa::setGerenteConta);
		paisComboBox.setValue("Brasil");
		binder.forField(paisComboBox).asRequired("Selecione um País").bind(Empresa::getPais, Empresa::setPais);
		binder.forField(cgcField).withValidator(new CnpjValidator()).bind(Empresa::getCgc, Empresa::setCgc);
		binder.forField(telefoneField).bind(Empresa::getTelefones, Empresa::setTelefones);
		binder.forField(cepField).withValidator(new CepValidator()).bind(Empresa::getCep, Empresa::setCep);
		binder.forField(emailField).withValidator(new br.com.tdec.intra.utils.validators.EmailValidator())
				.bind(Empresa::getEmailNfeServicos, Empresa::setEmailNfeServicos);
		binder.forField(enderecoField).bind(Empresa::getEndereco, Empresa::setEndereco);
		binder.forField(numeroField).bind(Empresa::getNumero, Empresa::setNumero);
		binder.forField(bairroField).bind(Empresa::getBairro, Empresa::setBairro);
		binder.forField(cidadeField).bind(Empresa::getCidade, Empresa::setCidade);
		binder.forField(ufComboBox).asRequired("Selecione um Estado").bind(Empresa::getEstado, Empresa::setEstado);
		binder.forField(statusCnpjField).bind(Empresa::getStatusCnpj, Empresa::setStatusCnpj);
		binder.forField(bancoField).bind(Empresa::getBanco, Empresa::setBanco);
		binder.forField(agenciaField).bind(Empresa::getAgencia, Empresa::setAgencia);
		binder.forField(contaField).bind(Empresa::getConta, Empresa::setConta);

		binder.setBean(model);

		binderFields.clear();
		binderFields.add(codigoGrupoEconomicoComboBox);
		binderFields.add(codigoField);
		binderFields.add(tipoComboBox);
		binderFields.add(nomeField);
		binderFields.add(gerenteContaComboBox);
		binderFields.add(telefoneField);
		binderFields.add(paisComboBox);
		binderFields.add(cepField);
		// binderFields.add(cgcField);
		addComponentToBinderFields(cgcField, 2);
		if (enderecoForm != null) {
			binderFields.add(enderecoForm);
		}

		// binderFields.add(enderecoField);
		// binderFields.add(numeroField);
		// binderFields.add(bairroField);
		// binderFields.add(cidadeField);
		// binderFields.add(ufComboBox);
		binderFields.add(emailField);
		binderFields.add(statusCnpjField);
		binderFields.add(bancoField);
		binderFields.add(agenciaField);
		binderFields.add(contaField);

	}

}
