package br.com.tdec.intra.abs;

import java.time.ZonedDateTime;
import java.util.TreeSet;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public abstract class AbstractModelDoc extends AbstractModel {

	protected String id;
	protected String uid;
	protected String idOrigem;
	protected String codigoOrigem;
	protected String idRaiz;
	protected String codigoRaiz;
	protected String campoOrigem; // utilizado por AbstractModelLista para saber em qual campo fazer o load da
									// lista (saveModel)
	protected String idNegocio;
	protected String codigoNegocio;
	protected String form;
	@NotNull
	protected String codigo;
	protected String status;
	protected String sit;
	protected ZonedDateTime data;
	protected String autor;
	protected ZonedDateTime criacao;
	protected String responsavel;
	protected String area;
	protected String nome;
	protected String descricao;
	protected ZonedDateTime dataMudancaStatus;
	protected String responsavelMudancaStatus;
	protected String tipo;
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
