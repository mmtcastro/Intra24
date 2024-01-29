package br.com.tdec.intra.views;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;

import br.com.tdec.intra.empresas.view.CargosView;
import br.com.tdec.intra.empresas.view.EmpresasView;
import br.com.tdec.intra.empresas.view.GruposEconomicosView;
import br.com.tdec.intra.empresas.view.VerticaisView;
import br.com.tdec.intra.views.about.AboutView;
import br.com.tdec.intra.views.helloworld.HelloWorldView;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

	private static final long serialVersionUID = 1L;
	private H2 viewTitle;

	public MainLayout() {
		setPrimarySection(Section.DRAWER);
		addDrawerContent();
		addHeaderContent();
	}

	private void addHeaderContent() {
		DrawerToggle toggle = new DrawerToggle();
		toggle.setAriaLabel("Menu toggle");

		viewTitle = new H2();
		viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

		addToNavbar(true, toggle, viewTitle);
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
		nav.addItem(new SideNavItem("About", AboutView.class, LineAwesomeIcon.FILE.create()));
		nav.addItem(new SideNavItem("GruposEconômicos", GruposEconomicosView.class,
				LineAwesomeIcon.ACQUISITIONS_INCORPORATED.create()));
		nav.addItem(
				new SideNavItem("Empresas", EmpresasView.class, LineAwesomeIcon.MONEY_BILL_WAVE_ALT_SOLID.create()));
		nav.addItem(new SideNavItem("Verticais", VerticaisView.class, LineAwesomeIcon.BUILDING.create()));
		nav.addItem(new SideNavItem("Cargos", CargosView.class, LineAwesomeIcon.PEOPLE_CARRY_SOLID.create()));

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
