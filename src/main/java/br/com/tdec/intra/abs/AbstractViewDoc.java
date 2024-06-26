package br.com.tdec.intra.abs;

import java.time.format.DateTimeFormatter;

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
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.Flex;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

import br.com.tdec.intra.abs.AbstractService.DeleteResponse;
import br.com.tdec.intra.abs.AbstractService.SaveResponse;
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
	protected Button saveButton;
	protected Button cancelButton;
	protected Button editButton;
	protected Button deleteButton;
	protected Dialog deleteDialog;
	protected boolean isReadOnly;

	protected HorizontalLayout horizontalLayoutButtons;
	protected HorizontalLayout footer;
	protected Span autorSpan;
	protected Span criacaoSpan;
	protected Span idSpan;

	public AbstractViewDoc(Class<T> modelType, AbstractService<T> service) {
		this.service = service;
		this.modelType = modelType;
		binder = new Binder<>(modelType);

		// addClassNames("formlayout-view", Width.FULL, Display.FLEX, Flex.AUTO,
		// Margin.LARGE);

		addClassNames("abstract-view-doc.css", Width.FULL, Display.FLEX, Flex.AUTO, Margin.LARGE);

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

	public void initButtons() {
		saveButton = new Button("Salvar");
		cancelButton = new Button("Cancelar");
		deleteButton = new Button("Excluir");
		editButton = new Button("Editar");
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		cancelButton.addClickShortcut(Key.ESCAPE);
		saveButton.addClickShortcut(Key.ENTER);
		saveButton.addClickListener(e -> save());
		cancelButton.addClickListener(e -> cancel());
		deleteButton.addClickListener(e -> openConfirmDeleteDialog());
		editButton.addClickListener(e -> edit());
		showButtons();
	}

	protected void showButtons() {
		if (horizontalLayoutButtons == null) {
			horizontalLayoutButtons = new HorizontalLayout();
		} else {
			horizontalLayoutButtons.removeAll();
		}
		if (isNovo) {
			horizontalLayoutButtons.add(saveButton, cancelButton);
		} else if (!isNovo && !isReadOnly) {
			horizontalLayoutButtons.add(saveButton, deleteButton, cancelButton);
		} else if (!isNovo && isReadOnly) {
			horizontalLayoutButtons.add(editButton, cancelButton);
		} else {
			Notification.show("Erro mostrando os botoes de ação.");
		}
		add(horizontalLayoutButtons);
	}

	public void initFooter() {
		if (model != null) {
			footer = new HorizontalLayout();
			footer.addClassName("abstract-view-doc-footer");
			autorSpan = new Span("Autor: " + model.getAutor());
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
			if (model.getCriacao() != null) {
				criacaoSpan = new Span("Criação: " + model.getCriacao().format(formatter));
			} else {
				criacaoSpan = new Span("");
			}

			idSpan = new Span("Id: " + model.getId());
			footer.add(autorSpan, criacaoSpan, idSpan);
			add(footer, 3);
		}
	}

	private void openConfirmDeleteDialog() {
		Dialog dialog = new Dialog();
		Span message = new Span("Tem certeza que deseja apagar " + model.getForm() + " - " + model.getCodigo() + "?");
		Button confirmButton = new Button("Confirmar", event -> {
			DeleteResponse deleteResponse = service.delete(model.getMeta().getUnid());
			System.out.println("DeleteResponse: " + deleteResponse);
			if (deleteResponse != null) {
				if (deleteResponse.getStatusCode().equals("200")) {
					Notification notification = Notification.show(deleteResponse.getMessage());
					notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
				} else if (deleteResponse.getStatusCode().equals("403")) {
					Notification notification = Notification.show(deleteResponse.getMessage());
					notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
				} else {
					Notification notification = Notification.show(deleteResponse.getMessage());
					notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
				}
			}
			dialog.close();
			UI.getCurrent().getPage().getHistory().back();
		});
		confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		Button cancelButton = new Button("Cancelar", event -> dialog.close());

		cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		HorizontalLayout cancelButtonsLayout = new HorizontalLayout(confirmButton, cancelButton);
		cancelButtonsLayout.getStyle().set("padding-top", "10px");

		dialog.add(message, cancelButtonsLayout);
		dialog.open();
	}

	// protected abstract SaveResponse save();

	public void save() {
		try {
			SaveResponse saveResponse = null;
			binder.validate();
			if (isNovo) {
				saveResponse = service.save(model);
			} else {
				saveResponse = service.put(model.getMeta().getUnid());
			}

			if (saveResponse != null) {
				if (saveResponse.getStatus() == null) {
					Notification notification = Notification.show("Documento Salvo com Sucesso!");
					notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
					return;
				} else if (saveResponse.getStatus().equals("403")) {
					Notification notification = Notification.show(saveResponse.getMessage());
					notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
					return;
				} else {
					Notification notification = Notification.show(saveResponse.getMessage());
					notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected abstract SaveResponse update();

	protected abstract DeleteResponse delete();

	protected void edit() {
		isReadOnly = false;
		binder.setReadOnly(isReadOnly);
		showButtons();
	}

	protected void cancel() {
		UI.getCurrent().getPage().getHistory().back();
	}

	private void sendMail() {
		mailService.sendSimpleMessage("mcastro@tdec.com.br", "Teste", "Conteudo de mensagem em texto simples.");

	}

}
