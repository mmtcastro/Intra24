package br.com.tdec.intra.inter.view;

import com.vaadin.flow.component.Component;

public interface HasTopActions {
	/**
	 * Interface criada para permitir de um AbstractViewDoc coloque um menu ou
	 * botões no topo do cabeçalho, por exemplo "Criar Empresa"
	 * 
	 * @return
	 */
	Component getTopActions(); // pode ser HorizontalLayout, MenuBar, etc.
}
