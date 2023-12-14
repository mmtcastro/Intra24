package br.com.tdec.intra.abs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.hcl.domino.db.model.BulkOperationException;
import com.hcl.domino.db.model.Database;
import com.hcl.domino.db.model.Document;
import com.hcl.domino.db.model.Item;
import com.hcl.domino.db.model.ItemValueType;
import com.hcl.domino.db.model.OptionalCount;
import com.hcl.domino.db.model.OptionalItemNames;
import com.hcl.domino.db.model.OptionalStart;
import com.vaadin.flow.data.provider.QuerySortOrder;

import br.com.tdec.intra.config.DominoServer;

@Repository
public abstract class AbstractRepository extends Abstract {

	private static final Logger logger = LoggerFactory.getLogger(AbstractRepository.class);
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

	public List<AbstractModelDoc> findAll(int offset, int limit, List<QuerySortOrder> sortOrders, Optional<Void> filter,
			String search) {
		limit = 50; // nao consegui fazer funcionar o limit automaticamente.
		print("Iniciando findAll com offset: " + offset + " e limit: " + limit);

		print("SortedOrders eh " + sortOrders);
		print("Filter eh " + filter);
		print("Search eh " + search);
		if (sortOrders != null) {
			for (QuerySortOrder sortOrder : sortOrders) {
				print("--- Sorting ----");
				print("Sorted: " + sortOrder.getSorted());
				print("Direction:  " + sortOrder.getDirection());
			}
		}

		List<AbstractModelDoc> lista = new ArrayList<>();

		try {
			String query = "'_intraForms'.Form = '" + modelClass.getSimpleName() + "'";
			;
			if (sortOrders != null && sortOrders.size() > 0 && sortOrders.get(0).getSorted().equals("codigo")) {
				query = "'_intraForms'.Form = '" + modelClass.getSimpleName() + "'";
			} else {
				print(">>> Problema com o sortOrders. Nao esta ordenando por codigo");
			}
			if (search != null && !search.isEmpty()) {
				query = "'_intraForms'.Form = '" + modelClass.getSimpleName() + "'" + " and contains ('" + search
						+ "*')";
			}
			print("query eh " + query);
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

	public AbstractModelDoc loadModel(Document doc) {
		AbstractModelDoc model = null;

		Method method = null;

		List<Item<?>> item;
		ItemValueType itemValueType;
		Class<?> superClass;
		try {
			model = (AbstractModelDoc) modelClass.getDeclaredConstructor().newInstance();

			Map<String, Class<?>> items = model.getAllModelFieldNamesProperCase(); // Notes grava em properCase

			String fieldName;
			fieldName = "stop";

			for (String campo : items.keySet()) {
				item = doc.getItemByName(campo);
				if (item != null && item.get(0) != null && item.get(0).getValue() != null
						&& item.get(0).getValue().size() > 0) {
//					if (item.get(0).getItemValueType() == ItemValueType.TEXT) {
//						fieldClass = items.get(campo); // boolean é string no disco
//					} else if (item.get(0).getItemValueType() == ItemValueType.NUMBER) {
//						fieldClass = Double.class;
//					} else if (item.get(0).getItemValueType() == ItemValueType.DATETIME) {
//						fieldClass = ZonedDateTime.class;
//					} else {
//						fieldClass = String.class;
//					}
					superClass = items.get(campo).getSuperclass();
					fieldName = "set" + campo;
					if (fieldName.equals("setCnpj")) {
						print(fieldName);
					}
					// print(fieldName);
					itemValueType = item.get(0).getItemValueType();
					// print(itemValueType);
					if (items.get(campo).equals(Boolean.class)) {
						method = model.getClass().getMethod("set" + campo, Boolean.class);
						if (item.get(0).getValue().get(0).equals("1")) {
							method.invoke(model, true);
						} else {
							method.invoke(model, false);
						}
					} else if (items.get(campo).equals(List.class) || items.get(campo).equals(ArrayList.class)) {
					} else if (items.get(campo).equals(Set.class) || items.get(campo).equals(TreeSet.class)) {
					} else if (superClass != null && superClass.equals(AbstractModelDoc.class)) {
						print("Doc superClass: " + superClass);
					} else if (superClass != null && superClass.equals(AbstractModelLista.class)) {
						print("Lista superClass: " + superClass);
					} else {
						method = model.getClass().getMethod("set" + campo, items.get(campo));
						method.invoke(model, item.get(0).getValue().get(0));
					}
				}

//			List<Item<?>> codigo = doc.getItemByName("Codigo");
//			List<Item<?>> id = doc.getItemByName("Id");
//
//			model.setId(id != null ? id.get(0).getValue().get(0).toString() : null);
//			model.setCodigo(codigo != null ? codigo.get(0).getValue().get(0).toString() : null);
			}
		} catch (

		Exception e) {
			logger.error("Logger -> loadModel + method: " + method.getName(), e);
			e.printStackTrace();
		}

		return model;
	}

}
