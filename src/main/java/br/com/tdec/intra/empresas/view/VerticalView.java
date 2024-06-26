package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractService.DeleteResponse;
import br.com.tdec.intra.abs.AbstractService.SaveResponse;
import br.com.tdec.intra.abs.AbstractViewDoc;
import br.com.tdec.intra.converters.LocalDateToZonedDateTimeConverter;
import br.com.tdec.intra.converters.RemoveSpacesConverter;
import br.com.tdec.intra.converters.UpperCaseConverter;
import br.com.tdec.intra.empresas.model.Vertical;
import br.com.tdec.intra.empresas.services.VerticalService;
import br.com.tdec.intra.empresas.validator.VerticalValidator;
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

	// private final VerticalService service;
	// private String unid;
	// private Vertical vertical;
	// private Binder<Vertical> binder = new Binder<>(Vertical.class, false);
	private TextField idField = new TextField("Id");
	private TextField autorField = new TextField("Autor");
	private TextField criacaoField = new TextField("Criação");
	private DatePicker dataField = new DatePicker("Data");
	private TextField codigoField = new TextField("Código");
	private H3 tituloCodigo;
	private TextField descricaoField = new TextField("Descrição");
	// private FormLayout form = new FormLayout();
	// private Button saveButton = new Button("Salvar", e -> save());
	// private Button deleteButton = new Button("Excluir", e -> delete());
	// private Button cancelButton = new Button("Cancelar", e -> cancel());

	public VerticalView(VerticalService service) {
		super(Vertical.class, service);
		addClassNames("abstract-view-doc");
	}

	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		this.unid = parameter;
		if (parameter == null || parameter.isEmpty()) {
			isNovo = true;
			model = createModel();
			model.init();
			isReadOnly = false;
		} else {
			model = service.findByUnid(unid);
			isReadOnly = true;
		}

		binder.setReadOnly(isReadOnly);
		if (isNovo) {
			binder.forField(codigoField).asRequired("Entre com um código")//
					.withNullRepresentation("") // Handle null values no campo texto
					.withValidator(new VerticalValidator.CodigoValidator(service))//
					.withConverter(new UpperCaseConverter())//
					.withConverter(new RemoveSpacesConverter())//
					.bind(Vertical::getCodigo, Vertical::setCodigo);
//		if (!isNovo) {
//			codigoField.setReadOnly(true);
//		}
		} else {
			tituloCodigo = new H3("Vertical: " + model.getCodigo());
		}
		// dataField.setHelperText("Formato esperado: DD/MM/AAAA");
		binder.forField(dataField)//
				.asRequired("Formato esperado: DD/MM/AAAA")//
				.withConverter(new LocalDateToZonedDateTimeConverter())//
				.bind(Vertical::getData, Vertical::setData);
		binder.forField(descricaoField).asRequired("Entre com uma descrição").bind(Vertical::getDescricao,
				Vertical::setDescricao);

		binder.setBean(model);
		if (isNovo) {
			add(codigoField, dataField, descricaoField);
		} else {
			add(tituloCodigo, dataField, descricaoField);
		}
		H2 isNovo = new H2("IsNovo: " + this.isNovo);
		H2 isEditable = new H2("IsReadOnly: " + this.isReadOnly);
		add(isNovo, isEditable);
		if (model.getMeta() != null) {
			H2 meta = new H2(model.getMeta().getUnid());
			add(meta);
		}
		dataField.addValueChangeListener(e -> Notification.show("Data: " + model.getData()));
		initButtons();
		initFooter();
	}

	@Override
	protected SaveResponse update() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected DeleteResponse delete() {
		// TODO Auto-generated method stub
		return null;
	}

}
