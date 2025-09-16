package br.com.tdec.intra.abs;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import br.com.tdec.intra.utils.Utils;
import br.com.tdec.intra.utils.UtilsSession;
import br.com.tdec.intra.utils.jackson.ZonedDateTimeDeserializer;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL) // nao mostrar campos nulos na conversao Jackson

public abstract class AbstractModelDoc extends AbstractModel {

	// Metadados do Domino
	@JsonProperty("@meta")
	@JsonAlias({ "@meta", "meta" })
	protected Meta meta;

	@JsonProperty("@unid")
	@JsonAlias({ "@unid", "unid" })
	protected String unid;

	@JsonProperty("@noteid")
	@JsonAlias({ "@noteid", "noteid" })
	protected int noteid;

	@JsonProperty("@index")
	@JsonAlias({ "@index", "index" })
	protected String index;

	// Identificação lógica
	@JsonProperty("Id")
	@JsonAlias({ "Id", "id" })
	@NotNull
	protected String id;

	@JsonProperty("uid")
	@JsonAlias({ "Uid", "uid" })
	protected String uid;

	@JsonProperty("UnidOrigem")
	@JsonAlias({ "UnidOrigem", "unidOrigem" })
	protected String unidOrigem;

	@JsonProperty("IdOrigem")
	@JsonAlias({ "IdOrigem", "idOrigem" })
	protected String idOrigem;

	@JsonProperty("CodigoOrigem")
	@JsonAlias({ "CodigoOrigem", "codigoOrigem" })
	protected String codigoOrigem;

	@JsonProperty("IdRaiz")
	@JsonAlias({ "IdRaiz", "idRaiz" })
	protected String idRaiz;

	@JsonProperty("CodigoRaiz")
	@JsonAlias({ "CodigoRaiz", "codigoRaiz" })
	protected String codigoRaiz;

	@JsonProperty("CampoOrigem")
	@JsonAlias({ "CampoOrigem", "campoOrigem" })
	protected String campoOrigem;

	@JsonProperty("IdNegocio")
	@JsonAlias({ "IdNegocio", "idNegocio" })
	protected String idNegocio;

	@JsonProperty("CodigoNegocio")
	@JsonAlias({ "CodigoNegocio", "codigoNegocio" })
	protected String codigoNegocio;

	// 🚨 Campo obrigatório para criar documentos no Domino
	@JsonProperty("Form")
	@JsonAlias({ "Form", "form" })
	@NotNull
	protected String form;

	// Atributos de negócio
	@JsonProperty("Codigo")
	@JsonAlias({ "Codigo", "codigo" })
	@NotNull
	protected String codigo;

	@JsonProperty("Status")
	@JsonAlias({ "Status", "status" })
	protected String status;

	@JsonProperty("Sit")
	@JsonAlias({ "Sit", "sit" })
	protected String sit;

	@JsonProperty("Criacao")
	@JsonAlias({ "Criacao", "criacao" })
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	protected ZonedDateTime criacao;

	@JsonProperty("Data")
	@JsonAlias({ "Data", "data" })
	protected LocalDate data;

	@JsonProperty("Autor")
	@JsonAlias({ "autor", "Autor" })
	protected String autor;

	@JsonProperty("Responsavel")
	@JsonAlias({ "responsavel", "Responsavel" })
	protected String responsavel;

	@JsonProperty("Area")
	@JsonAlias({ "Area", "area" })
	protected String area;

	@JsonProperty("Nome")
	@JsonAlias({ "nome", "Nome" })
	protected String nome;

	@JsonProperty("Descricao")
	@JsonAlias({ "descricao", "Descricao" })
	protected String descricao;

	@JsonProperty("DataMudancaStatus")
	@JsonAlias({ "dataMudancaStatus", "DataMudancaStatus" })
	protected ZonedDateTime dataMudancaStatus;

	@JsonProperty("ResponsavelMudancaStatus")
	@JsonAlias({ "responsavelMudancaStatus", "ResponsavelMudancaStatus" })
	protected String responsavelMudancaStatus;

	@JsonProperty("Tipo")
	@JsonAlias({ "Tipo", "tipo" })
	protected String tipo;

	@JsonProperty("Valor")
	@JsonAlias({ "valor", "Valor" })
	protected Double valor;

	// Controle interno
	@JsonProperty("PodeDeletar")
	protected Boolean podeDeletar;

	@JsonProperty("MensagemPorDeletar")
	protected String mensagemPorDeletar;

	@JsonProperty("UserAgent")
	protected String userAgent;

	@JsonProperty("LastModified")
	protected String lastModified;

	@JsonProperty("Autores")
	protected TreeSet<String> autores;

	@JsonProperty("Leitores")
	protected TreeSet<String> leitores;

	@JsonProperty("IsResponse")
	protected Boolean isResponse;

	@JsonProperty("Uri")
	protected String uri;

	@JsonProperty("Revision")
	protected String revision;

	// RichText (descomentar quando necessário)
	// @JsonProperty("Body")
	// @JsonAlias({ "body", "Body" })
	// @JsonDeserialize(using = BodyDeserializer.class)
	// protected RichText body;
	//
	// @JsonProperty("Obs")
	// @JsonAlias({ "obs", "Obs" })
	// @JsonDeserialize(using = BodyDeserializer.class)
	// protected RichText obs;

	// Anexos
	@JsonProperty("$FILES")
	@JsonAlias({ "fileNames", "$FILES" })
	protected List<String> fileNames = new ArrayList<>();

	@JsonProperty("Uploads")
	protected List<UploadedFile> uploads = new ArrayList<>();

	@JsonProperty("AnexosParaExcluir")
	protected List<String> anexosParaExcluir = new ArrayList<>();

	public AbstractModelDoc() {
		super();
		this.autores = new TreeSet<>();
		this.leitores = new TreeSet<>();
		this.fileNames = new ArrayList<>();
		this.podeDeletar = true; // valor default
		this.mensagemPorDeletar = "";
		this.isResponse = false;
		this.init();
	}

	public void init() {
		try {
			// Se já tem UNID, não mexe (doc existente)
			if (this.getMeta() != null && this.getMeta().getUnid() != null) {
				return;
			}

			// Form obrigatório
			if (this.form == null || this.form.isBlank()) {
				this.form = this.getClass().getSimpleName();
			}

			// ID lógico
			if (this.id == null || this.id.isBlank()) {
				this.id = generateNewModelId();
			}

			// Autor
			if (this.autor == null || this.autor.isBlank()) {
				this.autor = UtilsSession.getCurrentUserName();
			}

			// Data de criação
			if (this.criacao == null) {
				this.criacao = ZonedDateTime.now();
			}

		} catch (Exception e) {
			System.err.println("Erro init() ao inicializar o AbstractModelDoc: " + e.getMessage());
			e.printStackTrace();
		}
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
	@ToString
	public static class Meta {
		@JsonProperty("noteid")
		private int noteid;
		@JsonProperty("parentunid")
		private String parentunid; // response ou repl conflict
		@JsonProperty("unid")
		private String unid;
		@JsonProperty("created")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
		private ZonedDateTime created;
		@JsonProperty("lastmodified")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
		private ZonedDateTime lastmodified;
		@JsonProperty("lastaccessed")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
		private ZonedDateTime lastaccessed;
		@JsonProperty("lastmodifiedinfile")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
		private ZonedDateTime lastmodifiedinfile;
		@JsonProperty("addedtofile")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
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
		@JsonProperty("toplevelchildunids")
		@JsonIgnore
		private String toplevelchildunids;
	}

	@Getter
	@Setter
	public static class RichText {
		private String type;
		private String encoding;
		private String headers;
		private String content;
		// private String decodedContent;

		// Construtor padrão com valores para HTML e Base64
		public RichText() {
			this.type = "text/html";
			this.encoding = "BASE64"; // Para lidar com conteúdo complexo como HTML
			this.headers = "Content-Type: text/html; charset=UTF-8";
		}

		// Método para definir conteúdo em texto simples, convertendo automaticamente
		// para Base64
		public void setPlainTextContent(String plainText) {
			this.content = Base64.getEncoder().encodeToString(plainText.getBytes(StandardCharsets.UTF_8));
		}

		// Método para definir conteúdo HTML diretamente, sem codificação Base64
		public void setHtmlContent(String htmlContent) {
			this.content = htmlContent;
			this.encoding = "PLAIN"; // Define como "PLAIN" para HTML direto
		}

		// Método para verificar e ajustar a codificação e o tipo de conteúdo
		public void ensureHtmlBase64Encoding() {
			if (!"text/html".equalsIgnoreCase(this.type)) {
				this.type = "text/html";
			}
			if (!"BASE64".equalsIgnoreCase(this.encoding)) {
				this.encoding = "BASE64";
			}
		}

		@Override
		public String toString() {
			return "RichText {" + "\n  type='" + type + '\'' + ",\n  encoding='" + encoding + '\'' + ",\n  headers='"
					+ headers + '\'' + ",\n  content='"
					+ (content != null ? content.substring(0, Math.min(content.length(), 100)) + "..." : "null") + '\''
					+ "\n}";
		}

	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		AbstractModelDoc that = (AbstractModelDoc) o;

		return id != null ? id.equals(that.id) : that.id == null;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	public void newRevision() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss");
		if (this.revision != null && this.revision.contains("-")) {
			String[] parts = this.revision.split("-");
			int currentRevisionNumber = Integer.parseInt(parts[0]);
			// Incrementa o número e atualiza a data e hora
			this.revision = (currentRevisionNumber + 1) + "-" + LocalDateTime.now().format(formatter);
		} else {
			// Caso não tenha um valor válido, inicializa como a primeira revisão
			this.revision = "1-" + LocalDateTime.now().format(formatter);
		}
	}

	private int obterTamanhoListas(String prefix, Field[] innerFields) {
		int tamanho = 0;
		for (Field innerField : innerFields) {
			String nomeCampo = prefix + Utils.capitalize(innerField.getName());
			Field listaField = getFieldByName(this.getClass(), nomeCampo);

			if (listaField != null && List.class.isAssignableFrom(listaField.getType())) {
				listaField.setAccessible(true);
				try {
					List<?> listaValores = (List<?>) listaField.get(this);
					if (listaValores != null) {
						tamanho = Math.max(tamanho, listaValores.size());
					}
				} catch (IllegalAccessException e) {
					System.err.println("Erro ao acessar lista de valores: " + e.getMessage());
				}
			}
		}
		return tamanho;
	}

	public void extrairCamposMultivalueGenerico() {
		print("Extraindo campos multivalue para " + this.getClass().getSimpleName());

		try {
			for (Class<?> innerClass : this.getClass().getDeclaredClasses()) {
				if (AbstractModelDocMultivalue.class.isAssignableFrom(innerClass)) {
					String prefix = innerClass.getSimpleName().toLowerCase();

					// Obtenha a lista de instâncias da inner class (por exemplo, List<Unidade>)
					String listaFieldName = Utils.addPlurais(prefix);
					Field listaField = getFieldByName(this.getClass(), listaFieldName);

					if (listaField != null && List.class.isAssignableFrom(listaField.getType())) {
						listaField.setAccessible(true);
						List<?> listaDeObjetos = (List<?>) listaField.get(this);

						// Criar listas para armazenar os valores extraídos
						Map<String, List<Object>> valoresMap = new HashMap<>();

						// Iterar sobre cada objeto e extrair os valores dos campos
						for (Object obj : listaDeObjetos) {
							if (obj instanceof AbstractModelDocMultivalue) {
								for (Field innerField : innerClass.getDeclaredFields()) {
									innerField.setAccessible(true);
									String nomeCampo = prefix + Utils.capitalize(innerField.getName());

									// Inicializar a lista para o campo se não existir
									valoresMap.putIfAbsent(nomeCampo, new ArrayList<>());

									// Adicionar o valor à lista correspondente
									Object valor = innerField.get(obj);
									valoresMap.get(nomeCampo).add(valor);
								}
							}
						}

						// Atribuir as listas preenchidas aos campos correspondentes na classe concreta
						for (Map.Entry<String, List<Object>> entry : valoresMap.entrySet()) {
							String campoNome = entry.getKey();
							List<Object> valores = entry.getValue();
							Field campoField = getFieldByName(this.getClass(), campoNome);

							if (campoField != null && List.class.isAssignableFrom(campoField.getType())) {
								campoField.setAccessible(true);
								campoField.set(this, valores);
								print("Campo " + campoNome + " preenchido com " + valores.size() + " valores.");
							} else {
								print("Campo " + campoNome + " não encontrado ou não é uma lista.");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Erro ao extrair campos multivalue de forma genérica: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void adicionarAnexo(UploadedFile anexo) {
		this.uploads.add(anexo);
	}

	public void removerAnexo(String fileName) {
		this.uploads.removeIf(anexo -> anexo.getFileName().equals(fileName));
	}

	@Getter
	@Setter
	public static class UploadedFile {
		private String fileName;
		private byte[] fileData;

		public UploadedFile(String fileName, byte[] fileData) {
			this.fileName = fileName;
			this.fileData = fileData;
		}
	}

	/**
	 * Retorna o nome do banco de dados (database) do Domino associado a esta
	 * classe. Derivado do nome do pacote, assumindo a convenção
	 * 'br.com.tdec.intra.<database>.model'.
	 * 
	 * @return O nome do banco de dados.
	 */
	public String getDatabase() {
		String packageName = this.getClass().getPackageName();
		String[] parts = packageName.split("\\."); // <-- escape duplo

		if (parts.length > 4 && "intra".equalsIgnoreCase(parts[3])) {
			return parts[4];
		}
		System.err.println(
				"WARN: Could not derive database from package name: " + packageName + ". Check naming convention.");
		return null;
	}

}
