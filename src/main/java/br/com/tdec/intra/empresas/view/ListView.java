package br.com.tdec.intra.empresas.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.views.MainLayout;

import java.io.Serial;

@PageTitle("ListView")
@Route(value = "listview", layout = MainLayout.class)
public class ListView extends VerticalLayout {

    @Serial
    private static final long serialVersionUID = 1L;
	
	public ListView() {
		new Button("Click me!");
	}

}
