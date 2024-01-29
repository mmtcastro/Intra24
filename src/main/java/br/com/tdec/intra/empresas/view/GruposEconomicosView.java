package br.com.tdec.intra.empresas.view;

import java.util.List;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.empresas.model.GrupoEconomico;
import br.com.tdec.intra.empresas.services.GrupoEconomicoService;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Route(value = "gruposeconomicos", layout = MainLayout.class)
@PageTitle("Grupos Econômicos")
@PermitAll
public class GruposEconomicosView extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	private GrupoEconomicoService grupoEconomicoService;
	private Grid<GrupoEconomico> grid = new Grid<>(GrupoEconomico.class);

	public GruposEconomicosView(GrupoEconomicoService grupoEconomicoService) {
		this.grupoEconomicoService = grupoEconomicoService;

		List<GrupoEconomico> gruposEconomicos = grupoEconomicoService.processGruposEconomicos().block();
		System.out.println("Grupos Economicos: " + gruposEconomicos.size());
		// grid.setItems(gruposEconomicos);
		grid.setItems(query -> grupoEconomicoService.getGruposEconomicos().flux().flatMapIterable(Function.identity())
				.skip(query.getOffset()).take(query.getLimit()).toStream());
		add(grid);

		Button button = new Button("Converte Grupo Economico", event -> convert());
		add(button);
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
