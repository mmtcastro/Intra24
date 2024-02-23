package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractViewDoc;
import br.com.tdec.intra.empresas.model.Empresa;
import br.com.tdec.intra.empresas.services.EmpresaService;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;

@PageTitle("Empresa")
@Route(value = "empresa", layout = MainLayout.class)
@Getter
@Setter
@RolesAllowed("ROLE_EVERYONE")
public class EmpresaView extends AbstractViewDoc {

	private static final long serialVersionUID = 1L;
	private final EmpresaService service;
	private String unid;
	private Empresa empresa;
	private FormLayout form = new FormLayout();
	private TextField idField = new TextField("Id");
	private TextField codigoField = new TextField("Código");
	private TextField nomeField = new TextField("Nome");
	private Binder<Empresa> binder = new Binder<>(Empresa.class, false);
//	private Button saveButton = new Button("Salvar", e -> save());
//	private Button deleteButton = new Button("Excluir", e -> delete());
//	private Button cancelButton = new Button("Cancelar", e -> cancel());

	public EmpresaView(EmpresaService service) {
		this.service = service;
		// super();
	}

	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		this.unid = parameter;
		empresa = service.findByUnid(unid);
		binder.forField(codigoField).asRequired("Entre com um código").bind(Empresa::getCodigo, Empresa::setCodigo);
		binder.bind(idField, Empresa::getId, Empresa::setId);
		binder.bind(nomeField, Empresa::getNome, Empresa::setNome);
		idField.setReadOnly(true);
		binder.readBean(empresa);
		form.add(codigoField, nomeField, idField);
		// addButtons();
		add(form);
	}

}
