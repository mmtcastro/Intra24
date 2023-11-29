package br.com.tdec.intra.empresas.repositories;

import br.com.tdec.intra.abs.AbstractRepository;
import br.com.tdec.intra.config.DominoServer;

public class GrupoEconomicoRepository extends AbstractRepository {

	public GrupoEconomicoRepository(DominoServer dominoServer) {
		super(dominoServer, "empresas.nsf");

	}

}
