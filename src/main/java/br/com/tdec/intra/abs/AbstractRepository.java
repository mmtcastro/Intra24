package br.com.tdec.intra.abs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.hcl.domino.db.model.BulkOperationException;
import com.hcl.domino.db.model.Database;
import com.hcl.domino.db.model.Document;
import com.hcl.domino.db.model.OptionalCount;
import com.hcl.domino.db.model.OptionalItemNames;
import com.hcl.domino.db.model.OptionalStart;
import com.vaadin.flow.data.provider.QuerySortOrder;

import br.com.tdec.intra.config.DominoServer;
import br.com.tdec.intra.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Repository
@Service
public abstract class AbstractRepository<E extends AbstractModelDoc> extends Abstract {

	protected String databaseName;
	protected DominoServer dominoServer;
	protected Database database;
	protected final Class<E> modelClass;
	// protected Grid<E> basicGrid;
	// protected TextField filterText;

	// Default constructor
	public AbstractRepository(Class<E> modelClass) {
		this.modelClass = modelClass;
	}

	// Constructor with DominoServer and databaseName
	@Autowired
	public AbstractRepository(DominoServer dominoServer, String databaseName, Class<E> modelClass) {
		this.dominoServer = dominoServer;
		this.databaseName = databaseName;
		this.database = dominoServer.getDatabase(databaseName);
		this.modelClass = modelClass;
	}

//	public void initBasicGrid() {
//		basicGrid = new Grid<>();
//		initFilter();
//		updateList();
//	}
//
//	public void initFilter() {
//		filterText = new TextField();
//		filterText.setPlaceholder("filtro...");
//		filterText.setClearButtonVisible(true);
//		filterText.setValueChangeMode(ValueChangeMode.LAZY);
//		filterText.addValueChangeListener(e -> updateList());
//	}
//
//	public void updateList() {
//		LazyDataView<E> dataView = basicGrid.setItems(
//				q -> this.findAll(q.getOffset(), q.getLimit(), q.getSortOrders(), q.getFilter(), filterText.getValue())
//						.stream());
//
//		dataView.setItemCountEstimate(8000);
//	}

	@SuppressWarnings("unchecked")
	public List<E> findAll(int offset, int limit, List<QuerySortOrder> sortOrders, Optional<Void> filter,
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
		List<E> lista = new ArrayList<E>();
		try {
			// String query = "'_intraForms'.Form = 'GrupoEconomico'";
			String query = "";
			if (sortOrders != null && sortOrders.size() > 0 && sortOrders.get(0).getSorted().equals("codigo")) {
				query = "'_intraForms'.Form = '" + Utils.getFormFromModelClass(modelClass) + "'";
				print("query eh " + query);
			}
			if (search != null && !search.isEmpty()) {
				query = query + " and contains ('" + search + "*')";
			}
			print("query eh " + query);
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
			print("Achei " + docs.size() + " Documentos de GruposEconomicos");

			for (Document doc : docs) {
				lista.add((E) loadModel(doc));
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
			// List<Item<?>> items = doc.getItems();
			model.setId(doc.getItemByName("id").get(0).getValue().toString());
			model.setCodigo(doc.getItemByName("codigo").get(0).getValue().toString());
			model.setNome(doc.getItemByName("nome").get(0).getValue().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return model;
	}

}
