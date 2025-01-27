package br.com.tdec.intra.empresas.view;

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
	private ComboBox<String> responsavelComboBox = new ComboBox<>("Responsável");

	public GrupoEconomicoView(ColaboradorService colaboradorService) {
		super();
		addClassNames("abstract-view-doc.css", Width.FULL, Display.FLEX, Flex.AUTO, Margin.LARGE);
		responsavelComboBox.setItems(colaboradorService.getFuncionariosAtivos());
		responsavelComboBox.setPlaceholder("Selecione um responsável");
		responsavelComboBox.setWidthFull();

		// add(responsavelComboBox); // Adiciona o ComboBox ao layout
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

		// Configura o binding para o ComboBox 'responsavelComboBox'
		binder.forField(responsavelComboBox).asRequired("Selecione um responsável").bind(GrupoEconomico::getResponsavel,
				GrupoEconomico::setResponsavel);

		binder.forField(descricaoField).asRequired("Entre com uma descrição").bind(GrupoEconomico::getDescricao,
				GrupoEconomico::setDescricao);

		binder.setBean(model);

		// Adiciona os campos ao binderFields
		binderFields.add(codigoField);
		binderFields.add(responsavelComboBox);
		binderFields.add(descricaoField);
	}

}
