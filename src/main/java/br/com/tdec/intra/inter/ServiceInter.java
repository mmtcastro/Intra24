package br.com.tdec.intra.inter;

import java.util.List;
import java.util.Optional;

import com.vaadin.flow.data.provider.QuerySortOrder;

import br.com.tdec.intra.abs.AbstractModel;

public interface ServiceInter<M extends AbstractModel> {

	public List<M> findAllByCodigo(int offset, int count, List<QuerySortOrder> sortOrders, Optional<Void> filter,
			String search);

	public M findByCodigo(String codigo);

	public M findById(String id);

	public M findByUnid(String unid);

}
