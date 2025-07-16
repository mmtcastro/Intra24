package br.com.tdec.intra.empresas.componentes;

import java.io.Serial;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import br.com.tdec.intra.abs.AbstractModelDoc;
import br.com.tdec.intra.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnderecoForm<T extends AbstractModelDoc> extends FormLayout {

    @Serial
    private static final long serialVersionUID = 1L;
	private T model;
	private Binder<T> binder;

	private TextField enderecoField = new TextField("Endereço");
	private TextField numeroField = new TextField("Número");
	private TextField bairroField = new TextField("Bairro");
	private TextField cidadeField = new TextField("Cidade");
	private ComboBox<String> ufComboBox = new ComboBox<>("Estado");

	public EnderecoForm(Binder<T> binder, T model) {
		this.model = model;
		this.binder = binder;

		setResponsiveSteps(new ResponsiveStep("0px", 1), new ResponsiveStep("600px", 2));

		ufComboBox.setItems(Utils.getUfs());
		ufComboBox.setPlaceholder("Selecione um Estado");

		add(enderecoField, numeroField, bairroField, cidadeField, ufComboBox);
		setColspan(enderecoField, 2);

		// 🚀 Usa reflection para mapear os campos automaticamente
		binder.bindInstanceFields(this);
	}

	/**
	 * Valida se o modelo possui os métodos get/set obrigatórios.
	 */
	private void validateModelFields() {
		List<String> requiredFields = List.of("Endereco", "Numero", "Bairro", "Cidade", "Estado");
		List<Method> modelMethods = Arrays.asList(model.getClass().getMethods());

		for (String field : requiredFields) {
			boolean hasGetter = modelMethods.stream().anyMatch(m -> m.getName().equalsIgnoreCase("get" + field));
			boolean hasSetter = modelMethods.stream().anyMatch(m -> m.getName().equalsIgnoreCase("set" + field));

			if (!hasGetter || !hasSetter) {
				// 🔹 Se um campo estiver ausente, mostra um aviso no UI
				Notification.show(
						"⚠ Erro: O modelo " + model.getClass().getSimpleName()
								+ " não possui os métodos get/set para o campo: " + field,
						5000, Notification.Position.MIDDLE);

				// 🔹 Também pode lançar uma exceção para evitar problemas mais graves
				throw new IllegalStateException("O modelo " + model.getClass().getSimpleName()
						+ " não possui os métodos get/set para o campo: " + field);
			}
		}
	}

	/**
	 * Faz o bind automático entre os campos do formulário e os métodos do modelo.
	 */
	private void bindFields() {
		binder.forField(enderecoField).bind(model -> invokeGetter(model, "endereco"),
				(model, value) -> invokeSetter(model, "endereco", value));

		binder.forField(numeroField).bind(model -> invokeGetter(model, "numero"),
				(model, value) -> invokeSetter(model, "numero", value));

		binder.forField(bairroField).bind(model -> invokeGetter(model, "bairro"),
				(model, value) -> invokeSetter(model, "bairro", value));

		binder.forField(cidadeField).bind(model -> invokeGetter(model, "cidade"),
				(model, value) -> invokeSetter(model, "cidade", value));

		binder.forField(ufComboBox).bind(model -> invokeGetter(model, "estado"),
				(model, value) -> invokeSetter(model, "estado", value));
	}

	/**
	 * Invoca dinamicamente o getter de um campo do modelo.
	 */
	private String invokeGetter(T model, String fieldName) {
		try {
			Method getter = model.getClass().getMethod("get" + capitalize(fieldName));
			Object value = getter.invoke(model);
			return value != null ? value.toString() : "";
		} catch (Exception e) {
			throw new RuntimeException("Erro ao acessar o getter para o campo: " + fieldName, e);
		}
	}

	/**
	 * Invoca dinamicamente o setter de um campo do modelo.
	 */
	private void invokeSetter(T model, String fieldName, String value) {
		try {
			Method setter = model.getClass().getMethod("set" + capitalize(fieldName), String.class);
			setter.invoke(model, value);
		} catch (Exception e) {
			throw new RuntimeException("Erro ao acessar o setter para o campo: " + fieldName, e);
		}
	}

	/**
	 * Converte a primeira letra de uma string para maiúscula.
	 */
	private String capitalize(String str) {
		if (str == null || str.isEmpty())
			return str;
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

}
