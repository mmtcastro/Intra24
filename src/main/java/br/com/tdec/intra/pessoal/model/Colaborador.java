package br.com.tdec.intra.pessoal.model;

import com.fasterxml.jackson.annotation.JsonAlias;

import br.com.tdec.intra.abs.AbstractModelDoc;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Colaborador extends AbstractModelDoc {
	@JsonAlias({ "Funcionario", "funcionario" })
	public String funcionario;

}
