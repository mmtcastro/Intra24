package br.com.tdec.intra.views;

import java.io.Serial;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;

import br.com.tdec.intra.empresas.view.CargosView;
import br.com.tdec.intra.empresas.view.ContatosView;
import br.com.tdec.intra.empresas.view.EmpresasView;
import br.com.tdec.intra.empresas.view.GruposEconomicosView;
import br.com.tdec.intra.empresas.view.OrigensClientesView;
import br.com.tdec.intra.empresas.view.TiposEmpresasView;
import br.com.tdec.intra.empresas.view.TiposExcecoesTributarias;
import br.com.tdec.intra.empresas.view.VerticaisView;
import br.com.tdec.intra.inter.view.HasTopActions;
import br.com.tdec.intra.utils.UtilsSession;
import br.com.tdec.intra.views.about.AboutView;
import br.com.tdec.intra.views.helloworld.HelloWorldView;
import lombok.Getter;
import lombok.Setter;

/**
 * The main view is a top-level placeholder for other views.
 */
@Getter
@Setter
@CssImport("./themes/intra24/main-layout.css")
public class MainLayout extends AppLayout {

	@Serial
	private static final long serialVersionUID = 1L;
	private H2 viewTitle;
	private final br.com.tdec.intra.config.SecurityService securityService;
	private final HorizontalLayout topActionBar = new HorizontalLayout();

	public MainLayout(br.com.tdec.intra.config.SecurityService securityService) {
		this.securityService = securityService;

		setPrimarySection(Section.DRAWER); // 🔹 Força o menu a ficar no topo sempre
		addDrawerContent();
		addHeaderContent();
	}

	private void addHeaderContent() {
		DrawerToggle toggle = new DrawerToggle();
		toggle.setAriaLabel("Menu toggle");

		viewTitle = new H2();
		viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
		viewTitle.getStyle().set("white-space", "nowrap");

		// Placeholder para menu contextual (topActionBar será preenchido depois)
		topActionBar.setSpacing(true);
		topActionBar.setAlignItems(Alignment.CENTER);

		// Logout
		String username = UtilsSession.getCurrentUserName();
		Anchor logout = new Anchor("logout", "Logout " + username);
		logout.getElement().addEventListener("click", event -> securityService.logout());

		// Espaçador flexível
		Div spacer = new Div();
		spacer.getStyle().set("flex-grow", "1");

		// Layout principal da barra superior
		HorizontalLayout headerLayout = new HorizontalLayout(toggle, viewTitle, topActionBar, spacer, logout);
		headerLayout.setWidthFull();
		headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
		headerLayout.setPadding(true);
		headerLayout.setSpacing(true);

		addToNavbar(true, headerLayout);

	}

	private void addDrawerContent() {
		H1 appName = new H1("Intra");
		appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
		Header header = new Header(appName);

		Scroller scroller = new Scroller(createNavigation());

		addToDrawer(header, scroller, createFooter());
	}

	private SideNav createNavigation() {
		SideNav nav = new SideNav();

		nav.addItem(new SideNavItem("Hello World", HelloWorldView.class, LineAwesomeIcon.GLOBE_SOLID.create()));
		nav.addItem(new SideNavItem("About", AboutView.class, LineAwesomeIcon.INFO_CIRCLE_SOLID.create()));

		SideNavItem empresas = new SideNavItem("Empresas", GruposEconomicosView.class,
				LineAwesomeIcon.HANDSHAKE_SOLID.create());
		empresas.addItem(new SideNavItem("Grupos Econômicos", GruposEconomicosView.class,
				LineAwesomeIcon.SITEMAP_SOLID.create()));
		empresas.addItem(new SideNavItem("Empresas", EmpresasView.class, LineAwesomeIcon.BUILDING_SOLID.create()));
		empresas.addItem(new SideNavItem("Contatos", ContatosView.class, LineAwesomeIcon.USERS_SOLID.create()));
		empresas.addItem(new SideNavItem("Verticais", VerticaisView.class, LineAwesomeIcon.LAYER_GROUP_SOLID.create()));
		empresas.addItem(new SideNavItem("Cargos", CargosView.class, LineAwesomeIcon.USER.create()));

		// Submenu Configurações Empresas
		SideNavItem empresas_config = new SideNavItem("Configurações", OrigensClientesView.class,
				LineAwesomeIcon.COG_SOLID.create());
		empresas_config.setExpanded(false);
		SideNavItem tiposEmpresas = new SideNavItem("Tipos de Empresas", TiposEmpresasView.class,
				LineAwesomeIcon.INDUSTRY_SOLID.create());
		SideNavItem origensCliente = new SideNavItem("Origens Cliente", OrigensClientesView.class,
				LineAwesomeIcon.USER_PLUS_SOLID.create());
		SideNavItem tiposExecaoTrib = new SideNavItem("Tipos Excecao Trib", TiposExcecoesTributarias.class,
				LineAwesomeIcon.USER_PLUS_SOLID.create());
		empresas_config.addItem(origensCliente, tiposEmpresas, tiposExecaoTrib);
		empresas.addItem(empresas_config);

		nav.addItem(empresas);
		return nav;
	}

	private Footer createFooter() {
		Footer layout = new Footer();

		return layout;
	}

	@Override
	protected void afterNavigation() {
		super.afterNavigation();
		viewTitle.setText(getCurrentPageTitle());

		// Limpa ações anteriores
		clearTopActions();

		// Verifica se a view implementa HasTopActions
		Component content = getContent();
		if (content instanceof HasTopActions topActionView) {
			Component topActions = topActionView.getTopActions();
			if (topActions != null) {
				showTopActions(topActions);
			}
		}

	}

	private void showTopActions(Component actions) {
		topActionBar.removeAll();
		topActionBar.add(actions);
	}

	private void clearTopActions() {
		topActionBar.removeAll();
	}

	private String getCurrentPageTitle() {
		PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
		return title == null ? "" : title.value();
	}

}
