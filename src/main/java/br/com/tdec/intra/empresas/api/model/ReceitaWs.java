package br.com.tdec.intra.empresas.api.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ReceitaWs {

	private String status;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
	private ZonedDateTime ultimaAtualizacao;

	private String cnpj;
	private String tipo;
	private String porte;
	private String nome;
	private String fantasia;
	private String abertura;

	@JsonProperty("atividadePrincipal")
	private List<Atividade> atividadePrincipal;

	@JsonProperty("atividadesSecundarias")
	private List<Atividade> atividadesSecundarias;

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
	private String dataSituacao;
	private String motivoSituacao;
	private String situacaoEspecial;
	private String dataSituacaoEspecial;
	private Double capitalSocial;

	private List<Socio> qsa;
	private Simples simples;
	private Simei simei;
	private Billing billing;

	// Getters e Setters omitidos para brevidade
	// Você pode gerar automaticamente no Eclipse ou IntelliJ

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Atividade {
		private String code;
		private String text;

		// Getters e Setters
		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Socio {
		private String nome;
		private String qual;
		private String paisOrigem;
		private String nomeRepLegal;
		private String qualRepLegal;

		// Getters e Setters
		public String getNome() {
			return nome;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		public String getQual() {
			return qual;
		}

		public void setQual(String qual) {
			this.qual = qual;
		}

		public String getPaisOrigem() {
			return paisOrigem;
		}

		public void setPaisOrigem(String paisOrigem) {
			this.paisOrigem = paisOrigem;
		}

		public String getNomeRepLegal() {
			return nomeRepLegal;
		}

		public void setNomeRepLegal(String nomeRepLegal) {
			this.nomeRepLegal = nomeRepLegal;
		}

		public String getQualRepLegal() {
			return qualRepLegal;
		}

		public void setQualRepLegal(String qualRepLegal) {
			this.qualRepLegal = qualRepLegal;
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Getter
	@Setter
	public static class Simples {
		private Boolean optante;
		private String dataOpcao;
		private String dataExclusao;

		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
		private ZonedDateTime ultimaAtualizacao;

		@Override
		public String toString() {
			return "Simples {" + "optante=" + optante + ", dataOpcao='" + dataOpcao + '\'' + ", dataExclusao='"
					+ dataExclusao + '\'' + ", ultimaAtualizacao=" + ultimaAtualizacao + '}';
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Simei {
		@Override
		public String toString() {
			return "Simei {}";
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Getter
	@Setter
	public static class Billing {
		private Boolean free;
		private Boolean database;

		@Override
		public String toString() {
			return "Billing {" + "free=" + free + ", database=" + database + '}';
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ReceitaWs {\n");

		sb.append("  status: ").append(status).append("\n");
		sb.append("  ultimaAtualizacao: ").append(ultimaAtualizacao).append("\n");
		sb.append("  cnpj: ").append(cnpj).append("\n");
		sb.append("  tipo: ").append(tipo).append("\n");
		sb.append("  porte: ").append(porte).append("\n");
		sb.append("  nome: ").append(nome).append("\n");
		sb.append("  fantasia: ").append(fantasia).append("\n");
		sb.append("  abertura: ").append(abertura).append("\n");

		sb.append("  atividadePrincipal:\n");
		if (atividadePrincipal != null) {
			for (Atividade a : atividadePrincipal) {
				sb.append("    - ").append(a.getCode()).append(" - ").append(a.getText()).append("\n");
			}
		}

		sb.append("  atividadesSecundarias:\n");
		if (atividadesSecundarias != null) {
			for (Atividade a : atividadesSecundarias) {
				sb.append("    - ").append(a.getCode()).append(" - ").append(a.getText()).append("\n");
			}
		}

		sb.append("  naturezaJuridica: ").append(naturezaJuridica).append("\n");
		sb.append("  logradouro: ").append(logradouro).append("\n");
		sb.append("  numero: ").append(numero).append("\n");
		sb.append("  complemento: ").append(complemento).append("\n");
		sb.append("  cep: ").append(cep).append("\n");
		sb.append("  bairro: ").append(bairro).append("\n");
		sb.append("  municipio: ").append(municipio).append("\n");
		sb.append("  uf: ").append(uf).append("\n");
		sb.append("  email: ").append(email).append("\n");
		sb.append("  telefone: ").append(telefone).append("\n");
		sb.append("  efr: ").append(efr).append("\n");
		sb.append("  situacao: ").append(situacao).append("\n");
		sb.append("  dataSituacao: ").append(dataSituacao).append("\n");
		sb.append("  motivoSituacao: ").append(motivoSituacao).append("\n");
		sb.append("  situacaoEspecial: ").append(situacaoEspecial).append("\n");
		sb.append("  dataSituacaoEspecial: ").append(dataSituacaoEspecial).append("\n");

		sb.append("  capitalSocial: R$ ");
		sb.append(capitalSocial != null ? String.format(Locale.getDefault(), "%,.2f", capitalSocial) : "N/A")
				.append("\n");

		sb.append("  qsa (sócios):\n");
		if (qsa != null) {
			for (Socio s : qsa) {
				sb.append("    - ").append(s.getNome()).append(" (").append(s.getQual()).append(")\n");
			}
		}

		sb.append("  simples: ").append(simples != null ? simples.toString() : "null").append("\n");
		sb.append("  simei: ").append(simei != null ? simei.toString() : "null").append("\n");
		sb.append("  billing: ").append(billing != null ? billing.toString() : "null").append("\n");

		sb.append("}");

		return sb.toString();
	}

}
