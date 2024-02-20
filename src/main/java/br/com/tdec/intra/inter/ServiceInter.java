package br.com.tdec.intra.inter;

import java.util.List;
import java.util.Optional;

import com.vaadin.flow.data.provider.QuerySortOrder;

import br.com.tdec.intra.abs.AbstractModel;

public interface ServiceInter<T extends AbstractModel> {

	public List<T> findAllByCodigo(int offset, int count, List<QuerySortOrder> sortOrders, Optional<Void> filter,
			String search);

	public T findByCodigo(String codigo);

	public T findById(String id);

	public T findByUnid(String unid);

}
