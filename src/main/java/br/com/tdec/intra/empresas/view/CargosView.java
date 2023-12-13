package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractViewLista;
import br.com.tdec.intra.empresas.repositories.CargoRepository;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Route(value = "cargos", layout = MainLayout.class)
@PermitAll
@Data
@EqualsAndHashCode(callSuper = false)
public class CargosView extends AbstractViewLista {

	private static final long serialVersionUID = 1L;

	public CargosView(CargoRepository repository) {
		super(repository);
		Button sendMailButton = new Button("Send Mail", e -> sendMail());
		add(sendMailButton);
		initGridDefault();

	}

	public void sendMail() {
		// sendMail("mcastro@tdec.com.br", "mcastro@tdec.com.br", "Subject Teste", "Body
		// Teste");
		emailService.sendSimpleMessage("mcastro@tdec.com.br", "mcastro@tdec.com.br", "Subject Teste", "Body Teste");
	}

}
