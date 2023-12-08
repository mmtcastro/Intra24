package br.com.tdec.intra.empresas.repositories;

import org.springframework.stereotype.Repository;

import br.com.tdec.intra.abs.AbstractRepository;
import br.com.tdec.intra.config.DominoServer;
import br.com.tdec.intra.empresas.model.Vertical;

@Repository
public class VerticalRepository extends AbstractRepository {

	public VerticalRepository(DominoServer dominoServer) {
		super(dominoServer, "empresas.nsf", Vertical.class);

	}
}
