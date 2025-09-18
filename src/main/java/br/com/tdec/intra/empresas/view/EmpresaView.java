package br.com.tdec.intra.empresas.view;

import java.io.Serial;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.Flex;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

import br.com.tdec.intra.abs.AbstractValidator;
import br.com.tdec.intra.abs.AbstractViewDoc;
import br.com.tdec.intra.api.model.Viacep;
import br.com.tdec.intra.api.services.ViacepService;
import br.com.tdec.intra.empresas.api.model.ReceitaWs;
import br.com.tdec.intra.empresas.api.service.ReceitaWsService;
import br.com.tdec.intra.empresas.api.view.ReceitaWsView;
import br.com.tdec.intra.empresas.model.Empresa;
import br.com.tdec.intra.empresas.model.GrupoEconomico;
import br.com.tdec.intra.empresas.model.TipoEmpresa;
import br.com.tdec.intra.empresas.services.EmpresaService;
import br.com.tdec.intra.empresas.services.GrupoEconomicoService;
import br.com.tdec.intra.empresas.services.TipoEmpresaService;
import br.com.tdec.intra.pessoal.model.Colaborador;
import br.com.tdec.intra.pessoal.service.ColaboradorService;
import br.com.tdec.intra.services.Response;
import br.com.tdec.intra.utils.Utils;
import br.com.tdec.intra.utils.converters.RemoveSpacesConverter;
import br.com.tdec.intra.utils.converters.UpperCaseConverter;
import br.com.tdec.intra.utils.converters.ZonedDateTimeToLocalDateConverter;
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
public class EmpresaView extends AbstractViewDoc<Empresa> implements BeforeEnterObserver {
	@Serial
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
	private TextField porteField = new TextField("Porte");
	private TextField statusCnpjField = new TextField("Status CNPJ");
	private TextField bancoField = new TextField("Banco");
	private TextField agenciaField = new TextField("Agência");
	private TextField contaField = new TextField("Conta");
	@Autowired
	private GrupoEconomicoService grupoEconomicoService;
	private TipoEmpresaService tipoEmpresaService;
	private ColaboradorService colaboradorService;
	private ReceitaWsService cnpjService;
	private ViacepService viacepService;
	private ReceitaWsView receitaWsView;
	private String codigoGrupoEconomico;
	private TextField nomeFantasiaField = new TextField("Nome Fantasia");
	private TextField clienteField = new TextField("Cliente");
	private DatePicker dataAberturaField = new DatePicker("Data de Abertura");
	private TextField paginaWebField = new TextField("Página Web");
	private TextField emailNfeMateriaisField = new TextField("Email NFe Materiais");
	private TextField tipoInscricaoField = new TextField("Tipo Inscrição");
	private TextField cpfField = new TextField("CPF");
	private TextField inscricaoField = new TextField("Inscrição");
	private TextField cnaeField = new TextField("CNAE");
	private TextField indicadorIEField = new TextField("Indicador IE");
	private TextField ccmField = new TextField("CCM");
	private TextField regimeTributarioField = new TextField("Regime Tributário");
	private TextField verticalField = new TextField("Vertical");
	private TextField membroSuframaField = new TextField("Membro Suframa");
	private TextField inscricaoSuframaField = new TextField("Inscrição Suframa");
	private TextField bancoNumeroField = new TextField("Banco Número");
	private TextField agenciaDigitoField = new TextField("Agência Dígito");
	private TextField contaDigitoField = new TextField("Conta Dígito");
	private DatePicker ultimaConsultaCnpjField = new DatePicker("Última Consulta CNPJ");
	private Checkbox fezNegociosField = new Checkbox("Fez Negócios");
	private Checkbox temFaturasField = new Checkbox("Tem Faturas");

	public EmpresaView(GrupoEconomicoService grupoEconomicoService, TipoEmpresaService tipoEmpresaService,
			ColaboradorService colaboradorService, ReceitaWsService cnpjService, ViacepService viacepService) {
		super();
		this.tipoEmpresaService = tipoEmpresaService;
		this.colaboradorService = colaboradorService;
		this.cnpjService = cnpjService;
		this.viacepService = viacepService;

		addClassNames("abstract-view-doc.css", Width.FULL, Display.FLEX, Flex.AUTO, Margin.LARGE);

		configureComboBoxes();

	}

	/**
	 * Verifica se uma nova empresa está sendo criada a partir de um GrupoEconomico
	 * 
	 */
	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		Map<String, List<String>> params = event.getLocation().getQueryParameters().getParameters();
		String unidGrupoEconomico = params.getOrDefault("unidGrupoEconomico", List.of("")).get(0);
		codigoGrupoEconomico = params.getOrDefault("codigoGrupoEconomico", List.of("")).get(0);

		if (!unidGrupoEconomico.isBlank() && !codigoGrupoEconomico.isBlank()) {
			carregarGrupoEconomico(unidGrupoEconomico, codigoGrupoEconomico);
		}
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
				codigoGrupoEconomico = event.getValue();
				print("Buscando event.getValue = " + codigoGrupoEconomico);
				// Response<GrupoEconomico> response =
				// grupoEconomicoService.findByCodigo(event.getValue());
				Response<GrupoEconomico> response = grupoEconomicoService
						.findGrupoEconomicoSemMime(codigoGrupoEconomico);

				grupoEconomico = response.getModel();
				if (grupoEconomico != null) {
					preencheCamposVindosDoGrupoEconomico();
				}

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

	private void configureCnpjActions() {
		cgcField.addBlurListener(event -> {
			String cnpj = cgcField.getValue();
			String cnpjSemMascara = cnpj.replaceAll("[^0-9]", "");
			if (cnpjSemMascara.length() != 14) {
				return;
			}

			try {
				ReceitaWs receita = ReceitaWsService.findCnpj(cnpjSemMascara);
				if (receita != null) {
					// 🔹 Atualiza o modelo Empresa diretamente
					model.setNome(Optional.ofNullable(receita.getNome()).orElse(""));
					model.setNomeFantasia(Optional.ofNullable(receita.getFantasia()).orElse(""));
					model.setCgc(formatarCNPJ(cnpjSemMascara));
					model.setTelefones(Optional.ofNullable(receita.getTelefone()).orElse(""));
					model.setEmailNfeServicos(Optional.ofNullable(receita.getEmail()).orElse(""));
					model.setEndereco(Optional.ofNullable(receita.getLogradouro()).orElse(""));
					model.setNumero(Optional.ofNullable(receita.getNumero()).orElse(""));
					model.setComplemento(Optional.ofNullable(receita.getComplemento()).orElse(""));
					model.setBairro(Optional.ofNullable(receita.getBairro()).orElse(""));
					model.setCidade(Optional.ofNullable(receita.getMunicipio()).orElse(""));
					model.setEstado(Optional.ofNullable(receita.getUf()).orElse(""));
					model.setCep(Optional.ofNullable(Utils.normalizarCep(receita.getCep())).orElse(""));
					model.setStatusCnpj(Optional.ofNullable(receita.getStatus()).orElse(""));
					model.setPorte(Optional.ofNullable(receita.getPorte()).orElse(""));
					model.setDataAbertura(Optional.ofNullable(receita.getAbertura()).map(d -> LocalDate
							.parse(d, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay(ZoneId.systemDefault()))
							.orElse(null));

					// 🔹 Força o binder a refletir no form
					binder.readBean(model);

					// Opcional: mostrar o painel detalhado ReceitaWs
					receitaWsView = new ReceitaWsView();
					receitaWsView.popular(receita);
					form.addFormRow(receitaWsView);
				}
			} catch (Exception e) {
				Notification.show("Erro ao buscar CNPJ: " + e.getMessage(), 4000, Notification.Position.MIDDLE);
				e.printStackTrace();
			}
		});
	}

	public void configureCepActions() {
		if (model == null) {
			return;
		}
		if (model.getCep() == null) {
			return;
		}
		if (model.getCep().equals("")) {
			return;
		}

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
				enderecoField.setValue("Erro ao buscar CEP");
			}
		});

	}

	protected void initBinder() {
		if (isNovo) {
			binder.forField(codigoGrupoEconomicoComboBox).asRequired("Selecione um Grupo Econômico")
					.bind(Empresa::getCodigoGrupoEconomico, Empresa::setCodigoGrupoEconomico);
			binder.forField(codigoField).asRequired("Entre com um código").withNullRepresentation("")
					.withConverter(new UpperCaseConverter()).withConverter(new RemoveSpacesConverter())
					.withValidator(codigo -> codigo.matches("^[0-9A-Za-z]*[A-Za-z]$"),
							"O código pode começar com número, mas deve terminar com letra (sem acentos ou símbolos)")
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

		binder.forField(gerenteContaComboBox).asRequired("Informe o Gerente de Conta").bind(Empresa::getGerenteConta,
				Empresa::setGerenteConta);
		paisComboBox.setValue("Brasil");
		binder.forField(paisComboBox).asRequired("Selecione um País").bind(Empresa::getPais, Empresa::setPais);

		binder.forField(cgcField)//
				.withValidator(new CnpjUnicoValidator((EmpresaService) service))//
				.withValidator(new CnpjValidator(paisComboBox))// fora do brasil nao valida
				.bind(Empresa::getCgc, Empresa::setCgc);
		cgcField.setPlaceholder("00.000.000/0000-00");
		// Ao sair do campo, aplica a máscara (se tiver 14 dígitos numéricos)
		cgcField.getElement().addEventListener("blur", e -> {
			String raw = cgcField.getValue().replaceAll("[^\\d]", "");
			if (raw.length() == 14) {
				cgcField.setValue(formatarCNPJ(raw));
			}
		});

		configureCnpjActions(); // tem que validar antes

		binder.forField(nomeField).asRequired("Informe a Razão Social").bind(Empresa::getNome, Empresa::setNome);
		binder.forField(telefoneField).bind(Empresa::getTelefones, Empresa::setTelefones);
		binder.forField(cepField).withValidator(new CepValidator()).bind(Empresa::getCep, Empresa::setCep);
		binder.forField(emailField).withValidator(new br.com.tdec.intra.utils.validators.EmailValidator())
				.bind(Empresa::getEmailNfeServicos, Empresa::setEmailNfeServicos);
		configureCepActions();
		binder.forField(enderecoField).bind(Empresa::getEndereco, Empresa::setEndereco);
		binder.forField(numeroField).bind(Empresa::getNumero, Empresa::setNumero);
		binder.forField(bairroField).bind(Empresa::getBairro, Empresa::setBairro);
		binder.forField(cidadeField).bind(Empresa::getCidade, Empresa::setCidade);
		binder.forField(ufComboBox).asRequired("Selecione um Estado").bind(Empresa::getEstado, Empresa::setEstado);
		binder.forField(statusCnpjField).bind(Empresa::getStatusCnpj, Empresa::setStatusCnpj);
		binder.forField(bancoField).bind(Empresa::getBanco, Empresa::setBanco);
		binder.forField(agenciaField).bind(Empresa::getAgencia, Empresa::setAgencia);
		binder.forField(contaField).bind(Empresa::getConta, Empresa::setConta);
		binder.forField(porteField).bind(Empresa::getPorte, Empresa::setPorte);
		binder.forField(nomeFantasiaField).bind(Empresa::getNomeFantasia, Empresa::setNomeFantasia);
		binder.forField(clienteField).bind(Empresa::getCliente, Empresa::setCliente);
		binder.forField(dataAberturaField) // DatePicker
				.withConverter(new ZonedDateTimeToLocalDateConverter())
				.bind(Empresa::getDataAbertura, Empresa::setDataAbertura);
		binder.forField(paginaWebField).bind(Empresa::getPaginaWeb, Empresa::setPaginaWeb);
		binder.forField(emailNfeMateriaisField).bind(Empresa::getEmailNfeMateriais, Empresa::setEmailNfeMateriais);
		binder.forField(tipoInscricaoField).bind(Empresa::getTipoInscricao, Empresa::setTipoInscricao);
		binder.forField(cpfField).bind(Empresa::getCpf, Empresa::setCpf);
		binder.forField(inscricaoField).bind(Empresa::getInscricao, Empresa::setInscricao);
		binder.forField(cnaeField).bind(Empresa::getCnae, Empresa::setCnae);
		binder.forField(indicadorIEField).bind(Empresa::getIndicadorIE, Empresa::setIndicadorIE);
		binder.forField(ccmField).bind(Empresa::getCcm, Empresa::setCcm);
		binder.forField(regimeTributarioField).bind(Empresa::getRegimeTributario, Empresa::setRegimeTributario);
		binder.forField(verticalField).bind(Empresa::getVertical, Empresa::setVertical);
		binder.forField(membroSuframaField).bind(Empresa::getMembroSuframa, Empresa::setMembroSuframa);
		binder.forField(inscricaoSuframaField).bind(Empresa::getInscricaoSuframa, Empresa::setInscricaoSuframa);
		binder.forField(bancoNumeroField).bind(Empresa::getBancoNumero, Empresa::setBancoNumero);
		binder.forField(agenciaDigitoField).bind(Empresa::getAgenciaDigito, Empresa::setAgenciaDigito);
		binder.forField(contaDigitoField).bind(Empresa::getContaDigito, Empresa::setContaDigito);
		binder.forField(ultimaConsultaCnpjField).withConverter(new ZonedDateTimeToLocalDateConverter())
				.bind(Empresa::getUltimaConsultaCnpj, Empresa::setUltimaConsultaCnpj);
		binder.forField(tipoInscricaoField).bind(Empresa::getTipoInscricao, Empresa::setTipoInscricao);
		binder.forField(cpfField).bind(Empresa::getCpf, Empresa::setCpf);
		binder.forField(inscricaoField).bind(Empresa::getInscricao, Empresa::setInscricao);
		binder.forField(cnaeField).bind(Empresa::getCnae, Empresa::setCnae);
		binder.forField(indicadorIEField).bind(Empresa::getIndicadorIE, Empresa::setIndicadorIE);
		binder.forField(ccmField).bind(Empresa::getCcm, Empresa::setCcm);
		binder.forField(regimeTributarioField).bind(Empresa::getRegimeTributario, Empresa::setRegimeTributario);
		binder.forField(verticalField).bind(Empresa::getVertical, Empresa::setVertical);
		binder.forField(membroSuframaField).bind(Empresa::getMembroSuframa, Empresa::setMembroSuframa);
		binder.forField(inscricaoSuframaField).bind(Empresa::getInscricaoSuframa, Empresa::setInscricaoSuframa);
		binder.forField(bancoNumeroField).bind(Empresa::getBancoNumero, Empresa::setBancoNumero);
		binder.forField(agenciaDigitoField).bind(Empresa::getAgenciaDigito, Empresa::setAgenciaDigito);
		binder.forField(contaDigitoField).bind(Empresa::getContaDigito, Empresa::setContaDigito);
		binder.forField(fezNegociosField).bind(Empresa::getFezNegocios, Empresa::setFezNegocios);
		binder.forField(temFaturasField).bind(Empresa::getTemFaturas, Empresa::setTemFaturas);

		form.addFormRow(codigoGrupoEconomicoComboBox, codigoField);
		form.addFormRow(tipoComboBox, gerenteContaComboBox);
		form.addFormRow(paisComboBox, cgcField);
		form.addFormRow(nomeField, telefoneField);

		form.addFormRow(cepField);
		form.addFormRow(enderecoField, numeroField);
		form.addFormRow(bairroField); //
		form.addFormRow(bairroField, cidadeField, ufComboBox);

		form.addFormRow(emailField, statusCnpjField);

		form.addFormRow(bancoField);
		form.addFormRow(agenciaField);
		form.addFormRow(contaField);

		form.addFormRow(porteField);
		form.addFormRow(nomeFantasiaField, clienteField);
		form.addFormRow(dataAberturaField, paginaWebField);
		form.addFormRow(emailNfeMateriaisField, tipoInscricaoField);
		form.addFormRow(cpfField, inscricaoField);
		form.addFormRow(cnaeField, indicadorIEField);
		form.addFormRow(ccmField, regimeTributarioField);
		form.addFormRow(verticalField, membroSuframaField);
		form.addFormRow(inscricaoSuframaField, bancoNumeroField);
		form.addFormRow(agenciaDigitoField, contaDigitoField);
		form.addFormRow(ultimaConsultaCnpjField);
		form.addFormRow(nomeFantasiaField, clienteField);
		form.addFormRow(dataAberturaField, paginaWebField);
		form.addFormRow(emailNfeMateriaisField, tipoInscricaoField);
		form.addFormRow(cpfField, inscricaoField);
		form.addFormRow(cnaeField, indicadorIEField);
		form.addFormRow(ccmField, regimeTributarioField);
		form.addFormRow(verticalField, membroSuframaField);
		form.addFormRow(inscricaoSuframaField, bancoNumeroField);
		form.addFormRow(agenciaDigitoField, contaDigitoField);
		form.addFormRow(ultimaConsultaCnpjField);
		form.addFormRow(fezNegociosField, temFaturasField);

	}

	private void carregarGrupoEconomico(String unidGrupoEconomico, String codigoGrupoEconomico) {
		/*
		 * só preciso setar o valor do codigoGrupoComboBox, pois o setValue faz o
		 * eventChangeListener rodar com a mesma lógica de quando o usuário sai do
		 * campo.
		 * 
		 */
		codigoGrupoEconomicoComboBox.setItems(codigoGrupoEconomico);
		codigoGrupoEconomicoComboBox.setValue(codigoGrupoEconomico);
	}

	private String formatarCNPJ(String cnpj) {
		return cnpj.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
	}

	private class CnpjUnicoValidator implements Validator<String> {

		private static final long serialVersionUID = 1L;
		private final EmpresaService service;

		public CnpjUnicoValidator(EmpresaService service) {
			this.service = service;
		}

		@Override
		public ValidationResult apply(String value, ValueContext context) {
			if (value == null || value.trim().isEmpty()) {
				return ValidationResult.ok();
			}

			Response<Empresa> response = service.findByCnpj(value);
			Empresa encontrada = response.getModel();

			if (!response.isSuccess() || encontrada == null) {
				return ValidationResult.ok();
			}

			// Verifica se o CNPJ pertence a outra empresa
			if (!Objects.equals(encontrada.getUnid(), model.getUnid())) {
				return ValidationResult.error("Este CNPJ já está cadastrado para a empresa " + encontrada.getCodigo());
			}
			print("Chegou aqui, cnpj ok");
			return ValidationResult.ok();
		}
	}

	public void preencheCamposVindosDoGrupoEconomico() {
		// tipoComboBox.setItems(grupoEconomico.getTipo());
		tipoComboBox.setValue(grupoEconomico.getTipo());
		// gerenteContaComboBox.setItems(grupoEconomico.getGerenteConta());
		gerenteContaComboBox.setValue(grupoEconomico.getGerenteConta());
		// paisComboBox.setItems("Brasil");
		paisComboBox.setValue("Brasil");

		// Preencher início do código da empresa
		String codigoInicial = grupoEconomico.getCodigo();
		codigoField.setValue(codigoInicial);

		gerenteContaComboBox.setValue(grupoEconomico.getGerenteConta());

		// Marcar relação no modelo
		((Empresa) model).setCodigoGrupoEconomico(grupoEconomico.getCodigo());
		model.setCodigoGrupoEconomico(grupoEconomico.getCodigo());
		model.setUnidOrigem(grupoEconomico.getUnid());
		model.setIdOrigem(grupoEconomico.getId());

		codigoField.setPlaceholder("Ex: " + grupoEconomico.getCodigo() + "SP");
	}

}
