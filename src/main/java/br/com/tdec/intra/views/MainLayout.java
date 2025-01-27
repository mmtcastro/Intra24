package br.com.tdec.intra.views;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;

import br.com.tdec.intra.empresas.view.CargosView;
import br.com.tdec.intra.empresas.view.EmpresasView;
import br.com.tdec.intra.empresas.view.GruposEconomicosView;
import br.com.tdec.intra.empresas.view.OrigensClientesView;
import br.com.tdec.intra.empresas.view.TiposEmpresasView;
import br.com.tdec.intra.empresas.view.VerticaisView;
import br.com.tdec.intra.utils.UtilsSession;
import br.com.tdec.intra.views.about.AboutView;
import br.com.tdec.intra.views.helloworld.HelloWorldView;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

	private static final long serialVersionUID = 1L;
	private H2 viewTitle;
	private final br.com.tdec.intra.config.SecurityService securityService;

	public MainLayout(br.com.tdec.intra.config.SecurityService securityService) {
		this.securityService = securityService;
		setPrimarySection(Section.DRAWER);
		addDrawerContent();
		addHeaderContent();
	}

	private void addHeaderContent() {
		DrawerToggle toggle = new DrawerToggle();
		toggle.setAriaLabel("Menu toggle");

		viewTitle = new H2();
		viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
		viewTitle.getStyle().set("white-space", "nowrap"); // Evitar quebra de linha

		addToNavbar(true, toggle, viewTitle);

		// String username = securityService.getAuthenticatedUser().getUsername();
		String username = UtilsSession.getCurrentUserName();

		// Button logout = new Button("Log out " + username, e ->
		// securityService.logout());
		Anchor logout = new Anchor("logout", "Log out");
		logout.addClassNames(LumoUtility.Margin.Left.AUTO);

		logout.getElement().addEventListener("click", event -> securityService.logout());

		var header = new HorizontalLayout(logout);

		header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

		header.setWidthFull();
		header.addClassNames(LumoUtility.Padding.Vertical.NONE, LumoUtility.Padding.Horizontal.MEDIUM);

		addToNavbar(header);

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
		empresas_config.addItem(origensCliente, tiposEmpresas);
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
	}

	private String getCurrentPageTitle() {
		PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
		return title == null ? "" : title.value();
	}
}
