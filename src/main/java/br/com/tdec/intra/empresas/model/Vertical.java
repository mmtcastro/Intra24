package br.com.tdec.intra.empresas.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import br.com.tdec.intra.abs.AbstractModelDoc;
import br.com.tdec.intra.abs.AbstractModelDocMultivalue;
import br.com.tdec.intra.abs.AbstractModelListaMultivalue;
import br.com.tdec.intra.utils.jackson.BodyDeserializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public class Vertical extends AbstractModelDoc {
	private List<String> unidadeEstado;
	private List<LocalDate> unidadeCriacao;
	private List<Double> unidadeValor;
	@JsonIgnore
	private List<Unidade> unidades;
	@JsonAlias({ "body", "Body" })
	@JsonDeserialize(using = BodyDeserializer.class)
	protected RichText body;
	@JsonAlias({ "obs", "Obs" })
	@JsonDeserialize(using = BodyDeserializer.class)
	protected RichText obs;

	public Vertical() {
		super();
		if (getUnidades() == null) {
			setUnidades(new ArrayList<>());
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
		private String estado;
		@JsonIgnore
		private LocalDate criacao;
		@JsonIgnore
		private Double valor;
	}

	@Getter
	@Setter
	public static class Unidades extends AbstractModelListaMultivalue<Unidade> {

	}

}
