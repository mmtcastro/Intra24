package br.com.tdec.intra.views.helloworld;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import br.com.tdec.intra.config.DominoServer;
import br.com.tdec.intra.empresas.model.Empresa;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;

@PageTitle("Hello World")
@Route(value = "hello", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class HelloWorldView extends HorizontalLayout {

	private static final long serialVersionUID = 1L;
	private final DominoServer dominoServer;
	private TextField name;
	private Button sayHello;

	public HelloWorldView(DominoServer dominoServer) {
		this.dominoServer = dominoServer;
		name = new TextField("Your name");
		sayHello = new Button("Diga Olá");
		sayHello.addClickListener(e -> {
			Notification.show("Hello " + name.getValue());
		});

		Empresa empresa = new Empresa();
		empresa.setNome("TDEC");

		sayHello.addClickShortcut(Key.ENTER);
		Text serverName = new Text(dominoServer.getHostName());

		setMargin(true);
		setVerticalComponentAlignment(Alignment.END, name, sayHello);
		

		add(name, sayHello, serverName);
	}

	public DominoServer getDominoServer() {
		return dominoServer;
	}

}
