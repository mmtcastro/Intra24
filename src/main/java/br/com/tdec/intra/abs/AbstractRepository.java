package br.com.tdec.intra.abs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Repository;

import com.hcl.domino.db.model.BulkOperationException;
import com.hcl.domino.db.model.Database;
import com.hcl.domino.db.model.Document;
import com.hcl.domino.db.model.OptionalCount;
import com.hcl.domino.db.model.OptionalItemNames;
import com.hcl.domino.db.model.OptionalStart;
import com.vaadin.flow.data.provider.QuerySortOrder;

import br.com.tdec.intra.config.DominoServer;
import br.com.tdec.intra.utils.Utils;

@Repository
public abstract class AbstractRepository extends Abstract {

	protected String databaseName;
	protected DominoServer dominoServer;
	protected Database database;
	protected final Class<?> modelClass;

	// Constructor with DominoServer and databaseName
	public AbstractRepository(DominoServer dominoServer, String databaseName, Class<?> modelClass) {
		this.dominoServer = dominoServer;
		this.databaseName = databaseName;
		this.database = dominoServer.getDatabase(databaseName);
		this.modelClass = modelClass;
	}

	@SuppressWarnings("unchecked")
	public List<AbstractModelDoc> findAll(int offset, int limit, List<QuerySortOrder> sortOrders, Optional<Void> filter,
			String search) {
		limit = 50; // nao consegui fazer funcionar o limit automaticamente.
		print("Iniciando findAll com " + offset + " - " + limit);
		print("SortedOrders eh " + sortOrders);
		if (sortOrders != null) {
			for (QuerySortOrder sortOrder : sortOrders) {
				print("--- Sorting ----");
				print("Sorted: " + sortOrder.getSorted());
				print("Direction:  " + sortOrder.getDirection());
			}
		}
		print("Filter eh " + filter);
		List<AbstractModelDoc> lista = new ArrayList<>();
		try {
			// String query = "'_intraForms'.Form = 'GrupoEconomico'";
			String query = "";
			if (sortOrders != null && sortOrders.size() > 0 && sortOrders.get(0).getSorted().equals("codigo")) {
				query = "'_intraForms'.Form = '" + Utils.getFormFromModelClass(modelClass) + "'";
				print("query eh " + query);
			} else {
				print(">>> Problema com o sortOrders. Nao esta ordenando por codigo");
			}
			if (search != null && !search.isEmpty()) {
				query = query + " and contains ('" + search + "*')";
			}

			query = "'_intraForms'.Form = 'Vertical'";
			print(">>>---->>> query eh " + query);
			print("Database eh " + database);
			// List<String> items = new ArrayList<>(Arrays.asList("Codigo", "Nome",
			// "Descricao", "Id", "Tipo", "Criacao"));
			List<String> items = List.of("Codigo");
			List<Document> docs = database.readDocuments(query, //
					new OptionalItemNames(items), // tem que usar, senao nao carrega os campos
					// new OptionalQueryLimit(maxViewEntriesScanned, maxDocumentsScanned,
					// maxMilliSeconds))//
					// new OptionalQueryLimit(1000,1000, 400), new OptionalStart(offset), new
					// OptionalCount(limit))
					new OptionalStart(offset), new OptionalCount(limit)).get();
			print("Achei " + docs.size() + " Documentos");

			for (Document doc : docs) {
				lista.add(loadModel(doc));
			}
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
		return lista;

	}

	public AbstractModelDoc loadModel(Document doc) {
		AbstractModelDoc model = null;
		try {
			model = (AbstractModelDoc) modelClass.getDeclaredConstructor().newInstance();
			// print("Model eh " + model.getClass().getName());
			print(doc.getItemByName("id"));
			// List<Item<?>> items = doc.getItems();
			// model.setId(doc.getItemByName("id").get(0).getValue().toString());
			model.setCodigo("123456");
			// model.setCodigo(doc.getItemByName("codigo").get(0).getValue().toString());
			// model.setNome(doc.getItemByName("nome").get(0).getValue().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return model;
	}

}
