package br.com.tdec.intra.abs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.tdec.intra.utils.Utils;
import br.com.tdec.intra.utils.UtilsSession;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(of = { "id" }) // Gera equals e hashCode baseados no campo 'id'
public abstract class AbstractModelDoc extends AbstractModel {

	@JsonProperty("@meta")
	protected Meta meta;
	@JsonProperty("@unid")
	protected String unid;
	@JsonProperty("@noteid")
	protected int noteid;
	@JsonProperty("@index")
	protected String index;
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
	protected String campoOrigem;
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
	protected Boolean podeDeletar; // proteção contra ser apagado por agente ou vista
	protected String mensagemPorDeletar; // para explicar por que nao apagou o doc
	protected String userAgent;
	protected String lastModified; // pegar getLastModified to Domino
	protected TreeSet<String> autores;
	protected TreeSet<String> leitores;
	protected Boolean isResponse; // para manter compatibilidade com Notes (contato eh response de Empresa)
	protected String uri; // para guardar a identificacao de um determinado documento. Ex.
							// intra.tdec.com.br/intra.nsf/empresas_contato.xsp?id=empresas_Contato_asdasd_asdsad_sdas

	public AbstractModelDoc() {

	}

	public void init() {
		this.id = generateNewModelId();
		this.autor = UtilsSession.getCurrentUserName();
		this.criacao = ZonedDateTime.now();
	}

	public int compareTo(AbstractModelDoc outro) {
		if (getCodigo() != null) {
			return getCodigo().compareTo(outro.getCodigo());
		} else {
			return 0;
		}
	}

	public String generateNewModelId() {
		String ret = "";
		try {
			List<String> classe = Utils.stringToArrayList(this.getClass().getCanonicalName(), ".");
			ret = classe.get(4) + "_" + classe.get(6) + "_" + UUID.randomUUID().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Cria um AbstractModelDoc sem argumentos
	 * 
	 * @param <T>
	 * @param modelClass
	 * @return
	 */
	public <T extends AbstractModelDoc> T createModelDoc(Class<T> modelClass) {
		T model = null;
		try {
			// Accessible constructor might be required
			Constructor<T> constructor = modelClass.getDeclaredConstructor();
			constructor.setAccessible(true); // Make private constructor accessible if needed
			model = constructor.newInstance();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return model;
	}

	/**
	 * O Domino Restapi retorna a consulta do documento por UNID com este objeto
	 * meta
	 * 
	 */
	@Getter
	@Setter
	public class Meta {
		@JsonProperty("noteid")
		private int noteid;
		@JsonProperty("unid")
		private String unid;
		@JsonProperty("created")
		private ZonedDateTime created;
		@JsonProperty("lastmodified")
		private ZonedDateTime lastmodified;
		@JsonProperty("lastaccessed")
		private ZonedDateTime lastaccessed;
		@JsonProperty("lastmodifiedinfile")
		private ZonedDateTime lastmodifiedinfile;
		@JsonProperty("addedtofile")
		private ZonedDateTime addedtofile;
		@JsonProperty("noteclass")
		private List<String> noteclass;
		@JsonProperty("unread")
		private boolean unread;
		@JsonProperty("editable")
		private boolean editable;
		@JsonProperty("revision")
		private String revision;
		@JsonProperty("etag")
		private String etag;
		@JsonProperty("size")
		private int size;
	}

}
