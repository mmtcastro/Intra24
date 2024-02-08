package br.com.tdec.intra.views.helloworld;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import br.com.tdec.intra.empresas.model.Empresa;
import br.com.tdec.intra.utils.UtilsLdap;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Getter;
import lombok.Setter;

@PageTitle("Hello World")
@Route(value = "hello", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
@Getter
@Setter
public class HelloWorldView extends HorizontalLayout {

	private static final long serialVersionUID = 1L;

	private TextField name;
	private Button sayHello;
	private Button ldap = new Button("LDAP Search");
	private TextField ldapUser = new TextField("Usuário");
	private Button gruposLdap = new Button("Grupos LDAP");
	private TextField roleField = new TextField("Role");
	private Button roleButton = new Button("Set Role");

	private LdapContextSource contextSource;

	public HelloWorldView(LdapContextSource contextSource) {
		this.contextSource = contextSource;

		name = new TextField("Your name");
		sayHello = new Button("Diga Olá");

		sayHello.addClickListener(e -> {
			Notification.show("Hello " + name.getValue());
		});

		Empresa empresa = new Empresa();
		empresa.setNome("TDEC");

		sayHello.addClickShortcut(Key.ENTER);

		setMargin(true);
		setVerticalComponentAlignment(Alignment.END, name, sayHello);

		Button button = new Button("nagevar para GruposEconomicos",
				event -> UI.getCurrent().navigate("gruposeconomicos"));
		add(button);

		roleButton.addClickListener(e -> {
			List<GrantedAuthority> authorities = fetchAdditionalAuthorities(name.getValue(), roleField.getValue());
			Notification.show("Authorities: " + authorities);
		});

		ldap.addClickListener(e -> {
			findGroupsForUser();
		});
		gruposLdap.addClickListener(e -> {
			findGroups();
		});

		add(name, sayHello, new HorizontalLayout(ldapUser, ldap, gruposLdap));

	}

	public void findGroupsForUser() {
		UtilsLdap utilsLdap = new UtilsLdap(contextSource);
		List<String> ret = utilsLdap.findGroupsForUser(ldapUser.getValue());
		System.out.println(ret);

	}

	public void findGroups() {
		UtilsLdap utilsLdap = new UtilsLdap(contextSource);
		Map<String, List<String>> ret = utilsLdap.findGroupsAndMembers();
		System.out.println(ret);

	}

	private List<GrantedAuthority> fetchAdditionalAuthorities(String username, String role) {
		// Fetch additional authorities for the user from an external source
		// This could be querying a database, LDAP, or another service
		// For demonstration purposes, we'll return a hardcoded list of authorities
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(role));
		return authorities;
	}

}
