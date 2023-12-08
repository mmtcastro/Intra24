package br.com.tdec.intra.empresas.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.hcl.domino.db.model.BulkOperationException;
import com.hcl.domino.db.model.Document;
import com.hcl.domino.db.model.OptionalCount;
import com.hcl.domino.db.model.OptionalItemNames;
import com.hcl.domino.db.model.OptionalQueryLimit;
import com.hcl.domino.db.model.OptionalStart;
import com.vaadin.flow.data.provider.QuerySortOrder;

import br.com.tdec.intra.abs.AbstractModelDoc;
import br.com.tdec.intra.abs.AbstractRepository;
import br.com.tdec.intra.config.DominoServer;
import br.com.tdec.intra.empresas.model.Empresa;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Repository
@Data
@EqualsAndHashCode(callSuper = false)
public class EmpresaRepository extends AbstractRepository implements PagingAndSortingRepository<Empresa, String> {

	public EmpresaRepository(DominoServer dominoServer) {
		super(dominoServer, "empresas.nsf", Empresa.class);
	}

	public List<Empresa> getAllEmpresas() {
		int offset = 0;
		int limit = 100;
		int maxViewEntriesScanned = 100;
		int maxDocumentsScanned = 100;
		int maxMilliSeconds = 10000000;
		String query = "'_intraForms'.Form = 'Empresa' and codigo contains ('GERDAU*')";
		List<String> items = List.of("id", "codigo", "nome");

		List<Document> docs;
		List<Empresa> empresas = new ArrayList<Empresa>();

		try {
			docs = database.readDocuments(query, //
					new OptionalItemNames(items), // tem que usar, senao nao carrega os campos
					new OptionalQueryLimit(maxViewEntriesScanned, maxDocumentsScanned, maxMilliSeconds), //
					new OptionalQueryLimit(100000, 1000000, 400000), new OptionalStart(offset),
					new OptionalCount(limit), new OptionalStart(offset), new OptionalCount(limit)).get();
			System.out.println("Achei " + docs.size() + " Documentos de Empresas");
			for (Document doc : docs) {
				Empresa model = (Empresa) loadModel(doc);
				empresas.add(model);
				System.out.println(model.getCodigo().toString());
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

		return empresas;
	}

	@Override
	public List<AbstractModelDoc> findAll(int offset, int limit, List<QuerySortOrder> sortOrders, Optional<Void> filter,
			String search) {
		limit = 50;
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
		List<AbstractModelDoc> empresas = new ArrayList<>();
		try {
			String query = "'_intraForms'.Form = 'Empresa'";
			if (sortOrders != null && sortOrders.size() > 0 && sortOrders.get(0).getSorted().equals("codigo")) {
				query = "'_intraForms'.Form = 'GrupoEconomico'";
			}
			if (search != null && !search.isEmpty()) {
				query = query + " and contains ('" + search + "*')";
			}
			print("query eh " + query);
			print("Database eh " + database);
//			List<String> items = new ArrayList<>(
//					Arrays.asList("Codigo", "Nome", "GerenteConta", "Descricao", "Id", "Tipo", "Criacao"));
			List<String> items = List.of("id", "codigo", "nome");
			List<Document> docs = database.readDocuments(query, //
					new OptionalItemNames(items), // tem que usar, senao nao carrega os campos
					// new OptionalQueryLimit(maxViewEntriesScanned, maxDocumentsScanned,
					// maxMilliSeconds))//
					// new OptionalQueryLimit(1000,1000, 400), new OptionalStart(offset), new
					// OptionalCount(limit))
					new OptionalStart(offset), new OptionalCount(limit)).get();
			print("Achei " + docs.size() + " Documentos de GruposEconomicos");

			for (Document doc : docs) {
				empresas.add((Empresa) loadModel(doc));
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
		return empresas;
	}

	@Override
	public Page<Empresa> findAll(Pageable pageable) {
		List<Empresa> empresas = getAllEmpresas();
		int pageSize = 10; // Define the page size
		int totalElements = empresas.size(); // Calculate the total number of elements

		int pageNumber = 0; // Set the current page number to 0 (first page)

		Pageable pag = PageRequest.of(pageNumber, pageSize);

		Page<Empresa> page = new PageImpl<>(empresas, pag, totalElements);

		return page;
	}

	@Override
	public Iterable<Empresa> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	public Empresa loadModel(Document doc) {
		Empresa model = null;
		try {
			print("Este eh o codigo " + doc.getItemByName("codigo").get(0).getValue().toString());
			model = new Empresa();
			// List<Item<?>> items = doc.getItems();
			model.setId(doc.getItemByName("id").get(0).getValue().toString());
			model.setCodigo(doc.getItemByName("codigo").get(0).getValue().toString());
			// model.setNome(doc.getItemByName("nome").get(0).getValue().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return model;
	}

}
