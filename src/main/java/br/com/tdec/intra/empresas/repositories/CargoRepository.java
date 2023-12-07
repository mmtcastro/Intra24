package br.com.tdec.intra.empresas.repositories;

import org.springframework.stereotype.Repository;

import br.com.tdec.intra.abs.AbstractRepository;
import br.com.tdec.intra.config.DominoServer;
import br.com.tdec.intra.empresas.model.Cargo;

@Repository
public class CargoRepository extends AbstractRepository<Cargo> {

	public CargoRepository(DominoServer dominoServer) {
		super(dominoServer, "empresas.nsf", Cargo.class);
	}

}
