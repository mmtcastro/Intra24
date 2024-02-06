package br.com.tdec.intra.abs;

import java.time.ZonedDateTime;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractModelDoc extends AbstractModel {

	@JsonProperty("@unid")
	protected String unid;
	@JsonProperty("@noteid")
	private int noteid;
	@JsonProperty("@index")
	private String index;
	@JsonAlias({ "Id", "id" })
	@NotNull
	protected String id;
	@JsonAlias({ "Uid", "uid" })
	protected String uid;
	@JsonAlias({ "IdOrigem", "idOrigem" })
	protected String idOrigem;
	@JsonAlias({ "CodigoOrigem", "codigoOrigem" })
	protected String codigoOrigem;
	@JsonAlias({ "IdRaiz", "idRaiz" })
	protected String idRaiz;
	@JsonAlias({ "CodigoRaiz", "codigoRaiz" })
	protected String codigoRaiz;
	@JsonAlias({ "CampoOrigem", "campoOrigem" })
	protected String campoOrigem; // utilizado por AbstractModelLista para saber em qual campo fazer o load da
									// lista (saveModel)
	@JsonAlias({ "IdNegocio", "idNegocio" })
	protected String idNegocio;
	@JsonAlias({ "CodigoNegocio", "codigoNegocio" })
	protected String codigoNegocio;
	@JsonAlias({ "Form", "form" })
	protected String form;
	@NotNull
	@JsonAlias({ "Codigo", "codigo" })
	protected String codigo;
	@JsonAlias({ "Status", "status" })
	protected String status;
	@JsonAlias({ "Sit", "sit" })
	protected String sit;
	@JsonAlias({ "Data", "data" })
	protected ZonedDateTime data;
	@JsonAlias({ "autor", "Autor" })
	protected String autor;
	@JsonAlias({ "Criacao", "criacao" })
	protected ZonedDateTime criacao;
	@JsonAlias({ "responsavel", "Responsavel" })
	protected String responsavel;
	@JsonAlias({ "Area", "area" })
	protected String area;
	@JsonAlias({ "nome", "Nome" })
	protected String nome;
	@JsonAlias({ "descricao", "Descricao" })
	protected String descricao;
	@JsonAlias({ "dataMudancaStatus", "DataMudancaStatus" })
	protected ZonedDateTime dataMudancaStatus;
	@JsonAlias({ "responsavelMudancaStatus", "ResponsavelMudancaStatus" })
	protected String responsavelMudancaStatus;
	@JsonAlias({ "Tipo", "tipo" })
	protected String tipo;
	@JsonAlias({ "valor", "Valor" })
	protected Double valor;
//	// protected MimeMultipart obs; retirado pois nao se deve usar RTF/MIME à
//	// vontade. Apenas um por documento é recomendado.
	protected Boolean podeDeletar; // proteção contra ser apagado por agente ou vista
	protected String mensagemPorDeletar; // para explicar por que nao apagou o doc
	protected String userAgent;
	protected String lastModified; // pegar getLastModified to Domino
	protected TreeSet<String> autores;
	protected TreeSet<String> leitores;
	protected Boolean isResponse; // para manter compatibilidade com Notes (contato eh response de Empresa)
	protected String uri; // para guardar a identificacao de um determinado documento. Ex.
							// intra.tdec.com.br/intra.nsf/empresas_contato.xsp?id=empresas_Contato_asdasd_asdsad_sdas

	public int compareTo(AbstractModelDoc outro) {
		if (getCodigo() != null) {
			return getCodigo().compareTo(outro.getCodigo());
		} else {
			return 0;
		}
	}
}
