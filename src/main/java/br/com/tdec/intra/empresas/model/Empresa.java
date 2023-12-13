package br.com.tdec.intra.empresas.model;

import java.time.ZonedDateTime;

import br.com.tdec.intra.abs.AbstractModelDoc;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Empresa extends AbstractModelDoc implements Comparable<AbstractModelDoc> {

	private String telefones;
	private String emailNfeServicos;
	private String emailNfeMateriais;
	private String paginaWeb;
	private String endereco;
	private String numero;
	private String complemento;
	private String bairro;
	private String cidade;
	private String estado;
	private String pais;
	private String cep;
	private String tipoInscricao;
	private String cpf;
	private String cgc;
	private String inscricao;
	private String cnae;
	private String indicadorIE;
	private String ccm;
	private String regimeTributario;
	private String gerenteConta;
	private String vertical;
	private ExcecoesTributarias excecoesTributarias;
	private String membroSuframa;
	private String inscricaoSuframa;
	// private MimeMultipart observacoes;
	// private ArrayList<String> checkCnpj;
	// private Date dataCheckCnpj;
	private String banco;
	private String bancoNumero;
	private String agencia;
	private String agenciaDigito;
	private String conta;
	private String contaDigito;
	// private MimeMultipart obs;

	private Cnpj cnpj; // pensei em guardar o objeto, mas nao vale à pena, já que a consulta é
						// "efêmera", muda sempre, menos o BAIXADO
	private String statusCnpj; // se for "BAIXADO", nao precisa consultar mais, pois nao pode ser alterado por
								// novas consultas
	private ZonedDateTime ultimaConsultaCnpj;

	private Boolean fezNegocios;
	private Boolean temFaturas;
}
