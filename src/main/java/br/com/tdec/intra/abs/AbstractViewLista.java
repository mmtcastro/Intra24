package br.com.tdec.intra.abs;

import java.util.stream.Stream;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.LazyDataView;
import com.vaadin.flow.data.value.ValueChangeMode;

public class AbstractViewLista extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	protected AbstractRepository<? extends AbstractModelDoc> repository;
	protected Button criarDocumento;
	protected Grid<AbstractModelDoc> grid;
	protected TextField filterText;
	protected HorizontalLayout toolbar;

	public AbstractViewLista(AbstractRepository<? extends AbstractModelDoc> repository) {
		this.repository = repository;

	}

	public void initGrid() {
		grid = new Grid<>();
		criarDocumento = new Button("Criar Documento", e -> criarDocumento());
		filterText = new TextField();
		filterText.setPlaceholder("filtro...");
		filterText.setClearButtonVisible(true);
		filterText.setValueChangeMode(ValueChangeMode.LAZY);
		filterText.addValueChangeListener(e -> updateList());
		toolbar = new HorizontalLayout(filterText, criarDocumento);
	}

	public void updateList() {
		LazyDataView<AbstractModelDoc> dataView = grid.setItems(q -> captureWildcard(this.repository
				.findAll(q.getOffset(), q.getLimit(), q.getSortOrders(), q.getFilter(), filterText.getValue())
				.stream()));

		dataView.setItemCountEstimate(8000);
	}

	private Stream<AbstractModelDoc> captureWildcard(Stream<? extends AbstractModelDoc> stream) {
		// This casting operation captures the wildcard and returns a stream of
		// AbstractModelDoc
		return (Stream<AbstractModelDoc>) stream;
	}

	public void criarDocumento() {
//		AbstractModelDoc model = repository.createModelDoc();
//		AbstractForm form = repository.createForm(model);
//		form.open();
//		form.addOpenedChangeListener(e -> {
//			if (!e.isOpened()) {
//				if (form.isSaved()) {
//					repository.save(model);
//					updateList();
//				}
//			}
//		});

	}

}
