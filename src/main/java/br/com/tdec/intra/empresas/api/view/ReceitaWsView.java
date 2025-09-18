package br.com.tdec.intra.empresas.api.view;

import java.time.ZonedDateTime;
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

	private TextField statusField = criarField("Status");
	private TextField ultimaAtualizacaoField = criarField("Última Atualização");
	private TextField cnpjField = criarField("CNPJ");
	private TextField tipoField = criarField("Tipo");
	private TextField porteField = criarField("Porte");
	private TextField nomeField = criarField("Razão Social");
	private TextField fantasiaField = criarField("Nome Fantasia");
	private TextField aberturaField = criarField("Data de Abertura");
	private TextField naturezaField = criarField("Natureza Jurídica");

	private TextField logradouroField = criarField("Logradouro");
	private TextField numeroField = criarField("Número");
	private TextField complementoField = criarField("Complemento");
	private TextField cepField = criarField("CEP");
	private TextField bairroField = criarField("Bairro");
	private TextField municipioField = criarField("Município");
	private TextField ufField = criarField("UF");

	private TextField emailField = criarField("Email");
	private TextField telefoneField = criarField("Telefone");
	private TextField efrField = criarField("EFR");

	private TextField situacaoField = criarField("Situação Cadastral");
	private TextField dataSituacaoField = criarField("Data da Situação");
	private TextField motivoSituacaoField = criarField("Motivo Situação");
	private TextField situacaoEspecialField = criarField("Situação Especial");
	private TextField dataSituacaoEspecialField = criarField("Data Situação Especial");

	private TextField capitalField = criarField("Capital Social");

	private TextArea atividadesPrincipaisField = criarTextArea("Atividade Principal");
	private TextArea atividadesSecundariasField = criarTextArea("Atividades Secundárias");
	private TextArea sociosField = criarTextArea("Quadro Societário");

	private TextField simplesField = criarField("Optante Simples");
	private TextField simplesDataOpcaoField = criarField("Data Opção Simples");
	private TextField simplesDataExclusaoField = criarField("Data Exclusão Simples");
	private TextField simplesUltimaAtualizacaoField = criarField("Última Atualização Simples");

	private TextField simeiField = criarField("SIMEI");
	private TextField billingFreeField = criarField("Billing Free");
	private TextField billingDatabaseField = criarField("Billing Database");

	public ReceitaWsView() {
		setSpacing(false);
		setPadding(false);
		setWidthFull();

		FormLayout form = new FormLayout();
		form.setWidthFull();
		form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("600px", 2));

		// Adiciona os campos principais em ordem
		form.add(statusField, ultimaAtualizacaoField, cnpjField, tipoField, porteField, nomeField, fantasiaField,
				aberturaField, naturezaField, logradouroField, numeroField, complementoField, cepField, bairroField,
				municipioField, ufField, emailField, telefoneField, efrField, situacaoField, dataSituacaoField,
				motivoSituacaoField, situacaoEspecialField, dataSituacaoEspecialField, capitalField, simplesField,
				simplesDataOpcaoField, simplesDataExclusaoField, simplesUltimaAtualizacaoField, simeiField,
				billingFreeField, billingDatabaseField);

		add(form, atividadesPrincipaisField, atividadesSecundariasField, sociosField);
	}

	public void popular(ReceitaWs dados) {
		statusField.setValue(Optional.ofNullable(dados.getStatus()).orElse(""));
		ultimaAtualizacaoField
				.setValue(Optional.ofNullable(dados.getUltimaAtualizacao()).map(ZonedDateTime::toString).orElse(""));

		cnpjField.setValue(Optional.ofNullable(dados.getCnpj()).orElse(""));
		tipoField.setValue(Optional.ofNullable(dados.getTipo()).orElse(""));
		porteField.setValue(Optional.ofNullable(dados.getPorte()).orElse(""));
		nomeField.setValue(Optional.ofNullable(dados.getNome()).orElse(""));
		fantasiaField.setValue(Optional.ofNullable(dados.getFantasia()).orElse(""));
		aberturaField.setValue(Optional.ofNullable(dados.getAbertura()).orElse(""));
		naturezaField.setValue(Optional.ofNullable(dados.getNaturezaJuridica()).orElse(""));

		logradouroField.setValue(Optional.ofNullable(dados.getLogradouro()).orElse(""));
		numeroField.setValue(Optional.ofNullable(dados.getNumero()).orElse(""));
		complementoField.setValue(Optional.ofNullable(dados.getComplemento()).orElse(""));
		cepField.setValue(Optional.ofNullable(dados.getCep()).orElse(""));
		bairroField.setValue(Optional.ofNullable(dados.getBairro()).orElse(""));
		municipioField.setValue(Optional.ofNullable(dados.getMunicipio()).orElse(""));
		ufField.setValue(Optional.ofNullable(dados.getUf()).orElse(""));

		emailField.setValue(Optional.ofNullable(dados.getEmail()).orElse(""));
		telefoneField.setValue(Optional.ofNullable(dados.getTelefone()).orElse(""));
		efrField.setValue(Optional.ofNullable(dados.getEfr()).orElse(""));

		situacaoField.setValue(Optional.ofNullable(dados.getSituacao()).orElse(""));
		dataSituacaoField.setValue(Optional.ofNullable(dados.getDataSituacao()).orElse(""));
		motivoSituacaoField.setValue(Optional.ofNullable(dados.getMotivoSituacao()).orElse(""));
		situacaoEspecialField.setValue(Optional.ofNullable(dados.getSituacaoEspecial()).orElse(""));
		dataSituacaoEspecialField.setValue(Optional.ofNullable(dados.getDataSituacaoEspecial()).orElse(""));

		capitalField.setValue(dados.getCapitalSocial() != null
				? "R$ " + String.format(Locale.getDefault(), "%,.2f", dados.getCapitalSocial())
				: "");

		atividadesPrincipaisField
				.setValue(Optional
						.ofNullable(dados.getAtividadePrincipal()).filter(lista -> !lista.isEmpty()).map(lista -> lista
								.stream().map(a -> a.getCode() + " - " + a.getText()).collect(Collectors.joining("\n")))
						.orElse(""));

		atividadesSecundariasField.setValue(Optional
				.ofNullable(dados.getAtividadesSecundarias()).filter(lista -> !lista.isEmpty()).map(lista -> lista
						.stream().map(a -> a.getCode() + " - " + a.getText()).collect(Collectors.joining("\n")))
				.orElse(""));

		sociosField.setValue(Optional
				.ofNullable(dados.getQsa()).filter(lista -> !lista.isEmpty()).map(lista -> lista.stream()
						.map(s -> s.getNome() + " (" + s.getQual() + ")").collect(Collectors.joining("\n")))
				.orElse(""));

		if (dados.getSimples() != null) {
			simplesField.setValue(Boolean.TRUE.equals(dados.getSimples().getOptante()) ? "Sim" : "Não");
			simplesDataOpcaoField.setValue(Optional.ofNullable(dados.getSimples().getDataOpcao()).orElse(""));
			simplesDataExclusaoField.setValue(Optional.ofNullable(dados.getSimples().getDataExclusao()).orElse(""));
			simplesUltimaAtualizacaoField.setValue(Optional.ofNullable(dados.getSimples().getUltimaAtualizacao())
					.map(ZonedDateTime::toString).orElse(""));
		}

		simeiField.setValue(dados.getSimei() != null ? dados.getSimei().toString() : "");

		if (dados.getBilling() != null) {
			billingFreeField.setValue(Boolean.TRUE.equals(dados.getBilling().getFree()) ? "Sim" : "Não");
			billingDatabaseField.setValue(Boolean.TRUE.equals(dados.getBilling().getDatabase()) ? "Sim" : "Não");
		}
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
		area.setMaxRows(5);
		return area;
	}
}
