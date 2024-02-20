package br.com.tdec.intra.empresas.view;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.LazyDataView;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractModelDoc;
import br.com.tdec.intra.components.DefaultForm;
import br.com.tdec.intra.empresas.model.GrupoEconomico;
import br.com.tdec.intra.empresas.services.GrupoEconomicoService;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Route(value = "gruposeconomicos", layout = MainLayout.class)
@PageTitle("Grupos Econômicos")
@PermitAll
public class GruposEconomicosView extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	private GrupoEconomicoService grupoEconomicoService;
	private Grid<GrupoEconomico> grid = new Grid<>(GrupoEconomico.class, false);
	private Button setGruposEconomicosReactiveButton = new Button("Set Grupos Economicos Reactive");
	private Button setGrupoEconomicoSyncButton = new Button("Set Grupo Economico Sync");
	private Button clearButton = new Button("Clear");
	private TextField count = new TextField("Count");
	private TextField search = new TextField("Search");
	private DefaultForm defaultForm;
	private GrupoEconomico model;

	public GruposEconomicosView(GrupoEconomicoService grupoEconomicoService) {
		// setSizeFull();
		this.grupoEconomicoService = grupoEconomicoService;
		setGrid();
		updateGrid(grid, "");
		defaultForm = new DefaultForm(grid);
		defaultForm.setWidth("25cm");
		add(getDefaultContent());
		defaultForm.closeEditor();
	}

	private Component getDefaultContent() {
		HorizontalLayout content = new HorizontalLayout(grid, defaultForm);
		content.setFlexGrow(2, grid);
		content.setFlexGrow(1, defaultForm);
		content.addClassNames("gruposEconomicos-view-lista-content");
		content.setSizeFull();
		return content;
	}

	private void setGrid() {
		Column<GrupoEconomico> codigoColumn = grid.addColumn(GrupoEconomico::getCodigo).setHeader("Código")
				.setSortable(true);
		codigoColumn.setComparator(Comparator.comparing(GrupoEconomico::getCodigo)).setKey("codigo");
		Column<GrupoEconomico> descricaoColumn = grid.addColumn(GrupoEconomico::getDescricao).setHeader("Descrição");
		Grid.Column<GrupoEconomico> autorColumn = grid.addColumn(GrupoEconomico::getAutor).setHeader("Autor");
		Grid.Column<GrupoEconomico> criacaoColumn = grid.addColumn(new TextRenderer<>(item -> {
			if (item.getCriacao() != null) {
				return item.getCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
			} else {
				return null; // Or any placeholder text you prefer
			}
		})).setHeader("Criação");

		// grid.asSingleSelect().addValueChangeListener(evt ->
		// editModel(evt.getValue()));

		grid.asSingleSelect().addValueChangeListener(evt -> openPage(evt.getValue()));

	}

	public void openPage(GrupoEconomico grupoEconomico) {
		getUI().ifPresent(ui -> ui.navigate("grupoeconomico/" + grupoEconomico.getUnid()));
	}

	public void editModel(GrupoEconomico model) {
		this.model = model;
		if (model == null) {
			defaultForm.closeFormDefault();
		} else {
			defaultForm.setModel(model);
			defaultForm.setVisible(true);
			addClassName("abstract-view-lista-editing");
		}
	}

	protected void onAttach(AttachEvent attachEvent) {
		if (attachEvent.isInitialAttach()) {
			Button button = new Button("Converte Grupo Economico", event -> convert());
			// setGruposEconomicosReactiveButton.addClickListener(event ->
			// setGridValuesReactive());
			setGrupoEconomicoSyncButton.addClickListener(event -> setGridValuesSync());
			clearButton.addClickListener(event -> clearGrid());
			count.addKeyPressListener(Key.ENTER, event -> setGridValuesSync(count.getValue()));
			search.setPlaceholder("buscar...");
			search.setClearButtonVisible(true);
			search.setValueChangeMode(ValueChangeMode.LAZY);
			search.addValueChangeListener(e -> updateGrid(grid, search.getValue()));

			add(new HorizontalLayout(button, setGruposEconomicosReactiveButton, setGrupoEconomicoSyncButton,
					clearButton));
			add(new HorizontalLayout(count, search));
			add(grid);

		}
	}

	private void setGridValuesSync() {
		grid.setItems(grupoEconomicoService.getGruposEconomicosSync());
	}

	private void setGridValuesSync(String count) {
		grid.setItems(grupoEconomicoService.getGruposEconomicosSync(count));
	}

//	private void setGridValuesReactive() {
//		UI ui = getUI().get();
//		System.out.println(grupoEconomicoService.getGruposEconomicosReactive().toString());
//		grupoEconomicoService.getGruposEconomicosReactive().subscribe(e -> System.out.println(e.toString()));
//		grupoEconomicoService.getGruposEconomicosReactive()
//				.subscribe(gruposEconomicos -> ui.access(() -> grid.setItems(gruposEconomicos)));
//	}

	private void clearGrid() {
		grid.setItems(Collections.emptyList());
	}

	private GrupoEconomico convert() {
		String grupo = "{\"@unid\":\"BC4E36F3BBE69F8C832580AE00631ECE\",\"@noteid\":149438,\"@index\":\"9\",\"Codigo\":\"2NET\",\"Tipo\":\"Cliente\",\"Status\":\"Ativo\",\"QuantNegocios\":\"4.0\",\"Criacao\":\"2000-05-29T16:56:54-03:00\",\"DataUltimoNegocio\":\"2000-07-18T16:46:02-03:00\",\"GerenteConta\":\"Cristina Serra\",\"ParceriaPrimeiroNegocio\":\"TDec\",\"Autor\":\"Marcelo Castro\",\"Descricao\":\"\"}";
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		GrupoEconomico grupoEconomico = null;
		try {
			grupoEconomico = objectMapper.readValue(grupo, GrupoEconomico.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return grupoEconomico;
	}

	public void updateGrid(Grid<GrupoEconomico> grid, String searchText) {
		System.out.println("Search eh " + searchText);
		LazyDataView<GrupoEconomico> dataView = grid.setItems(q -> captureWildcard(this.grupoEconomicoService
				.findAllByCodigo(q.getOffset(), q.getLimit(), q.getSortOrders(), q.getFilter(), searchText).stream()));

		dataView.setItemCountEstimate(8000);
	}

	@SuppressWarnings("unchecked")
	private Stream<GrupoEconomico> captureWildcard(Stream<? extends AbstractModelDoc> stream) {
		// This casting operation captures the wildcard and returns a stream of
		// AbstractModelDoc - por causa do <E> no AbstractRepository
		return (Stream<GrupoEconomico>) stream;
	}

}
