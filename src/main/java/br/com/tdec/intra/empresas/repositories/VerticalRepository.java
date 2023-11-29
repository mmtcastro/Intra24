package br.com.tdec.intra.empresas.repositories;

import br.com.tdec.intra.abs.AbstractRepository;
import br.com.tdec.intra.config.DominoServer;

public class VerticalRepository extends AbstractRepository {

	public VerticalRepository(DominoServer dominoServer) {
		super(dominoServer, "empresas.nsf");

	}
}
