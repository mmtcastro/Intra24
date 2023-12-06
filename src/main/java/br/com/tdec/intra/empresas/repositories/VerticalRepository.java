package br.com.tdec.intra.empresas.repositories;

import br.com.tdec.intra.abs.AbstractRepository;
import br.com.tdec.intra.config.DominoServer;
import br.com.tdec.intra.empresas.model.Vertical;

public class VerticalRepository extends AbstractRepository<Vertical> {

	public VerticalRepository(DominoServer dominoServer) {
		super(dominoServer, "empresas.nsf", Vertical.class);

	}
}
