package br.com.tdec.intra.empresas.model;

import java.time.ZonedDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.tdec.intra.abs.AbstractModelDoc;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL) // Permite valores nulos sem quebrar a serialização
public class Empresa extends AbstractModelDoc implements Comparable<AbstractModelDoc> {

	@JsonProperty("CodigoGrupoEconomico")
	@JsonAlias({ "codigoGrupoEconomico", "CODIGOGRUPOECONOMICO" })
	private String codigoGrupoEconomico;

	@JsonProperty("Cliente")
	@JsonAlias({ "cliente", "CLIENTE" })
	private String cliente;

	@JsonProperty("GerenteConta")
	@JsonAlias({ "gerenteConta", "GERENTE_CONTA" })
	private String gerenteConta;

	@JsonProperty("Estado")
	@JsonAlias({ "estado", "ESTADO" })
	private String estado;

	@JsonProperty("Cgc")
	@JsonAlias({ "CGC", "cgc", "CNPJ", "cnpj" })
	private String cgc;

	@JsonProperty("Codigo")
	@JsonAlias({ "codigo", "CODIGO" })
	private String codigo;

	@JsonProperty("Telefones")
	@JsonAlias({ "telefones", "TELEFONES" })
	private String telefones;

	@JsonProperty("EmailNfeServicos")
	@JsonAlias({ "emailNfeServicos", "EMAIL_NFE_SERVICOS" })
	private String emailNfeServicos;

	@JsonProperty("EmailNfeMateriais")
	@JsonAlias({ "emailNfeMateriais", "EMAIL_NFE_MATERIAIS" })
	private String emailNfeMateriais;

	@JsonProperty("PaginaWeb")
	@JsonAlias({ "paginaWeb", "PAGINA_WEB" })
	private String paginaWeb;

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

	@JsonProperty("Pais")
	@JsonAlias({ "pais", "PAIS" })
	private String pais;

	@JsonProperty("Cep")
	@JsonAlias({ "cep", "CEP" })
	private String cep;

	@JsonProperty("TipoInscricao")
	@JsonAlias({ "tipoInscricao", "TIPO_INSCRICAO" })
	private String tipoInscricao;

	@JsonProperty("Cpf")
	@JsonAlias({ "cpf", "CPF" })
	private String cpf;

	@JsonProperty("Inscricao")
	@JsonAlias({ "inscricao", "INSCRICAO" })
	private String inscricao;

	@JsonProperty("Cnae")
	@JsonAlias({ "cnae", "CNAE" })
	private String cnae;

	@JsonProperty("IndicadorIE")
	@JsonAlias({ "indicadorIE", "INDICADOR_IE" })
	private String indicadorIE;

	@JsonProperty("Ccm")
	@JsonAlias({ "ccm", "CCM" })
	private String ccm;

	@JsonProperty("RegimeTributario")
	@JsonAlias({ "regimeTributario", "REGIME_TRIBUTARIO" })
	private String regimeTributario;

	@JsonProperty("Vertical")
	@JsonAlias({ "vertical", "VERTICAL" })
	private String vertical;

	@JsonProperty("ExcecoesTributarias")
	@JsonAlias({ "excecoesTributarias", "EXCECOES_TRIBUTARIAS" })
	private ExcecoesTributarias excecoesTributarias;

	@JsonProperty("MembroSuframa")
	@JsonAlias({ "membroSuframa", "MEMBRO_SUFRAMA" })
	private String membroSuframa;

	@JsonProperty("InscricaoSuframa")
	@JsonAlias({ "inscricaoSuframa", "INSCRICAO_SUFRAMA" })
	private String inscricaoSuframa;

	@JsonProperty("Banco")
	@JsonAlias({ "banco", "BANCO" })
	private String banco;

	@JsonProperty("BancoNumero")
	@JsonAlias({ "bancoNumero", "BANCO_NUMERO" })
	private String bancoNumero;

	@JsonProperty("Agencia")
	@JsonAlias({ "agencia", "AGENCIA" })
	private String agencia;

	@JsonProperty("AgenciaDigito")
	@JsonAlias({ "agenciaDigito", "AGENCIA_DIGITO" })
	private String agenciaDigito;

	@JsonProperty("Conta")
	@JsonAlias({ "conta", "CONTA" })
	private String conta;

	@JsonProperty("ContaDigito")
	@JsonAlias({ "contaDigito", "CONTA_DIGITO" })
	private String contaDigito;

	@JsonProperty("Cnpj")
	@JsonAlias({ "cnpj", "CNPJ" })
	private Cnpj cnpj;

	@JsonProperty("StatusCnpj")
	@JsonAlias({ "statusCnpj", "STATUS_CNPJ" })
	private String statusCnpj;

	@JsonProperty("UltimaConsultaCnpj")
	@JsonAlias({ "ultimaConsultaCnpj", "ULTIMA_CONSULTA_CNPJ" })
	private ZonedDateTime ultimaConsultaCnpj;

	@JsonProperty("FezNegocios")
	@JsonAlias({ "fezNegocios", "FEZ_NEGOCIOS" })
	private Boolean fezNegocios;

	@JsonProperty("TemFaturas")
	@JsonAlias({ "temFaturas", "TEM_FATURAS" })
	private Boolean temFaturas;

	@Override
	public int hashCode() {
		return Objects.hash(codigo, cgc);
	}
}
