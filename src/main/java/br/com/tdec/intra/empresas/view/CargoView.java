package br.com.tdec.intra.empresas.view;

import java.io.Serial;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractValidator;
import br.com.tdec.intra.abs.AbstractViewDoc;
import br.com.tdec.intra.empresas.model.Cargo;
import br.com.tdec.intra.utils.converters.ProperCaseConverter;
import br.com.tdec.intra.utils.converters.RemoveSimbolosEAcentos;
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

	@Serial
	private static final long serialVersionUID = 1L;
	private TextField codigoField = new TextField("Código");
	private TextField descricaoField = new TextField("Descrição");

	public CargoView() {
		super();
		// addClassNames("abstract-view-doc.css", Width.FULL, Display.FLEX, Flex.AUTO,
		// Margin.LARGE);
	}

	@Override
	protected void initBinder() {
		if (isNovo) {
			binder.forField(codigoField).asRequired("Entre com um código")//
					.withNullRepresentation("")//
					.withConverter(new ProperCaseConverter())//
					.withValidator(new AbstractValidator.CodigoValidator<>(service))//
					.withConverter(new ProperCaseConverter())//
					.withConverter(new RemoveSimbolosEAcentos())//
					.bind(Cargo::getCodigo, Cargo::setCodigo);
		} else {
			binder.forField(codigoField).asRequired("Entre com um código").withNullRepresentation("")
					.bind(Cargo::getCodigo, Cargo::setCodigo);
			readOnlyFields.add(codigoField);
		}
		binder.forField(descricaoField).asRequired("Entre com uma descrição").bind(Cargo::getDescricao,
				Cargo::setDescricao);
		// binder.readBean(model);
		binderFields.add(codigoField);
		binderFields.add(descricaoField);

	}

}
