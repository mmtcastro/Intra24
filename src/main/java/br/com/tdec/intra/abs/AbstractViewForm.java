package br.com.tdec.intra.abs;

import java.lang.reflect.InvocationTargetException;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

import br.com.tdec.intra.utils.Utils;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractViewForm extends FormLayout implements BeforeEnterObserver {

	private static final long serialVersionUID = 1L;
	protected AbstractModelDoc model;
	protected Map queryParams;
	private H1 title;
	private Button save;
	private Button cancel;
	private Button delete;
	private Button edit;
	private HorizontalLayout buttons;

	public AbstractViewForm() {

		initModel();
		title = new H1(this.model.getClass().getSimpleName());
		this.setTitle(title);
		initButtons();
	}

	public void beforeEnter(BeforeEnterEvent event) {
		queryParams = (Map) event.getLocation().getQueryParameters().getParameters();
		System.out.println("queryParams: " + queryParams);
		// List<String> ids = queryParams.get("id");

//		if (ids != null && !ids.isEmpty()) {
//			infoLabel.setText("ID: " + ids.get(0));
//		} else {
//			infoLabel.setText("No ID provided");
//		}
	}

	public void initButtons() {
		save = new Button("Salvar");
		cancel = new Button("Cancelar");
		delete = new Button("Excluir");
		edit = new Button("Editar");
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
		edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		cancel.addClickShortcut(Key.ESCAPE);
		save.addClickShortcut(Key.ENTER);
		buttons = new HorizontalLayout(save, cancel, delete, edit);
		add(buttons);
		save.addClickListener(e -> save());
		cancel.addClickListener(e -> cancel());
		delete.addClickListener(e -> delete());
		edit.addClickListener(e -> edit());
	}

	private Object edit() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object delete() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object cancel() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object save() {
		// TODO Auto-generated method stub
		return null;
	}

	public void initModel() {
		Class<?> clazz = Utils.getClassModelFromViewClass(this.getClass());
		try {
			model = (AbstractModelDoc) clazz.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

}
