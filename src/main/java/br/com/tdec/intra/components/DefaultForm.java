package br.com.tdec.intra.components;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;

import br.com.tdec.intra.abs.AbstractModelDoc;

import java.io.Serial;
import br.com.tdec.intra.utils.UtilsConverter;

public class DefaultForm extends FormLayout {

    @Serial
    private static final long serialVersionUID = 1L;

	TextField codigo = new TextField("Código");
	TextField descricao = new TextField("Descrição");
	ComboBox<String> status = new ComboBox<>("Status");
	TextField autor = new TextField("Autor");
	TextField criacao = new TextField("Criação");
	Grid<?> grid;

	Button save = new Button("Salvar");
	Button delete = new Button("Apagar");
	Button close = new Button("Cancelar");

	BeanValidationBinder<AbstractModelDoc> binder;

	public DefaultForm(Grid<?> grid) {
		addClassName("default-form");
		this.grid = grid;
		binder = new BeanValidationBinder<>(AbstractModelDoc.class);
		binder.forField(criacao).withConverter(new UtilsConverter.ZonedDateTimeToStringConverter())
				.bind(AbstractModelDoc::getCriacao, AbstractModelDoc::setCriacao);
		autor.setReadOnly(true);
		criacao.setReadOnly(true);
		status.setItems("Ativo", "Inativo");
		status.setPlaceholder("Selecione o status");

		binder.bindInstanceFields(this);
		// save.addClickListener(e -> save());

		add(codigo, descricao, status, autor, criacao, createButtonsLayout());

	}

//	private boolean save() {
//		return repository.saveModel(model, null);
//	}

	public void setModel(AbstractModelDoc model) {
		binder.setBean(model);

	}

	private HorizontalLayout createButtonsLayout() {
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
		close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		save.addClickShortcut(Key.ENTER);
		close.addClickShortcut(Key.ESCAPE);

		close.addClickListener(e -> closeEditor());

		return new HorizontalLayout(save, delete, close);

	}

	public void closeEditor() {
		setModel(null);
		setVisible(false);
		removeClassName("abstract-view-lista-editing");
	}

	public void closeFormDefault() {
		removeClassName("editing");
	}
}
