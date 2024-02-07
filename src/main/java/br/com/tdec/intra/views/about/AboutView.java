package br.com.tdec.intra.views.about;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

import br.com.tdec.intra.config.LdapConfig;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Getter;
import lombok.Setter;

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
@PermitAll
@Getter
@Setter
public class AboutView extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	private final br.com.tdec.intra.config.SecurityService securityService;
	private final LdapConfig ldapConfig;;

	public AboutView(br.com.tdec.intra.config.SecurityService securityService, LdapConfig ldapConfig) {
		this.securityService = securityService;
		this.ldapConfig = ldapConfig;
		setSpacing(false);

		Image img = new Image("images/empty-plant.png", "placeholder plant");
		img.setWidth("200px");
		add(img);

		H2 header = new H2("This place intentionally left empty");
		header.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);
		add(header);
		add(new Paragraph("It’s a place where you can grow your own UI 🤗"));

		setSizeFull();
		setJustifyContentMode(JustifyContentMode.CENTER);
		setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		getStyle().set("text-align", "center");

		String username = securityService.getAuthenticatedUser().getUsername();

		VaadinSession.getCurrent().setAttribute("grupos", ldapConfig.findGroupsForUser(username));

		H3 pwd = new H3("Grupos:  " + UI.getCurrent().getSession().getAttribute("grupos"));
		add(pwd);
		Button logout = new Button("Logout " + username, e -> securityService.logout());
		add(logout);
	}

	public br.com.tdec.intra.config.SecurityService getSecurityService() {
		return securityService;
	}

}
