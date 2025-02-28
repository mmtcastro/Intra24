package br.com.tdec.intra.empresas.view;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractViewDoc;
import br.com.tdec.intra.api.services.ApolloLinkedinService;
import br.com.tdec.intra.empresas.model.Contato;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Route(value = "contato", layout = MainLayout.class)
@PageTitle("Contato")
@RolesAllowed("ROLE_EVERYONE")
public class ContatoView extends AbstractViewDoc<Contato> {

	private static final long serialVersionUID = 1L;
	private TextField codigoField = new TextField("Código");
	private TextField nomeField = new TextField("Nome");
	private TextField emailField = new TextField("Email");
	private Button linkedinButton = new Button("LinkedIn");

	@Autowired
	private ApolloLinkedinService apolloLinkedinService;

	public ContatoView(ApolloLinkedinService apolloLinkedinService) {
		this.apolloLinkedinService = apolloLinkedinService;
		configureButton();
	}

	private void configureButton() {
		linkedinButton.addClickListener(event -> {
			String email = emailField.getValue();
			String firstName = model.getFirstName();
			String lastName = model.getLastName();

			System.out.println("🔍 Buscando LinkedIn para: " + email + ", " + firstName + " " + lastName);

			apolloLinkedinService.buscarApolloLinkedinModel(email, firstName, lastName).subscribe(model -> {
				System.out.println("🔗 LinkedIn Model: " + model);
			});
		});
	}

	@Override
	protected void initBinder() {

		binder.forField(codigoField).bind(Contato::getCodigo, Contato::setCodigo);
		binder.forField(nomeField).bind(Contato::getNome, Contato::setNome);
		binder.forField(emailField).bind(Contato::getCodigo, Contato::setCodigo);
		binderFields.add(codigoField);
		binderFields.add(nomeField);
		binderFields.add(emailField);
		binderFields.add(linkedinButton);

	}
}
