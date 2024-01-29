package br.com.tdec.intra.empresas.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.empresas.model.GrupoEconomico;
import br.com.tdec.intra.empresas.services.GrupoEconomicoService;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
	private Grid<GrupoEconomico> grid = new Grid<>(GrupoEconomico.class);
	private Button setGruposEconomicosReactiveButton = new Button("Set Grupos Economicos Reactive");
	private Button setGrupoEconomicoSyncButton = new Button("Set Grupo Economico Sync");
	private Button clearButton = new Button("Clear");
	private TextField count = new TextField("Count");

	public GruposEconomicosView(GrupoEconomicoService grupoEconomicoService) {
		setSizeFull();
		grid.setSizeFull();
		this.grupoEconomicoService = grupoEconomicoService;

		// List<GrupoEconomico> gruposEconomicos =
		// grupoEconomicoService.getGruposEconomicosSync();
		// gruposEconomicos.stream().forEach(e-> System.out.println(e.toString()));
		// System.out.println("Grupos Economicos: " + gruposEconomicos.size());
		// grid.addColumn(GrupoEconomico::getCodigo).setHeader("Código");
		// grid.setItems(gruposEconomicos);
	}

	protected void onAttach(AttachEvent attachEvent) {
		if (attachEvent.isInitialAttach()) {
			Button button = new Button("Converte Grupo Economico", event -> convert());
			setGruposEconomicosReactiveButton.addClickListener(event -> setGridValuesReactive());
			setGrupoEconomicoSyncButton.addClickListener(event -> setGridValuesSync());
			clearButton.addClickListener(event -> clearGrid());
			count.addKeyPressListener(Key.ENTER, event -> setGridValuesSync(count.getValue()));

			add(new HorizontalLayout(button, setGruposEconomicosReactiveButton, setGrupoEconomicoSyncButton, clearButton));
			add(new HorizontalLayout(count));
			add(grid);

		}
	}

	private void setGridValuesSync() {
		grid.setItems(grupoEconomicoService.getGruposEconomicosSync());
	}
	
	private void setGridValuesSync(String count) {
		grid.setItems(grupoEconomicoService.getGruposEconomicosSync(count));
	}

	private void setGridValuesReactive() {
		UI ui = getUI().get();
		System.out.println(grupoEconomicoService.getGruposEconomicosReactive().toString());
		grupoEconomicoService.getGruposEconomicosReactive()
	    .subscribe(e -> System.out.println(e.toString()));
		grupoEconomicoService.getGruposEconomicosReactive()
				.subscribe(gruposEconomicos -> ui.access(() -> grid.setItems(gruposEconomicos)));
	}
	
	private void clearGrid() {
		grid.setItems(Collections.emptyList());
	}
	

	private GrupoEconomico convert() {
		String grupo = "{\"@unid\":\"BC4E36F3BBE69F8C832580AE00631ECE\",\"@noteid\":149438,\"@index\":\"9\",\"Codigo\":\"2NET\",\"Tipo\":\"Cliente\",\"Status\":\"Ativo\",\"QuantNegocios\":\"4.0\",\"Criacao\":\"2000-05-29T16:56:54-03:00\",\"DataUltimoNegocio\":\"2000-07-18T16:46:02-03:00\",\"GerenteConta\":\"Cristina Serra\",\"ParceriaPrimeiroNegocio\":\"TDec\",\"Autor\":\"Marcelo Castro\",\"Descricao\":\"\"}";
		ObjectMapper objectMapper = new ObjectMapper();
		GrupoEconomico grupoEconomico = null;
		try {
			grupoEconomico = objectMapper.readValue(grupo, GrupoEconomico.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return grupoEconomico;
	}

}
