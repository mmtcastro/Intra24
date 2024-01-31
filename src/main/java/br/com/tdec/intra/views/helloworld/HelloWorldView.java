package br.com.tdec.intra.views.helloworld;

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
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Data;
import lombok.EqualsAndHashCode;

@PageTitle("Hello World")
@Route(value = "hello", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
@Data
@EqualsAndHashCode(callSuper = false)
public class HelloWorldView extends HorizontalLayout {

	private static final long serialVersionUID = 1L;

	private TextField name;
	private Button sayHello;

	public HelloWorldView() {

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

		add(name, sayHello);
	}

}
