package br.com.tdec.intra.empresas.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import br.com.tdec.intra.abs.AbstractModelDoc;
import br.com.tdec.intra.utils.jackson.BodyDeserializer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL) // Ignora campos nulos para evitar erros na desserialização
public class Contato extends AbstractModelDoc {

	@JsonProperty("Codigo")
	@JsonAlias({ "codigo", "CODIGO" })
	private String codigo;

	@JsonProperty("CodigoGrupoEconomico")
	@JsonAlias({ "codigoGrupoEconomico", "CODIGOGRUPOECONOMICO" })
	private String codigoGrupoEconomico;

	@JsonProperty("IdGrupoEconomico")
	@JsonAlias({ "idGrupoEconomico", "IDGRUPOECONOMICO" })
	private String idGrupoEconomico;

	@JsonProperty("Vip")
	@JsonAlias({ "vip", "VIP" })
	private String vip;

	@JsonProperty("Cargo")
	@JsonAlias({ "cargo", "CARGO" })
	private String cargo;

	@JsonProperty("CargoContato")
	@JsonAlias({ "cargoContato", "CARGOCONTATO" })
	private String cargoContato;

	@JsonProperty("Tratamento")
	@JsonAlias({ "tratamento", "TRATAMENTO" })
	private String tratamento;

	@JsonProperty("Prioridade")
	@JsonAlias({ "prioridade", "PRIORIDADE" })
	private List<String> prioridade;

	@JsonProperty("DecisaoCompra")
	@JsonAlias({ "decisaoCompra", "DECISAOCOMPRA" })
	private String decisaoCompra;

	@JsonProperty("Telefones")
	@JsonAlias({ "telefones", "TELEFONES" })
	private String telefones;

	@JsonProperty("Celular")
	@JsonAlias({ "celular", "CELULAR" })
	private String celular;

	@JsonProperty("Email")
	@JsonAlias({ "email", "EMAIL" })
	private String email;

	@JsonProperty("Endereco")
	@JsonAlias({ "endereco", "ENDERECO" })
	private String endereco;

	@JsonProperty("Numero")
	@JsonAlias({ "numero", "NUMERO" })
	private String numero;

	@JsonProperty("Complemento")
	@JsonAlias({ "complemento", "COMPLEMENTO" })
	private String complemento;

	@JsonProperty("Bairro")
	@JsonAlias({ "bairro", "BAIRRO" })
	private String bairro;

	@JsonProperty("Cidade")
	@JsonAlias({ "cidade", "CIDADE" })
	private String cidade;

	@JsonProperty("Estado")
	@JsonAlias({ "estado", "ESTADO" })
	private String estado;

	@JsonProperty("Pais")
	@JsonAlias({ "pais", "PAIS" })
	private String pais;

	@JsonProperty("Cep")
	@JsonAlias({ "cep", "CEP" })
	private String cep;

	@JsonProperty("Observacoes")
	@JsonAlias({ "observacoes", "OBSERVACOES" })
	@JsonDeserialize(using = BodyDeserializer.class)
	private RichText observacoes = new RichText(); // Evita problemas com valores nulos

	@JsonProperty("Obs")
	@JsonAlias({ "obs", "OBS" })
	@JsonDeserialize(using = BodyDeserializer.class)
	private RichText obs = new RichText(); // Evita problemas com valores nulos

	@JsonProperty("CheckEmail")
	@JsonAlias({ "checkEmail", "CHECKEMAIL" })
	private List<String> checkEmail;

	@JsonProperty("DataCheckEmail")
	@JsonAlias({ "dataCheckEmail", "DATACHECKEMAIL" })
	private ZonedDateTime dataCheckEmail;

	@JsonProperty("Linkedin")
	@JsonAlias({ "linkedin", "LINKEDIN" })
	private String linkedin;

	@JsonProperty("ContatoFezNegocios")
	@JsonAlias({ "contatoFezNegocios", "CONTATOFEZNEGOCIOS" })
	private Boolean contatoFezNegocios;

	@JsonProperty("Negocios")
	@JsonAlias({ "negocios", "NEGOCIOS" })
	private Map<String, Double> negocios;

	// Campos do LinkedIn
	@JsonProperty("LinkedinContatoUrl")
	@JsonAlias({ "linkedinContatoUrl", "LINKEDINCONTATOURL" })
	private String linkedinContatoUrl;

	@JsonProperty("LinkedinContatoCargo")
	@JsonAlias({ "linkedinContatoCargo", "LINKEDINCONTATOCARGO" })
	private String linkedinContatoCargo;

	@JsonProperty("LinkedinContatoId")
	@JsonAlias({ "linkedinContatoId", "LINKEDINCONTATOID" })
	private String linkedinContatoId;

	@JsonProperty("LinkedinEmpresaUrl")
	@JsonAlias({ "linkedinEmpresaUrl", "LINKEDINEMPRESAURL" })
	private String linkedinEmpresaUrl;

	@JsonProperty("LinkedinEmpresaNome")
	@JsonAlias({ "linkedinEmpresaNome", "LINKEDINEMPRESANOME" })
	private String linkedinEmpresaNome;

	@JsonProperty("LinkedinEmpresaId")
	@JsonAlias({ "linkedinEmpresaId", "LINKEDINEMPRESAID" })
	private String linkedinEmpresaId;

	// Implementação de equals() e hashCode() para evitar problemas com Set/Map
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Contato contato = (Contato) obj;
		return Objects.equals(codigo, contato.codigo);
	}

	@Override
	public int hashCode() {
		return Objects.hash(codigo);
	}
}
