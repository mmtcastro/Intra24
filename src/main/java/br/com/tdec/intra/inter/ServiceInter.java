package br.com.tdec.intra.inter;

import java.util.List;
import java.util.Optional;

import com.vaadin.flow.data.provider.QuerySortOrder;

public interface ServiceInter {

	public List<?> findAllByCodigo(int offset, int count, List<QuerySortOrder> sortOrders, Optional<Void> filter,
			String search);
}
