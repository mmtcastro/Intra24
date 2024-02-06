package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;

public class SettingReadingSessionAttributesUI extends UI {

	private static final long serialVersionUID = 1L;

	private String value;

	private VerticalLayout statusHolder = new VerticalLayout();
	private TextField textField = new TextField();

	@Override
	protected void init(VaadinRequest request) {
		add(statusHolder);
		add(textField);
		Button setNewValuesButton = new Button("Set new values", event -> {
			String value = textField.getValue();
			saveValue(SettingReadingSessionAttributesUI.this, value);
		});

//        add(new Button("Set new values", new Button.ClickListener() {
//            @Override
//            public void buttonClick(ClickEvent event) {
//                String value = textField.getValue();
//
//                saveValue(SettingReadingSessionAttributesUI.this, value);
//            }
//        }));
		add(new Button("Set new values", event -> {
			String value = textField.getValue();
			saveValue(SettingReadingSessionAttributesUI.this, value);
		}));

		add(new Button("Reload page", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				getPage().setLocation(getPage().getLocation());
			}
		}));

		showValue(this);
	}

	private static void saveValue(SettingReadingSessionAttributesUI ui, String value) {
		// Save to UI instance
		ui.value = value;
		// Save to VaadinServiceSession
		ui.getSession().setAttribute("myValue", value);
		// Save to HttpSession
		VaadinService.getCurrentRequest().getWrappedSession().setAttribute("myValue", value);

		// Show new values
		showValue(ui);
	}

	private static void showValue(SettingReadingSessionAttributesUI ui) {
		ui.statusHolder.removeAllComponents();
		ui.statusHolder.addComponent(new Label("Value in UI: " + ui.value));
		ui.statusHolder
				.addComponent(new Label("Value in VaadinServiceSession: " + ui.getSession().getAttribute("myValue")));
		ui.statusHolder.addComponent(new Label("Value in HttpSession: "
				+ VaadinService.getCurrentRequest().getWrappedSession().getAttribute("myValue")));
	}

}
