package br.com.tdec.intra.abs;

import java.lang.reflect.InvocationTargetException;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import br.com.tdec.intra.utils.Utils;

public class AbstractViewDoc extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	protected AbstractModelDoc model;

	public AbstractViewDoc() {
		initModel();

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
