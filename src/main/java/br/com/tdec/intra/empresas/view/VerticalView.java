package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.empresas.model.Vertical;
import br.com.tdec.intra.empresas.services.VerticalService;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PageTitle("Vertical")
@Route(value = "vertical", layout = MainLayout.class)
@PermitAll
public class VerticalView extends VerticalLayout implements HasUrlParameter<String> {

	private static final long serialVersionUID = 1L;

	private VerticalService service;
	private String unid;
	private Vertical vertical;
	private Binder<Vertical> binder = new Binder<>(Vertical.class, false);
	private TextField idField = new TextField("Id");
	private TextField codigoField = new TextField("Código");
	private TextField descricaoField = new TextField("Descrição");
	private FormLayout form = new FormLayout();
	private Button saveButton = new Button("Salvar", e -> save());
	private Button deleteButton = new Button("Excluir", e -> delete());
	private Button cancelButton = new Button("Cancelar", e -> cancel());

	public VerticalView(VerticalService service) {
		this.service = service;
	}

	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		this.unid = parameter;
		if (parameter == null || parameter.isEmpty()) {
			this.vertical = new Vertical();
			System.out.println("VerticalView.setParameter() - New Vertical");
		} else {
			findVertical(unid);
		}
		binder.forField(codigoField).asRequired("Entre com um código").bind(Vertical::getCodigo, Vertical::setCodigo);

		binder.bind(idField, Vertical::getId, Vertical::setId);
		binder.bind(descricaoField, Vertical::getDescricao, Vertical::setDescricao);
		idField.setReadOnly(true);
		binder.readBean(vertical);
		form.add(codigoField, descricaoField, idField);
		addButtons();
		add(form);
	}

	public void addButtons() {
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		form.add(new HorizontalLayout(saveButton, deleteButton, cancelButton));
	}

	private void findVertical(String unid) {
		this.vertical = service.findByUnid(unid);
	}

	private void save() {
		binder.validate();

		// Stream through the fields and perform some operation on each field
		binder.getFields().forEach(field -> {
			// Process each field here
			System.out.println("Field Name: " + field.getValue());
			System.out.println("Field Value: " + field.isEmpty());
		});
		service.save(vertical);
	}

	private void update() {
		service.update(vertical);
	}

	private void delete() {
		service.delete(unid);
	}

	private void cancel() {
		UI.getCurrent().getPage().getHistory().back();
	}

}
