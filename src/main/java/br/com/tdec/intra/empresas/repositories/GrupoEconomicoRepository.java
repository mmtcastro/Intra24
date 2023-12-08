package br.com.tdec.intra.empresas.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import br.com.tdec.intra.abs.AbstractRepository;
import br.com.tdec.intra.config.DominoServer;
import br.com.tdec.intra.empresas.model.GrupoEconomico;

@Repository
@Service
public class GrupoEconomicoRepository extends AbstractRepository {

	public GrupoEconomicoRepository(DominoServer dominoServer) {
		super(dominoServer, "empresas.nsf", GrupoEconomico.class);

	}

}
