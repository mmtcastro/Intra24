package br.com.tdec.intra.api.view;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import br.com.tdec.intra.api.model.Receitaws;

import java.io.Serial;

public class ReceitawsView extends VerticalLayout {

    @Serial
    private static final long serialVersionUID = 1L;
	private final TextField status = new TextField("Status");
	private final TextField ultimaAtualizacao = new TextField("Última Atualização");
	private final TextField cnpj = new TextField("CNPJ");
	private final TextField nome = new TextField("Razão Social");
	private final TextField fantasia = new TextField("Nome Fantasia");
	private final TextField abertura = new TextField("Data de Abertura");
	private final TextField tipo = new TextField("Tipo");
	private final TextField porte = new TextField("Porte");
	private final TextField naturezaJuridica = new TextField("Natureza Jurídica");

	private final TextField logradouro = new TextField("Logradouro");
	private final TextField numero = new TextField("Número");
	private final TextField complemento = new TextField("Complemento");
	private final TextField bairro = new TextField("Bairro");
	private final TextField municipio = new TextField("Município");
	private final TextField uf = new TextField("UF");
	private final TextField cep = new TextField("CEP");
	private final TextField telefone = new TextField("Telefone");
	private final TextField email = new TextField("E-mail");

	private final TextField capitalSocial = new TextField("Capital Social");
	private final TextField atividadePrincipal = new TextField("Atividade Principal");

	private final Binder<Receitaws> binder = new Binder<>(Receitaws.class);

	public ReceitawsView() {
		setPadding(true);
		setSpacing(true);

		FormLayout formLayout = new FormLayout();
		formLayout.add(status, ultimaAtualizacao, cnpj, nome, fantasia, abertura, tipo, porte, naturezaJuridica);
		formLayout.add(logradouro, numero, complemento, bairro, municipio, uf, cep, telefone, email);
		formLayout.add(capitalSocial, atividadePrincipal);

		// Ajuste de largura dos campos
		formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1),
				new FormLayout.ResponsiveStep("600px", 2), new FormLayout.ResponsiveStep("900px", 3));

		add(formLayout);
		configureBinder();
	}

	private void configureBinder() {
		binder.bindInstanceFields(this);
	}

	public void setReceitawsData(Receitaws receitaws) {
		if (receitaws != null) {
			binder.setBean(receitaws);
			if (receitaws.getAtividadePrincipal() != null && !receitaws.getAtividadePrincipal().isEmpty()) {
				atividadePrincipal.setValue(receitaws.getAtividadePrincipal().get(0).getText());
			}
		}
	}
}
