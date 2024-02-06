package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.empresas.model.GrupoEconomico;
import br.com.tdec.intra.empresas.services.GrupoEconomicoService;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Getter;
import lombok.Setter;

@PermitAll
@Getter
@Setter
@Route(value = "grupoeconomico", layout = MainLayout.class)
@PageTitle("Grupo Econômico")
public class GrupoEconomicoView extends VerticalLayout implements HasUrlParameter<String> {
	private static final long serialVersionUID = 1L;
	private GrupoEconomicoService service;
	private String unid;
	private GrupoEconomico grupoEconomico;
	private FormLayout form = new FormLayout();
	private TextField idField = new TextField("Id");
	private TextField codigoField = new TextField("Código");
	private Binder<GrupoEconomico> binder = new Binder<>(GrupoEconomico.class, false);

	public GrupoEconomicoView(GrupoEconomicoService service) {
		this.service = service;

	}

	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		this.unid = parameter;
		findGrupoEconomico(unid);
		binder.bind(codigoField, GrupoEconomico::getCodigo, GrupoEconomico::setCodigo);
		binder.bind(idField, GrupoEconomico::getId, GrupoEconomico::setId);
		binder.readBean(grupoEconomico);
		form.add(idField, codigoField);
		add(form);
	}

	private void findGrupoEconomico(String unid) {
		this.grupoEconomico = service.findByUnid(unid);

	}

}
