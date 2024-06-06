package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.Flex;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

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
public class CargoView extends AbstractViewDoc {

	private static final long serialVersionUID = 1L;
	private final CargoService service;
	private String unid;
	private Cargo cargo;
	private FormLayout form = new FormLayout();
	private TextField idField = new TextField("Id");
	private TextField codigoField = new TextField("Código");
	private TextField descricaoField = new TextField("Descrição");
	private Binder<Cargo> binder = new Binder<>(Cargo.class, false);
	private Button saveButton = new Button("Salvar", e -> save());
	private Button deleteButton = new Button("Excluir", e -> delete());
	private Button cancelButton = new Button("Cancelar", e -> cancel());

	public CargoView(CargoService service) {
		this.service = service;
		addClassNames("abstract-view-doc.css", Width.FULL, Display.FLEX, Flex.AUTO, Margin.LARGE);
	}

	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		this.unid = parameter;
		findCargo(unid);
		binder.forField(codigoField).asRequired("Entre com um código").bind(Cargo::getCodigo, Cargo::setCodigo);
		binder.bind(idField, Cargo::getId, Cargo::setId);
		binder.bind(descricaoField, Cargo::getDescricao, Cargo::setDescricao);
		idField.setReadOnly(true);
		binder.readBean(cargo);
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

	private void findCargo(String unid) {
		this.cargo = service.findByUnid(unid);
	}

	private void save() {
		service.save(cargo);
	}

	private void delete() {
		service.delete(cargo);
	}

	private void cancel() {
		UI.getCurrent().getPage().getHistory().back();
	}

}
