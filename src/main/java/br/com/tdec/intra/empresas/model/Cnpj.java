package br.com.tdec.intra.empresas.model;

import java.util.ArrayList;
import java.util.List;

import br.com.tdec.intra.abs.AbstractModelDoc;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cnpj extends AbstractModelDoc {
	private String message;
	private String cnpj;
	// private String tipo; herdado do Abstract
	private String abertura;
	// private String nome;
	private String fantasia;
	private List<String> atividadePrincipal;
	private String atividadePrincipalCode; // Código CNAE da atividade no formato 00.00-0-00.
	private String atividadePrincipalText; // Descrição da atividade.
	private List<String> atividadesSecundarias;
	private String atividadesSecundariasCode; // Código CNAE da atividade no formato 00.00-0-00.
	private String atividadesSecundariasText;
	private String natureza_juridica;

	private String logradouro;
	private String numero;
	private String complemento;
	private String cep; // 00.000-000.
	private String bairro;
	private String municipio;
	private String uf;
	private String email;
	private String telefone;
	private String efr;

	private String situacao;
	private String data_situacao;
	private String motivo_situacao;
	private String situacao_especial;
	private String data_situacao_especial;

	private Double capital_social;

	private ArrayList<String> qsa; // socios
	private String qsaNome; // nome do sócio
	private String qsaQual; // qualificacao do sócio
	private String qsaPaisOrigem; // País de origem do sócio. Disponível apenas para sócios estrangeiros.
	private String qsaNomeRepLegal; // Nome do representante legal. Disponível apenas para sócios com
									// representantes.
	private String qsaQualRepLegal; // Qualificação do representante legal. Disponível apenas para sócios com
									// representantes.

	private String ultima_atualizacao;

	public Cnpj() {
		// Construtor vazio para Jackson
	}

	// 🔹 Construtor que aceita uma String (para desserialização correta)
	public Cnpj(String cnpj) {
		this.cnpj = cnpj;
	}

}
