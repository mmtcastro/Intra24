package br.com.tdec.intra.views.about;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

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

		checkAuthentication();

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

	public void checkAuthentication() {
		// Fetch additional authorities for the user from an external source
		List<GrantedAuthority> additionalAuthorities = fetchAdditionalAuthorities("Marcelo Castro");

		// Get the current authentication object
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// Update the user's authorities with the additional authorities
		List<GrantedAuthority> authorities = new ArrayList<>(authentication.getAuthorities());
		authorities.addAll(additionalAuthorities);

		// Create a new authentication object with updated authorities
		Authentication newAuthentication = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
				authentication.getCredentials(), authorities);

		// Set the new authentication object in the security context
		SecurityContextHolder.getContext().setAuthentication(newAuthentication);

		// Collection<? extends GrantedAuthority> authorities =
		// authentication.getAuthorities();

		System.out.println("User Authorities:");
		authorities.stream().map(GrantedAuthority::getAuthority).forEach(System.out::println);

	}

	private List<GrantedAuthority> fetchAdditionalAuthorities(String username) {
		// Fetch additional authorities for the user from an external source
		// This could be querying a database, LDAP, or another service
		// For demonstration purposes, we'll return a hardcoded list of authorities
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_VENDAS"));
		return authorities;
	}
}
