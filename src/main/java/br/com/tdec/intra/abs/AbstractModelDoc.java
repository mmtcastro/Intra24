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
//@JsonInclude(Include.NON_NULL) // nao mostrar campos nulos na conversao Jackson

public abstract class AbstractModelDoc extends AbstractModel {

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
	@NotNull
	@JsonAlias({ "Id", "id" })
	@NotNull
	protected String id;
	@JsonAlias({ "Uid", "uid" })
	protected String uid;
	@JsonAlias({ "UnidOrigem", "unidOrigem" })
	protected String unidOrigem;
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
	@JsonProperty("Form")
	protected String form;
	@NotNull
	@JsonAlias({ "Codigo", "codigo" })
	protected String codigo;
	@JsonAlias({ "Status", "status" })
	protected String status;
	@JsonAlias({ "Sit", "sit" })
	protected String sit;
	@JsonAlias({ "Criacao", "criacao" })
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	protected ZonedDateTime criacao;
	@JsonAlias({ "Data", "data" })
//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
//	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	protected LocalDate data;
	@JsonAlias({ "autor", "Autor" })
	protected String autor;
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
	protected String revision; // para ver quantas vezes foi revisado e a ultima revisão
//	@JsonAlias({ "body", "Body" })
//	@JsonDeserialize(using = BodyDeserializer.class)
//	protected RichText body;
//	@JsonAlias({ "obs", "Obs" })
//	@JsonDeserialize(using = BodyDeserializer.class)
//	protected RichText obs;
	@JsonAlias({ "fileNames", "$FILES" })
	protected List<String> fileNames;
	protected List<UploadedFile> uploads = new ArrayList<>();
	private List<String> anexosParaExcluir = new ArrayList<>(); // para controle de exclusão de anexos no AbstractView

	public AbstractModelDoc() {

	}

	public void init() {
		try {
			if (this.getMeta() == null) { // novo doc
				this.form = this.getClass().getSimpleName();
				this.id = generateNewModelId();
				this.autor = UtilsSession.getCurrentUserName();
				this.criacao = ZonedDateTime.now();
			} else {
				// preencherCamposMultivalue();
			}
			newRevision();
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

//	private void preencherCamposMultivalue() {
//		print("Preenchendo campos multivalue para " + this.getClass().getSimpleName());
//		try {
//			// Iterar sobre todas as inner classes
//			for (Class<?> innerClass : this.getClass().getDeclaredClasses()) {
//				if (AbstractModelDocMultivalue.class.isAssignableFrom(innerClass)) {
//					String prefix = innerClass.getSimpleName().toLowerCase();
//					Field[] innerFields = innerClass.getDeclaredFields();
//
//					// Obter listas de valores da classe concreta
//					List<Object> listaDeObjetos = new ArrayList<>();
//					int tamanho = obterTamanhoListas(prefix, innerFields);
//
//					// Criar instâncias da inner class e preencher com valores
//					for (int i = 0; i < tamanho; i++) {
//						Object innerInstance = innerClass.getDeclaredConstructor().newInstance();
//
//						for (Field innerField : innerFields) {
//							innerField.setAccessible(true);
//							String nomeCampo = prefix + Utils.capitalize(innerField.getName());
//
//							Field listaField = getFieldByName(this.getClass(), nomeCampo);
//							if (listaField != null && List.class.isAssignableFrom(listaField.getType())) {
//								listaField.setAccessible(true);
//								List<?> listaValores = (List<?>) listaField.get(this);
//
//								if (listaValores != null && i < listaValores.size()) {
//									Object valor = listaValores.get(i);
//									innerField.set(innerInstance, valor);
//								}
//							}
//						}
//						listaDeObjetos.add(innerInstance);
//					}
//
//					// Atribuir a lista preenchida ao campo correspondente na classe concreta
//					String listaFieldName = Utils.addPlurais(prefix);
//					Field listaField = getFieldByName(this.getClass(), listaFieldName);
//					if (listaField != null) {
//						listaField.setAccessible(true);
//						// listaField.set(this, listaDeObjetos);
//						assignListToField(this, listaField, listaDeObjetos, /* pluralHint */ listaFieldName);
//					} else {
//						print("nao achei o campo da lista: " + listaFieldName);
//					}
//				}
//			}
//		} catch (Exception e) {
//			System.err.println("Erro ao preencher campos multivalue: " + e.getMessage());
//			e.printStackTrace();
//		}
//	}

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

}
