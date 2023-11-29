package br.com.tdec.intra.abs;

import org.springframework.beans.factory.annotation.Autowired;

import com.hcl.domino.db.model.Database;

import br.com.tdec.intra.config.DominoServer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractRepository extends Abstract {

	protected String databaseName;
	protected DominoServer dominoServer;
	protected Database database;

	// Default constructor
	public AbstractRepository() {
		// Initialize default values if needed
	}

	// Constructor with DominoServer and databaseName
	@Autowired
	public AbstractRepository(DominoServer dominoServer, String databaseName) {
		this.dominoServer = dominoServer;
		this.databaseName = databaseName;
		this.database = dominoServer.getDatabase(databaseName);
	}

}
