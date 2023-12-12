package br.com.tdec.intra.abs;

import java.lang.reflect.InvocationTargetException;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import br.com.tdec.intra.utils.Utils;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractViewForm extends FormLayout {

	private static final long serialVersionUID = 1L;
	protected AbstractModelDoc model;
	private H1 title;
	private Button save;
	private Button cancel;
	private Button delete;
	private Button edit;
	private Button newRecord;
	private HorizontalLayout buttons;

	public AbstractViewForm() {

		initModel();
		title = new H1(this.model.getClass().getSimpleName());
		this.setTitle(title);
		initButtons();
	}

	public void initButtons() {
		save = new Button("Salvar");
		cancel = new Button("Cancelar");
		delete = new Button("Excluir");
		edit = new Button("Editar");
		newRecord = new Button("Novo");
		buttons = new HorizontalLayout(save, cancel, delete, edit, newRecord);
		add(buttons);
		save.addClickListener(e -> save());
		cancel.addClickListener(e -> cancel());
		delete.addClickListener(e -> delete());
		edit.addClickListener(e -> edit());
		newRecord.addClickListener(e -> newRecord());
	}

	private Object newRecord() {
		// TODO Auto-generated method stub
		return null;
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
