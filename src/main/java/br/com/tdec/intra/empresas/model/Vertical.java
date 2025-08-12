package br.com.tdec.intra.empresas.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import br.com.tdec.intra.abs.AbstractModelDoc;
import br.com.tdec.intra.abs.AbstractModelDocMultivalue;
import br.com.tdec.intra.abs.AbstractModelListaMultivalue;
import br.com.tdec.intra.utils.jackson.BodyDeserializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class Vertical extends AbstractModelDoc {
	// private List<String> unidadeResponsavel = new ArrayList<>();
	// private List<String> unidadeEstado = new ArrayList<>();
	// private List<LocalDate> unidadeCriacao = new ArrayList<>();
	// private List<Double> unidadeValor = new ArrayList<>();
	// private List<String> unidadeStatus = new ArrayList<>();
	@JsonIgnore
	private Unidades unidades;
	@JsonAlias({ "body", "Body" })
	@JsonDeserialize(using = BodyDeserializer.class)
	protected RichText body;
	@JsonAlias({ "obs", "Obs" })
	@JsonDeserialize(using = BodyDeserializer.class)
	protected RichText obs;

	public Vertical() {
		super();
		if (getUnidades() == null) {
			setUnidades(new Unidades());
		}
	}

	public int compareTo(AbstractModelDoc outro) {
		if (getCodigo() != null) {
			return getCodigo().compareTo(outro.getCodigo());
		} else {
			return 0;
		}
	}

	public RichText getObs() {
		if (this.obs == null) {
			return new RichText();
		} else {
			return this.obs;
		}
	}

	@Getter
	@Setter
	public static class Unidade extends AbstractModelDocMultivalue {
		@JsonIgnore
		private String responsavel;
		@JsonIgnore
		private String estado;
		@JsonIgnore
		private LocalDate criacao;
		@JsonIgnore
		private Double valor;
		@JsonIgnore
		private String status;
	}

	@Getter
	@Setter
	public static class Unidades extends AbstractModelListaMultivalue<Unidade> {

	}

	public Unidades getUnidades() {
		if (unidades == null) {
			unidades = new Unidades();
		}
		return unidades;
	}

	public void setUnidades(Unidades u) {
		this.unidades = (u != null ? u : new Unidades());
	}

}
