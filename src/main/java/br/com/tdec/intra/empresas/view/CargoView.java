package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractValidator;
import br.com.tdec.intra.abs.AbstractViewDoc;
import br.com.tdec.intra.empresas.model.Cargo;
import br.com.tdec.intra.empresas.services.CargoService;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Route(value = "cargo", layout = MainLayout.class)
@PageTitle("Cargo")
@RolesAllowed("ROLE_EVERYONE")
public class CargoView extends AbstractViewDoc<Cargo> {

	private static final long serialVersionUID = 1L;
	private TextField codigoField = new TextField("Código");
	private TextField descricaoField = new TextField("Descrição");

	public CargoView(CargoService service) {
		super(Cargo.class, service);
		this.service = service;
		// addClassNames("abstract-view-doc.css", Width.FULL, Display.FLEX, Flex.AUTO,
		// Margin.LARGE);
	}

	@Override
	protected void initBinder() {

		binder.forField(codigoField).asRequired("Entre com um código")//
				.withValidator(new AbstractValidator.CodigoValidator<>(service))//
				.bind(Cargo::getCodigo, Cargo::setCodigo);
		if (!isNovo) {
			readOnlyFields.add(codigoField);
		}
		binder.forField(descricaoField).asRequired("Entre com uma descrição").bind(Cargo::getDescricao,
				Cargo::setDescricao);
		binder.readBean(model);
		// form.add(codigoField, descricaoField, idField, autorField, criacaoField);
		add(codigoField, descricaoField);

	}

}
