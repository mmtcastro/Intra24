package br.com.tdec.intra.abs;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.Flex;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

import br.com.tdec.intra.abs.AbstractService.DeleteResponse;
import br.com.tdec.intra.config.MailService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractViewDoc<T extends AbstractModelDoc> extends FormLayout
		implements HasUrlParameter<String> {

	private static final long serialVersionUID = 1L;
	protected T model;
	protected Class<T> modelType;
	protected AbstractService<T> service;
	protected MailService mailService;
	protected String unid;
	protected Binder<T> binder;
	protected boolean isNovo;
	protected Map queryParams;
	protected H1 title;
	// protected FormLayout form = new FormLayout();
	protected Button saveButton;
	protected Button cancelButton;
	protected Button editButton;
	protected Button deleteButton;
	protected Dialog deleteDialog;
	protected boolean isEditable;

	protected HorizontalLayout horizontalLayoutButtons;
	protected HorizontalLayout footer;
	protected Span autorSpan;
	protected Span criacaoSpan;
	protected Span idSpan;

	public AbstractViewDoc(Class<T> modelType, AbstractService<T> service) {
		this.service = service;
		this.modelType = modelType;
		model = createModel(); // vai ser substituido pelo setParameter
		binder = new Binder<>(modelType);
		binder.setBean(model);
		isEditable = false;
		binder.setReadOnly(isEditable);

		addClassNames("formlayout-view", Width.FULL, Display.FLEX, Flex.AUTO, Margin.LARGE);
		// title = new H1(this.model.getClass().getSimpleName());
		this.setTitle(title);
		setWidth("100%");
		getStyle().set("flex-grow", "1");

	}

	@Autowired
	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	protected T createModel() {
		try {
			return modelType.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Nao foi possivel criar o modelo - " + modelType, e);
		}
	}

//	public void initModel() {
//		Class<?> clazz = Utils.getViewDocClassFromViewListaClass(this.getClass());
//		try {
//			model = (AbstractModelDoc) clazz.getDeclaredConstructor().newInstance();
//		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
//				| NoSuchMethodException | SecurityException e) {
//			e.printStackTrace();
//		}
//	}

//	public void initDefaultForm(AbstractModelDoc model) {
//		TextField codigoField = new TextField("codigo");
//		TextField descricaoField = new TextField("descricao");
//		// DatePicker criacaoField = new DatePicker("criacao");
//		add(codigoField);
//		Binder<AbstractModelDoc> binder = new Binder<>(AbstractModelDoc.class);
//		binder.forField(codigoField).bind(AbstractModelDoc::getCodigo, AbstractModelDoc::setCodigo);
//
//		binder.forField(descricaoField).bind(AbstractModelDoc::getDescricao, AbstractModelDoc::setDescricao);
//
//		binder.setBean(model);
//
//		// add(formLayout);
//		initButtons();
//	}

	public void initButtons() {
		saveButton = new Button("Salvar");
		cancelButton = new Button("Cancelar");
		deleteButton = new Button("Excluir");
		editButton = new Button("Editar");
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		cancelButton.addClickShortcut(Key.ESCAPE);
		saveButton.addClickShortcut(Key.ENTER);
		horizontalLayoutButtons = new HorizontalLayout(saveButton, editButton, deleteButton, cancelButton);

		saveButton.addClickListener(e -> save());
		cancelButton.addClickListener(e -> cancel());
		deleteButton.addClickListener(e -> openConfirmDeleteDialog());
		editButton.addClickListener(e -> edit());
		add(horizontalLayoutButtons, 2);
	}

	public void initFooter() {
		if (model != null) {
			footer = new HorizontalLayout();
			footer.addClassName("abstract-view-doc-footer");
			autorSpan = new Span("Autor: " + model.getAutor());
			criacaoSpan = new Span("Criação: " + model.getCriacao());
			idSpan = new Span("Id: " + model.getId());

			footer.add(autorSpan, criacaoSpan, idSpan);
			add(footer, 3);
		}
	}

	private void openConfirmDeleteDialog() {
		Dialog dialog = new Dialog();

		Span message = new Span("Tem certeza que deseja apagar " + model.getForm() + " - " + model.getCodigo() + "?");
		Button confirmButton = new Button("Confirmar", event -> {
			DeleteResponse deleteResponse = service.delete(model.getUnid());
			dialog.close();
			Notification.show(deleteResponse.getMessage());
		});
		confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		Button cancelButton = new Button("Cancelar", event -> dialog.close());
		cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		HorizontalLayout cancelButtonsLayout = new HorizontalLayout(confirmButton, cancelButton);
		cancelButtonsLayout.getStyle().set("padding-top", "10px");

		dialog.add(message, cancelButtonsLayout);
		dialog.open();
	}

	protected abstract void save();

	protected void edit() {
		if (isEditable) {
			isEditable = false;
			binder.setReadOnly(isEditable);
		} else {
			isEditable = true;
			binder.setReadOnly(isEditable);
		}
		this.binder.setReadOnly(isEditable);
	}

	protected void cancel() {
		UI.getCurrent().getPage().getHistory().back();
	}

	private void sendMail() {
		mailService.sendSimpleMessage("mcastro@tdec.com.br", "Teste", "Conteudo de mensagem em texto simples.");

	}

}
