package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractViewDoc;
import br.com.tdec.intra.empresas.model.TipoExcecaoTributaria;
import br.com.tdec.intra.empresas.services.TipoExcecaoTributariaService;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Route(value = "tipoexcecaotributaria", layout = MainLayout.class)
@PageTitle("Tipo Exceção Tributária")
@RolesAllowed("ROLE_EVERYONE")
public class TipoExcecaoTributariaView extends AbstractViewDoc<TipoExcecaoTributaria> {

	private static final long serialVersionUID = 1L;
	private final TipoExcecaoTributariaService service;

	public TipoExcecaoTributariaView(TipoExcecaoTributariaService service) {
		this.service = service;

	}

	@Override
	protected void initBinder() {
		TextField firstName = new TextField("First name");
		TextField lastName = new TextField("Last name");
		EmailField email = new EmailField("Email address");
		PasswordField password = new PasswordField("Password");
		PasswordField confirmPassword = new PasswordField("Confirm password");
		TextField rua = new TextField("Rua");
		TextField bairro = new TextField("Bairro");
		TextField cidade = new TextField("Cidade");
		TextField estado = new TextField("Estado");
		TextField cep = new TextField("Cep");
		TextField pais = new TextField("País");
		form.add(firstName, lastName, email, password, confirmPassword, rua, bairro, cidade, estado, cep, pais);

	}

	// Additional methods to handle user interactions, data binding, etc.

}
