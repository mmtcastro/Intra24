package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractValidator;
import br.com.tdec.intra.abs.AbstractViewDoc;
import br.com.tdec.intra.empresas.model.Vertical;
import br.com.tdec.intra.empresas.services.VerticalService;
import br.com.tdec.intra.utils.converters.RemoveSpacesConverter;
import br.com.tdec.intra.utils.converters.UpperCaseConverter;
import br.com.tdec.intra.utils.converters.ZonedDateTimeToLocalDateConverter;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PageTitle("Vertical")
@Route(value = "vertical", layout = MainLayout.class)
@RolesAllowed("ROLE_EVERYONE")
public class VerticalView extends AbstractViewDoc<Vertical> {

	private static final long serialVersionUID = 1L;
	private DatePicker dataField = new DatePicker("Data");
	private TextField codigoField = new TextField("Código");
	private TextField descricaoField = new TextField("Descrição");

	public VerticalView(VerticalService service) {
		super(Vertical.class, service);
		addClassNames("abstract-view-doc");
	}

	public void initBinder() {

		if (isNovo) {
			binder.forField(codigoField).asRequired("Entre com um código").withNullRepresentation("")
					.withConverter(new UpperCaseConverter()).withConverter(new RemoveSpacesConverter())
					.withValidator(new AbstractValidator.CodigoValidator<>(service))
					.bind(Vertical::getCodigo, Vertical::setCodigo);
		} else {
			binder.forField(codigoField).asRequired("Entre com um código").withNullRepresentation("")
					.withConverter(new UpperCaseConverter()).withConverter(new RemoveSpacesConverter())
					.bind(Vertical::getCodigo, Vertical::setCodigo);
			readOnlyFields.add(codigoField);
		}
		binder.forField(dataField)//
				.asRequired("Formato esperado: DD/MM/AAAA")//
				.withConverter(new ZonedDateTimeToLocalDateConverter())//
				.bind(Vertical::getData, Vertical::setData);
		System.out.println("Data no Binder: " + model.getData());
		binder.forField(descricaoField).asRequired("Entre com uma descrição").bind(Vertical::getDescricao,
				Vertical::setDescricao);

		binder.setBean(model);

		add(codigoField, dataField, descricaoField);

		dataField.addValueChangeListener(e -> Notification.show("Data: " + model.getData()));

	}

}
