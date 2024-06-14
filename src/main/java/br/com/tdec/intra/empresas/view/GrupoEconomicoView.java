package br.com.tdec.intra.empresas.view;

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
import br.com.tdec.intra.empresas.model.GrupoEconomico;
import br.com.tdec.intra.empresas.services.GrupoEconomicoService;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Route(value = "grupoeconomico", layout = MainLayout.class)
@PageTitle("Grupo Econômico")
@RolesAllowed("ROLE_EVERYONE")
public class GrupoEconomicoView extends AbstractViewDoc<GrupoEconomico> {
	private static final long serialVersionUID = 1L;
	// private final GrupoEconomicoService service;
	// private String unid;
	// private GrupoEconomico grupoEconomico;
	// private FormLayout form = new FormLayout();
	private TextField idField = new TextField("Id");
	private TextField codigoField = new TextField("Código");
	private Binder<GrupoEconomico> binder = new Binder<>(GrupoEconomico.class, false);

	public GrupoEconomicoView(GrupoEconomicoService service) {
		super(GrupoEconomico.class, service);
		addClassNames("abstract-view-doc.css", Width.FULL, Display.FLEX, Flex.AUTO, Margin.LARGE);

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
		binder.bind(codigoField, GrupoEconomico::getCodigo, GrupoEconomico::setCodigo);
		binder.bind(idField, GrupoEconomico::getId, GrupoEconomico::setId);
		binder.readBean(model);
		add(idField, codigoField);

	}

//	private void findGrupoEconomico(String unid) {
//		this.grupoEconomico = service.findByUnid(unid);
//
//	}

	@Override
	protected void save() {
		// TODO Auto-generated method stub

	}

}
