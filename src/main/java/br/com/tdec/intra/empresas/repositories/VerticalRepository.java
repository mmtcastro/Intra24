package br.com.tdec.intra.empresas.repositories;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Repository;

import com.hcl.domino.db.model.BulkOperationException;
import com.hcl.domino.db.model.Document;
import com.hcl.domino.db.model.OptionalCount;
import com.hcl.domino.db.model.OptionalItemNames;
import com.hcl.domino.db.model.OptionalStart;
import com.vaadin.flow.data.provider.QuerySortOrder;

import br.com.tdec.intra.abs.AbstractModelDoc;
import br.com.tdec.intra.abs.AbstractRepository;
import br.com.tdec.intra.config.DominoServer;
import br.com.tdec.intra.empresas.model.Vertical;

@Repository
public class VerticalRepository extends AbstractRepository {

	public VerticalRepository(DominoServer dominoServer) {
		super(dominoServer, "empresas.nsf", Vertical.class);

	}

	public List<Vertical> findAllVerticais(int offset, int limit, List<QuerySortOrder> sortOrders,
			Optional<Void> filter, String search) {
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
		List<Vertical> lista = new ArrayList<>();
		try {
			String query = "";
			if (sortOrders != null && sortOrders.size() > 0 && sortOrders.get(0).getSorted().equals("codigo")) {
				query = "'_intraForms'.Form = '" + modelClass.getSimpleName() + "'";
				print("query eh " + query);
			} else {
				print(">>> Problema com o sortOrders. Nao esta ordenando por codigo");
			}
			if (search != null && !search.isEmpty()) {
				query = query + " and contains ('" + search + "*')";
			}

			query = "'_intraForms'.Form = '" + modelClass.getSimpleName() + "'";

			AbstractModelDoc model = (AbstractModelDoc) modelClass.getDeclaredConstructor().newInstance();
			List<String> items = new ArrayList<String>(model.getAllModelFieldNamesProperCase().keySet());

			List<Document> docs = database.readDocuments(query, new OptionalItemNames(items), // tem que usar, senao nao
																								// carrega os campos
					// new OptionalQueryLimit(maxViewEntriesScanned, maxDocumentsScanned,
					// maxMilliSeconds))//
					// new OptionalQueryLimit(1000,1000, 400), new OptionalStart(offset), new
					// OptionalCount(limit))
					new OptionalStart(offset), new OptionalCount(limit)).get();
			print("Achei " + docs.size() + " Documentos");

			for (Document doc : docs) {
				// doc.getItems().add(new TextItem("Valor", "{#100.0}"));
				lista.add((Vertical) loadVertical(doc));
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
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lista;

	}

	private Vertical loadVertical(Document doc) {
		Vertical model = new Vertical();
		List<com.hcl.domino.db.model.Item<?>> codigo = doc.getItemByName("Codigo");
		List<com.hcl.domino.db.model.Item<?>> id = doc.getItemByName("Id");

		model.setId(id != null ? id.get(0).getValue().get(0).toString() : null);
		model.setCodigo(codigo != null ? codigo.get(0).getValue().get(0).toString() : null);
		return model;
	}
}
