package br.com.tdec.intra.empresas.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.QuerySortOrder;

import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.config.WebClientService;
import br.com.tdec.intra.empresas.model.Empresa;
import br.com.tdec.intra.inter.ServiceInter;

@Service
public class EmpresaService extends AbstractService implements ServiceInter<Empresa> {

	public EmpresaService(WebClientService webClientService) {
		super(webClientService);
	}

	@Override
	public List<Empresa> findAllByCodigo(int offset, int count, List<QuerySortOrder> sortOrders, Optional<Void> filter,
			String search) {
		List<Empresa> ret = new ArrayList<Empresa>();
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
				.uri("/lists/Empresas?dataSource=" + scope + "&count=" + count + direction + "&column=Codigo&start="
						+ offset + "&startsWith=" + search)
				.header("Authorization", "Bearer " + getUser().getToken()).retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<Empresa>>() {
				})//
				.block();

		return ret;
	}

	@Override
	public Empresa findByCodigo(String codigo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Empresa findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Empresa findByUnid(String unid) {
		Empresa ret = null;
		try {
			ret = webClient.get()
					.uri("/document/" + unid + "?dataSource=" + scope
							+ "&computeWithForm=false&richTextAs=markdown&mode=default")
					.header("Authorization", "Bearer " + getUser().getToken()).retrieve().bodyToMono(Empresa.class)
					.block();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

}
