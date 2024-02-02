package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
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
public class GrupoEconomicoView extends FormLayout implements HasUrlParameter<String> {
	private static final long serialVersionUID = 1L;
	private GrupoEconomicoService service;
	private String unid;
	private GrupoEconomico grupoEconomico;
	private TextField idField = new TextField("Id");
	private TextField codigoField = new TextField("Código");
	private Binder<GrupoEconomico> binder = new Binder<>(GrupoEconomico.class, false);

	public GrupoEconomicoView(GrupoEconomicoService service) {
		this.service = service;

	}

	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		this.unid = parameter;
		findGrupoEconomico(unid);
		H1 title = new H1("Grupo Econômico " + grupoEconomico.getCodigo());
		add(title);
	}

	public void findGrupoEconomico(String unid) {
		this.grupoEconomico = service.findByUnid(unid);
		System.out.println(grupoEconomico.getCodigo());
		binder.forField(idField).withValidator(new StringLengthValidator("Name must be at least 3 characters", 3, null))
				.bind(GrupoEconomico::getId, GrupoEconomico::setId);
		// binder.forField(codigoField).bind("codigo");
		add(idField, codigoField);
	}

}
