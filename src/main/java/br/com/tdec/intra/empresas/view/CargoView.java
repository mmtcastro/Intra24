package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractViewDoc;
import br.com.tdec.intra.converters.StringToZonedDateTimeConverter;
import br.com.tdec.intra.empresas.model.Cargo;
import br.com.tdec.intra.empresas.services.CargoService;
import br.com.tdec.intra.empresas.validator.CargoValidator;
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
//	private final CargoService service;
//	private String unid;
//	private Cargo cargo;
	// private FormLayout form = new FormLayout();
	private TextField idField = new TextField("Id");
	private TextField autorField = new TextField("Autor");
	private TextField criacaoField = new TextField("Criação");
	private TextField codigoField = new TextField("Código");
	private TextField descricaoField = new TextField("Descrição");
//	private Binder<Cargo> binder = new Binder<>(Cargo.class, false);
//	private Button saveButton = new Button("Salvar", e -> save());
//	private Button deleteButton = new Button("Excluir", e -> delete());
//	private Button cancelButton = new Button("Cancelar", e -> cancel());

	public CargoView(CargoService service) {
		super(Cargo.class, service);
		this.service = service;
		// addClassNames("abstract-view-doc.css", Width.FULL, Display.FLEX, Flex.AUTO,
		// Margin.LARGE);
	}

	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		this.unid = parameter;
		if (parameter == null || parameter.isEmpty()) {
			isNovo = true;
			// model = createModel(Vertical.class); - ele já cria automaticamente para setar
			// o Binder em AbtractViewDoc
			model.init();
		} else {
			model = service.findByUnid(unid);
		}
		binder.forField(codigoField).asRequired("Entre com um código")//
				.withValidator(new CargoValidator.CodigoValidator(service))//
				.bind(Cargo::getCodigo, Cargo::setCodigo);
		if (!isNovo) {
			codigoField.setReadOnly(true);
		}
		binder.forField(descricaoField).asRequired("Entre com uma descrição").bind(Cargo::getDescricao,
				Cargo::setDescricao);
		binder.bind(idField, Cargo::getId, Cargo::setId);
		binder.bind(autorField, Cargo::getAutor, Cargo::setAutor);
		binder.forField(criacaoField).withConverter(new StringToZonedDateTimeConverter()).bind(Cargo::getCriacao,
				Cargo::setCriacao);

		// idField.setReadOnly(true);
		// criacaoField.setReadOnly(true);
		// autorField.setReadOnly(true);

		binder.readBean(model);
		// form.add(codigoField, descricaoField, idField, autorField, criacaoField);
		add(codigoField, descricaoField);

		// addButtons();
		initButtons();
		initFooter();
		// add(form);
	}

//	public void addButtons() {
//		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//		deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
//		cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//		add(new HorizontalLayout(saveButton, deleteButton, cancelButton));
//	}

	@Override
	protected void save() {
		// TODO Auto-generated method stub

	}

}
