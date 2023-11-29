package br.com.tdec.intra.empresas.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Repository;

import com.hcl.domino.db.model.BulkOperationException;
import com.hcl.domino.db.model.Database;
import com.hcl.domino.db.model.Document;
import com.hcl.domino.db.model.OptionalCount;
import com.hcl.domino.db.model.OptionalItemNames;
import com.hcl.domino.db.model.OptionalQueryLimit;
import com.hcl.domino.db.model.OptionalStart;

import br.com.tdec.intra.config.DominoServer;
import br.com.tdec.intra.empresas.model.Empresa;
import lombok.Data;

@Repository
@Data
public class EmpresaRepository {

	private final DominoServer dominoServer;
	private final Database database;

	public EmpresaRepository(DominoServer dominoServer) {
		this.dominoServer = dominoServer;

		this.database = dominoServer.getServer().useDatabase("empresas.nsf");
		System.out.println("EmpresasDao - O database eh " + database.toString() + " deu certo");
		System.out.println(database.toString());

	}

	public List<Empresa> getAllEmpresas() {
		int offset = 0;
		int limit = 100;
		int maxViewEntriesScanned = 100;
		int maxDocumentsScanned = 100;
		int maxMilliSeconds = 10000000;
		var ret = new ArrayList<Document>();
		String query = "'_intraForms'.Form = 'Empresa' and codigo contains ('GERDAU*')";
		List<String> items = List.of("id", "codigo", "nome");

		List<Document> docs;
		try {
			docs = database.readDocuments(query, //
					new OptionalItemNames(items), // tem que usar, senao nao carrega os campos
					new OptionalQueryLimit(maxViewEntriesScanned, maxDocumentsScanned, maxMilliSeconds), //
					new OptionalQueryLimit(100000, 1000000, 400000), new OptionalStart(offset),
					new OptionalCount(limit), new OptionalStart(offset), new OptionalCount(limit)).get();
			System.out.println("Achei " + docs.size() + " Documentos de Empresas");
		} catch (BulkOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
