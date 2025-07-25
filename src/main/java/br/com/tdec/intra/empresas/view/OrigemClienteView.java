package br.com.tdec.intra.empresas.view;

import java.io.Serial;
import java.time.LocalDate;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractValidator;
import br.com.tdec.intra.abs.AbstractViewDoc;
import br.com.tdec.intra.empresas.model.OrigemCliente;
import br.com.tdec.intra.utils.converters.ProperCaseConverter;
import br.com.tdec.intra.utils.converters.RemoveSimbolosEAcentos;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PageTitle("Origem do Cliente")
@Route(value = "origemcliente", layout = MainLayout.class)
@RolesAllowed("ROLE_EVERYONE")
public class OrigemClienteView extends AbstractViewDoc<OrigemCliente> {

    /**
     * 
     */
    @Serial
    private static final long serialVersionUID = 1L;
	private TextField codigoField = new TextField("Código");
	private TextField descricaoField = new TextField("Descrição");

	public OrigemClienteView() {
		super();
		showUploads = false;
	}

	public void initBinder() {

		if (isNovo) {
			model.setData(LocalDate.now());
			binder.forField(codigoField).asRequired("Entre com um código")//
					.withNullRepresentation("")//
					.withConverter(new ProperCaseConverter())//
					.withConverter(new RemoveSimbolosEAcentos())
					.withValidator(new AbstractValidator.CodigoValidator<>(service))
					.bind(OrigemCliente::getCodigo, OrigemCliente::setCodigo);
		} else {
			binder.forField(codigoField).asRequired("Entre com um código").withNullRepresentation("")
					.bind(OrigemCliente::getCodigo, OrigemCliente::setCodigo);
			readOnlyFields.add(codigoField);
		}

		binder.forField(descricaoField).asRequired("Entre com uma descrição").bind(OrigemCliente::getDescricao,
				OrigemCliente::setDescricao);

		binder.setBean(model);

		// Adicionar o campo ao binderFields para controle de readOnly
		binderFields.add(codigoField);
		binderFields.add(descricaoField);

	}

}
