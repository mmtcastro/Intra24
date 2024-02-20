package br.com.tdec.intra.abs;

import java.lang.reflect.InvocationTargetException;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.vaadin.flow.component.UI;

import br.com.tdec.intra.directory.model.User;
import br.com.tdec.intra.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Service
public abstract class AbstractService {
	/* Durante o LoginView eu crio o webClient e o user e seto na sessao */
	protected WebClient webClient = (WebClient) UI.getCurrent().getSession().getAttribute("webClient");
	protected User user = (User) UI.getCurrent().getSession().getAttribute("user");
	protected final String scope = Utils.getScopeFromClass(this.getClass());

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
