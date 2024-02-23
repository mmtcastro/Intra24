package br.com.tdec.intra.abs;

import java.lang.reflect.InvocationTargetException;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.vaadin.flow.component.UI;

import br.com.tdec.intra.config.WebClientService;
import br.com.tdec.intra.directory.model.User;
import br.com.tdec.intra.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Service
public abstract class AbstractService {

	protected final WebClientService webClientService;
	protected final WebClient webClient;
	// protected User user = (User)
	// UI.getCurrent().getSession().getAttribute("user");
	protected final String scope = Utils.getScopeFromClass(this.getClass());

	public AbstractService(WebClientService webClientService) {
		this.webClientService = webClientService;
		this.webClient = webClientService.getWebClient();
	}

	protected User getUser() {
		User user = null;
		try {
			user = (User) UI.getCurrent().getSession().getAttribute("user");
			return user;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	/* Durante o LoginView eu crio o webClient e o user e seto na sessao */
	// protected WebClient webClient = (WebClient)
	// UI.getCurrent().getSession().getAttribute("webClient");

	/** Nao é utilizado no momento */
	public AbstractModelDoc createNewModel() {
		Class<?> modelClass = Utils.getModelClassFromServiceClass(this.getClass());
		AbstractModelDoc model = null;
		try {
			model = (AbstractModelDoc) modelClass.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return model;

	}

}
