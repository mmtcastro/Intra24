package br.com.tdec.intra.abs;

import java.lang.reflect.InvocationTargetException;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import br.com.tdec.intra.utils.Utils;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractViewDoc extends FormLayout {

	private static final long serialVersionUID = 1L;
	protected AbstractModelDoc model;
	protected Map queryParams;
	private H1 title;
	private Button save;
	private Button cancel;
	private Button delete;
	private Button edit;
	private HorizontalLayout buttons;

	public AbstractViewDoc() {
		// initModel();
	}

	public AbstractViewDoc(AbstractModelDoc model) {
		this.model = model;
		title = new H1(this.model.getClass().getSimpleName());
		this.setTitle(title);

	}

	public void initModel() {
		Class<?> clazz = Utils.getViewDocClassFromViewListaClass(this.getClass());
		try {
			model = (AbstractModelDoc) clazz.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	public void initDefaultForm(AbstractModelDoc model) {
		TextField codigoField = new TextField("codigo");
		TextField descricaoField = new TextField("descricao");
		// DatePicker criacaoField = new DatePicker("criacao");
		add(codigoField);
		Binder<AbstractModelDoc> binder = new Binder<>(AbstractModelDoc.class);
		binder.forField(codigoField).bind(AbstractModelDoc::getCodigo, AbstractModelDoc::setCodigo);

		binder.forField(descricaoField).bind(AbstractModelDoc::getDescricao, AbstractModelDoc::setDescricao);

		binder.setBean(model);

		// add(formLayout);
		initButtons();
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

}
