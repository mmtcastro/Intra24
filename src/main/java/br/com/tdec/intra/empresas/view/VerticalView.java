package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractViewDoc;
import br.com.tdec.intra.converters.StringToZonedDateTimeConverter;
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
	private TextField codigoField = new TextField("Código");
	private TextField descricaoField = new TextField("Descrição");
	// private FormLayout form = new FormLayout();
	// private Button saveButton = new Button("Salvar", e -> save());
	// private Button deleteButton = new Button("Excluir", e -> delete());
	// private Button cancelButton = new Button("Cancelar", e -> cancel());

	public VerticalView(VerticalService service) {
		super(Vertical.class, service);
	}

	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		this.unid = parameter;
		if (parameter == null || parameter.isEmpty()) {
			isNovo = true;
			// model = createModel(Vertical.class); - ele já cria automaticamente para setar
			// o Binder em AbtractViewDoc
			model.init();

			System.out.println("VerticalView.setParameter() - New Vertical");
		} else {
			model = service.findByUnid(unid);
		}
		binder.forField(codigoField).asRequired("Entre com um código")//
				.withValidator(new VerticalValidator.CodigoValidator(service))//
				.bind(Vertical::getCodigo, Vertical::setCodigo);
		if (!isNovo) {
			codigoField.setReadOnly(true);
		}
		binder.forField(descricaoField).asRequired("Entre com uma descrição").bind(Vertical::getDescricao,
				Vertical::setDescricao);
		binder.bind(idField, Vertical::getId, Vertical::setId);
		binder.bind(autorField, Vertical::getAutor, Vertical::setAutor);

		binder.forField(criacaoField).withConverter(new StringToZonedDateTimeConverter()).bind(Vertical::getCriacao,
				Vertical::setCriacao);

		idField.setReadOnly(true);
		// criacaoField.setReadOnly(true);
		autorField.setReadOnly(true);

		binder.readBean(model);
		form.add(codigoField, descricaoField, idField, autorField, criacaoField);
		// addButtons();
		initButtons();
		add(form);
	}

	@Override
	protected void save() {
		// TODO Auto-generated method stub

	}

//	public void addButtons() {
//		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//		deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
//		cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//		form.add(new HorizontalLayout(saveButton, deleteButton, cancelButton));
//	}

//	private void findByUnid(String unid) {
//		try {
//			Class<?> clazz = Vertical.class;
//			// Ensure clazz is actually a subclass of AbstractModelDoc to safely cast
//			if (Vertical.class.isAssignableFrom(clazz)) {
//				Vertical model = (Vertical) clazz.getDeclaredConstructor().newInstance();
//				model = service.findByUnid(unid);
//				this.vertical = (Vertical) model;
//			} else {
//				throw new IllegalArgumentException("Class does not extend AbstractModelDoc");
//			}
//		} catch (InstantiationException e) {
//			// Handle the case where the class is abstract or an interface
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// Handle the case where the constructor is inaccessible
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			// Handle the case where no default constructor is available
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			// Handle constructor exceptions
//			e.printStackTrace();
//		}
//
//	}

//	private void save() {
//		binder.validate();
//
//		// Stream through the fields and perform some operation on each field
//		binder.getFields().forEach(field -> {
//			// Process each field here
//			System.out.println("Field Name: " + field.getValue());
//			System.out.println("Field Value: " + field.isEmpty());
//		});
//		SaveResponse response = service.save(vertical);
//	}

}
