package br.com.tdec.intra.empresas.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.tdec.intra.abs.AbstractModelDoc;
import br.com.tdec.intra.abs.AbstractModelDocMultivalue;
import br.com.tdec.intra.abs.AbstractModelListaMultivalue;
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
