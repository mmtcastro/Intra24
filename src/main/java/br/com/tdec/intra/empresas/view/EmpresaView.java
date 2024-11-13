package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.Flex;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

import br.com.tdec.intra.abs.AbstractValidator;
import br.com.tdec.intra.abs.AbstractViewDoc;
import br.com.tdec.intra.empresas.model.Empresa;
import br.com.tdec.intra.empresas.services.EmpresaService;
import br.com.tdec.intra.utils.converters.RemoveSpacesConverter;
import br.com.tdec.intra.utils.converters.UpperCaseConverter;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;

@PageTitle("Empresa")
@Route(value = "empresa", layout = MainLayout.class)
@Getter
@Setter
@RolesAllowed("ROLE_EVERYONE")
public class EmpresaView extends AbstractViewDoc<Empresa> {

	private static final long serialVersionUID = 1L;
	private TextField codigoField = new TextField("Código");
	private TextField nomeField = new TextField("Nome");
	private TextField descricaoField = new TextField("Descrição");
	private Binder<Empresa> binder = new Binder<>(Empresa.class, false);

	public EmpresaView(EmpresaService service) {
		super(Empresa.class, service);
		addClassNames("abstract-view-doc.css", Width.FULL, Display.FLEX, Flex.AUTO, Margin.LARGE);
	}

	@Override
	protected void initBinder() {
		if (isNovo) {
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
		binder.forField(descricaoField).asRequired("Entre com uma descrição").bind(Empresa::getDescricao,
				Empresa::setDescricao);
		System.out.println("Data no Binder: " + model.getData());
		binder.forField(descricaoField).asRequired("Entre com uma descrição").bind(Empresa::getDescricao,
				Empresa::setDescricao);

		binder.setBean(model);

		add(codigoField, nomeField, descricaoField);

	}

	@Override
	protected void addCustomComponents() {
		// TODO Auto-generated method stub

	}

}
