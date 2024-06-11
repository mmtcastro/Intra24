package br.com.tdec.intra.empresas.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;

import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.empresas.model.Cargo;
import lombok.Getter;
import lombok.Setter;

@Service
@Getter
@Setter
public class CargoService extends AbstractService<Cargo> {

	public CargoService() {
		super(Cargo.class);
	}

	@Override
	public Cargo createModel() {
		return new Cargo();
	}

	@Override
	public SaveResponse save(Cargo model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void edit(Cargo model) {
		// TODO Auto-generated method stub

	}

	@Override
	public DeleteResponse delete(Cargo model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DeleteResponse delete(String unid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Cargo> findAllByCodigo(int offset, int count, List<QuerySortOrder> sortOrders, Optional<Void> filter,
			String search) {
		List<Cargo> ret = new ArrayList<Cargo>();
		count = 50; // nao consegui fazer funcionar o limit automaticamente.
		String direction = "";
		if (sortOrders != null) {
			for (QuerySortOrder sortOrder : sortOrders) {
				System.out.println("--- Sorting ----");
				System.out.println("Sorted: " + sortOrder.getSorted());
				System.out.println("Direction:  " + sortOrder.getDirection());
			}
			if (sortOrders.size() > 0) {
				if (sortOrders.get(0).getDirection() != null && sortOrders.get(0).getDirection().equals("ASCENDING")) {
					direction = "&direction=asc";
				} else {
					direction = "&direction=desc";
				}
			}
		}

		ret = webClient.get()
				.uri("/lists/Cargos?dataSource=" + scope + "&count=" + count + direction + "&column=Codigo&start="
						+ offset + "&startsWith=" + search)
				.header("Authorization", "Bearer " + getUser().getToken()).retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<Cargo>>() {
				})//
				.block();
		return ret;
	}

//	public List<Cargo> findAllByCodigo(int offset, int count, List<QuerySortOrder> sortOrders, Optional<Void> filter,
//			String search) {
//		List<Cargo> ret = new ArrayList<Cargo>();
//		count = 50; // nao consegui fazer funcionar o limit automaticamente.
//		String direction = "";
//		if (sortOrders != null) {
//			for (QuerySortOrder sortOrder : sortOrders) {
//				System.out.println("--- Sorting ----");
//				System.out.println("Sorted: " + sortOrder.getSorted());
//				System.out.println("Direction:  " + sortOrder.getDirection());
//			}
//			if (sortOrders.size() > 0) {
//				if (sortOrders.get(0).getDirection() != null && sortOrders.get(0).getDirection().equals("ASCENDING")) {
//					direction = "&direction=asc";
//				} else {
//					direction = "&direction=desc";
//				}
//			}
//		}
//
//		ret = webClient.get()
//				.uri("/lists/Cargos?dataSource=empresas&count=" + count + direction + "&column=Codigo&start=" + offset
//						+ "&startsWith=" + search)
//				.header("Authorization", "Bearer " + getUser().getToken()).retrieve()
//				.bodyToMono(new ParameterizedTypeReference<List<Cargo>>() {
//				})//
//				.block();
//
//		return ret;
//	}

	public Cargo findByUnid(String unid) {
		Cargo cargo = null;
		try {
			cargo = webClient.get()
					.uri("/document/" + unid + "?dataSource=" + scope
							+ "&computeWithForm=false&richTextAs=markdown&mode=default")
					.header("Authorization", "Bearer " + getUser().getToken()).retrieve().bodyToMono(Cargo.class)
					.block();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cargo;
	}

	public Cargo findByCodigo(String codigo) {

		try {
			String form = this.getClass().getSimpleName().replace("Service", "");
			List<Cargo> models = webClient.get()
					.uri("/lists/_intraCodigos?dataSource=" + scope + "&documents=true&key=" + codigo + "&key=" + form
							+ "&scope=documents")
					.header("Authorization", "Bearer " + getUser().getToken()).retrieve()
					.bodyToMono(new ParameterizedTypeReference<List<Cargo>>() {
					})//
					.block();
			if (models.size() > 0) {
				model = models.get(0);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return model;
	}

}
