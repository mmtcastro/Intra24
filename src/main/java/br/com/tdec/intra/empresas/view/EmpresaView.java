package br.com.tdec.intra.empresas.view;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.combobox.ComboBox;
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
import br.com.tdec.intra.empresas.model.Empresa;
import br.com.tdec.intra.empresas.model.GrupoEconomico;
import br.com.tdec.intra.empresas.services.GrupoEconomicoService;
import br.com.tdec.intra.utils.converters.RemoveSpacesConverter;
import br.com.tdec.intra.utils.converters.UpperCaseConverter;
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
	private ComboBox<String> codigoGrupoEconomicoField = new ComboBox<>("Grupo Econômico");
	private TextField codigoField = new TextField("Código");
	private TextField nomeField = new TextField("Razão Social");
	private TextField gerenteContaField = new TextField("Gerente de Contas");
	private TextField estadoField = new TextField("Estado");
	private TextField cgcField = new TextField("CNPJ");
	private TextField telefoneField = new TextField("Telefone");
	private TextField emailField = new TextField("Email");
	private TextField enderecoField = new TextField("Endereço");
	private TextField cidadeField = new TextField("Cidade");
	private TextField paisField = new TextField("País");
	private TextField cepField = new TextField("CEP");
	private TextField statusCnpjField = new TextField("Status CNPJ");
	private TextField bancoField = new TextField("Banco");
	private TextField agenciaField = new TextField("Agência");
	private TextField contaField = new TextField("Conta");

	@Autowired
	private GrupoEconomicoService grupoEconomicoService;

	public EmpresaView(GrupoEconomicoService grupoEconomicoService) {
		super();

		addClassNames("abstract-view-doc.css", Width.FULL, Display.FLEX, Flex.AUTO, Margin.LARGE);

		configureGrupoEconomicoField();
	}

	private void configureGrupoEconomicoField() {
		// Define a ordenação ASC para a coluna "Codigo"
		List<QuerySortOrder> sortOrders = Collections
				.singletonList(new QuerySortOrder("Codigo", SortDirection.ASCENDING));
		// Configura ComboBox com Lazy Loading
		codigoGrupoEconomicoField.setItems(query -> grupoEconomicoService.findAllByCodigo(query.getOffset(), //
				query.getLimit(), //
				sortOrders, //
				query.getFilter().orElse(""), GrupoEconomico.class //
		).stream().map(GrupoEconomico::getCodigo));

		codigoGrupoEconomicoField.setPlaceholder("Selecione um Grupo Econômico");
		codigoGrupoEconomicoField.setWidthFull();

		// Copia o grupo econômico para o campo "código" automaticamente quando for novo
		codigoGrupoEconomicoField.addValueChangeListener(event -> {
			if (isNovo && event.getValue() != null) {
				codigoField.setValue(event.getValue() + "..."); // Adiciona "..." para indicar que o código será
																// copiado");
			}
		});
	}

	protected void initBinder() {
		if (isNovo) {
			binder.forField(codigoGrupoEconomicoField).asRequired("Selecione um Grupo Econômico")
					.bind(Empresa::getCodigoGrupoEconomico, Empresa::setCodigoGrupoEconomico);
			binder.forField(codigoField).asRequired("Entre com um código").withNullRepresentation("")
					.withConverter(new UpperCaseConverter()).withConverter(new RemoveSpacesConverter())
					.withValidator(new AbstractValidator.CodigoValidator<>(service))
					.bind(Empresa::getCodigo, Empresa::setCodigo);
		} else {
			binder.forField(codigoField).asRequired("Entre com um código").withNullRepresentation("")
					.withConverter(new UpperCaseConverter()).withConverter(new RemoveSpacesConverter())
					.bind(Empresa::getCodigo, Empresa::setCodigo);
			readOnlyFields.add(codigoField);
		}

		binder.forField(nomeField).asRequired("Informe o nome do cliente").bind(Empresa::getCliente,
				Empresa::setCliente);
		binder.forField(gerenteContaField).bind(Empresa::getGerenteConta, Empresa::setGerenteConta);
		binder.forField(estadoField).bind(Empresa::getEstado, Empresa::setEstado);
		binder.forField(cgcField).bind(Empresa::getCgc, Empresa::setCgc);
		binder.forField(telefoneField).bind(Empresa::getTelefones, Empresa::setTelefones);
		binder.forField(emailField).bind(Empresa::getEmailNfeServicos, Empresa::setEmailNfeServicos);
		binder.forField(enderecoField).bind(Empresa::getEndereco, Empresa::setEndereco);
		binder.forField(cidadeField).bind(Empresa::getCidade, Empresa::setCidade);
		binder.forField(paisField).bind(Empresa::getPais, Empresa::setPais);
		binder.forField(cepField).bind(Empresa::getCep, Empresa::setCep);
		binder.forField(statusCnpjField).bind(Empresa::getStatusCnpj, Empresa::setStatusCnpj);
		binder.forField(bancoField).bind(Empresa::getBanco, Empresa::setBanco);
		binder.forField(agenciaField).bind(Empresa::getAgencia, Empresa::setAgencia);
		binder.forField(contaField).bind(Empresa::getConta, Empresa::setConta);

		binder.setBean(model);

		binderFields.clear();
		binderFields.add(codigoGrupoEconomicoField);
		binderFields.add(codigoField);
		binderFields.add(nomeField);
		binderFields.add(gerenteContaField);
		binderFields.add(estadoField);
		binderFields.add(cgcField);
		binderFields.add(telefoneField);
		binderFields.add(emailField);
		binderFields.add(enderecoField);
		binderFields.add(cidadeField);
		binderFields.add(paisField);
		binderFields.add(cepField);
		binderFields.add(statusCnpjField);
		binderFields.add(bancoField);
		binderFields.add(agenciaField);
		binderFields.add(contaField);
	}
}
