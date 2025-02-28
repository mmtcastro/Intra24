package br.com.tdec.intra.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false) // 🔹 Garante que equals/hashCode considerem a superclasse
@JsonIgnoreProperties(ignoreUnknown = true) // Ignora campos desconhecidos na resposta JSON
public class Receitaws {

	private String status;

	@JsonProperty("ultima_atualizacao")
	private String ultimaAtualizacao;

	private String cnpj;
	private String tipo;
	private String porte;
	private String nome;
	private String fantasia;
	private String abertura;

	@JsonProperty("atividade_principal")
	private List<Atividade> atividadePrincipal;

	@JsonProperty("atividades_secundarias")
	private List<Atividade> atividadesSecundarias;

	@JsonProperty("natureza_juridica")
	private String naturezaJuridica;

	private String logradouro;
	private String numero;
	private String complemento;
	private String cep;
	private String bairro;
	private String municipio;
	private String uf;
	private String email;
	private String telefone;
	private String efr;

	private String situacao;

	@JsonProperty("data_situacao")
	private String dataSituacao;

	@JsonProperty("motivo_situacao")
	private String motivoSituacao;

	@JsonProperty("situacao_especial")
	private String situacaoEspecial;

	@JsonProperty("data_situacao_especial")
	private String dataSituacaoEspecial;

	@JsonProperty("capital_social")
	private String capitalSocial;

	private List<Socio> qsa;
	private Simples simples;
	private Simei simei;
	private Billing billing;

	// 🔹 Classe interna para representar atividades principais e secundárias
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Atividade {
		private String code;
		private String text;
	}

	// 🔹 Classe interna para representar sócios (QSA)
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Socio {
		private String nome;
		private String qual;

		@JsonProperty("pais_origem")
		private String paisOrigem;

		@JsonProperty("nome_rep_legal")
		private String nomeRepLegal;

		@JsonProperty("qual_rep_legal")
		private String qualRepLegal;
	}

	// 🔹 Classe interna para Simples Nacional
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Simples {
		private boolean optante;

		@JsonProperty("data_opcao")
		private String dataOpcao;

		@JsonProperty("data_exclusao")
		private String dataExclusao;

		@JsonProperty("ultima_atualizacao")
		private String ultimaAtualizacao;
	}

	// 🔹 Classe interna para Simei (usamos herança de Simples, pois tem a mesma
	// estrutura)
	@Data
	@NoArgsConstructor
	public static class Simei extends Simples {
	}

	// 🔹 Classe interna para Billing
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Billing {
		private boolean free;
		private boolean database;
	}
}
