package br.com.tdec.intra.empresas.api.view;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import br.com.tdec.intra.empresas.api.model.ReceitaWs;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceitaWsView extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private TextField cnpjField = criarField("CNPJ");
	private TextField razaoSocialField = criarField("Razão Social");
	private TextField aberturaField = criarField("Data de Abertura");
	private TextField situacaoField = criarField("Situação Cadastral");
	private TextField naturezaField = criarField("Natureza Jurídica");
	private TextField porteField = criarField("Porte");
	private TextField capitalField = criarField("Capital Social");
	private TextField telefoneField = criarField("Telefone");
	private TextField emailField = criarField("Email");

	private TextArea atividadesPrincipaisField = criarTextArea("Atividade Principal");
	private TextArea atividadesSecundariasField = criarTextArea("Atividades Secundárias");
	private TextArea sociosField = criarTextArea("Quadro Societário");

	public ReceitaWsView() {
		setSpacing(false);
		setPadding(false);
		setWidthFull();

		FormLayout form = new FormLayout();
		form.setWidthFull();

		form.add(cnpjField, razaoSocialField, aberturaField, situacaoField, naturezaField, porteField, capitalField,
				telefoneField, emailField);

		add(form, atividadesPrincipaisField, atividadesSecundariasField, sociosField);
	}

	public void popular(ReceitaWs dados) {
		cnpjField.setValue(Optional.ofNullable(dados.getCnpj()).orElse(""));
		razaoSocialField.setValue(Optional.ofNullable(dados.getNome()).orElse(""));
		aberturaField.setValue(Optional.ofNullable(dados.getAbertura()).orElse(""));
		situacaoField.setValue(Optional.ofNullable(dados.getSituacao()).orElse(""));
		naturezaField.setValue(Optional.ofNullable(dados.getNaturezaJuridica()).orElse(""));
		porteField.setValue(Optional.ofNullable(dados.getPorte()).orElse(""));
		telefoneField.setValue(Optional.ofNullable(dados.getTelefone()).orElse(""));
		emailField.setValue(Optional.ofNullable(dados.getEmail()).orElse(""));

		String capital = dados.getCapitalSocial() != null
				? "R$ " + String.format(Locale.getDefault(), "%,.2f", dados.getCapitalSocial())
				: "";
		capitalField.setValue(capital);

		// Atividades principais
		String principais = Optional.ofNullable(dados.getAtividadePrincipal()).filter(lista -> !lista.isEmpty()).map(
				lista -> lista.stream().map(a -> a.getCode() + " - " + a.getText()).collect(Collectors.joining("\n")))
				.orElse("");
		atividadesPrincipaisField.setValue(principais);

		// Atividades secundárias
		String secundarias = Optional
				.ofNullable(dados.getAtividadesSecundarias()).filter(lista -> !lista.isEmpty()).map(lista -> lista
						.stream().map(a -> a.getCode() + " - " + a.getText()).collect(Collectors.joining("\n")))
				.orElse("");
		atividadesSecundariasField.setValue(secundarias);

		// Sócios
		String socios = Optional.ofNullable(dados.getQsa()).filter(lista -> !lista.isEmpty()).map(lista -> lista
				.stream().map(s -> s.getNome() + " (" + s.getQual() + ")").collect(Collectors.joining("\n")))
				.orElse("");
		sociosField.setValue(socios);
	}

	private static TextField criarField(String label) {
		TextField field = new TextField(label);
		field.setReadOnly(true);
		field.setWidthFull();
		return field;
	}

	private static TextArea criarTextArea(String label) {
		TextArea area = new TextArea(label);
		area.setReadOnly(true);
		area.setWidthFull();
		area.setMaxRows(3);
		return area;
	}
}
