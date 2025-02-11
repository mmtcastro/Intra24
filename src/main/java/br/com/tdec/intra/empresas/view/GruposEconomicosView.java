package br.com.tdec.intra.empresas.view;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractViewLista;
import br.com.tdec.intra.empresas.model.GrupoEconomico;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Route(value = "gruposeconomicos", layout = MainLayout.class)
@PageTitle("Grupos Econômicos")
@RolesAllowed("ROLE_EVERYONE")
public class GruposEconomicosView extends AbstractViewLista<GrupoEconomico> {

	private static final long serialVersionUID = 1L;
	// private final GrupoEconomicoService service;
//	//private Grid<GrupoEconomico> grid = new Grid<>(GrupoEconomico.class, false);
//	private Button setGruposEconomicosReactiveButton = new Button("Set Grupos Economicos Reactive");
//	private Button setGrupoEconomicoSyncButton = new Button("Set Grupo Economico Sync");
//	private Button clearButton = new Button("Clear");
//	private TextField count = new TextField("Count");
//	private TextField search = new TextField("Search");
//	private DefaultForm defaultForm;
//	private GrupoEconomico model;

	public GruposEconomicosView() {
		super();
	}

	@SuppressWarnings("unused")
	public void initGrid() {
		// Adiciona a coluna Código (única coluna ordenável)
		Column<GrupoEconomico> codigoColumn = grid.addColumn(GrupoEconomico::getCodigo).setHeader("Código")
				.setSortable(true) // Apenas essa coluna pode ser ordenada
				.setKey("codigo").setComparator(Comparator.comparing(GrupoEconomico::getCodigo));

		// Adiciona a coluna Tipo antes da Descrição (sem ordenação)
		Column<GrupoEconomico> tipoColumn = grid.addColumn(GrupoEconomico::getTipo).setHeader("Tipo").setKey("tipo");

		// Adiciona a coluna Descrição (sem ordenação)
		Column<GrupoEconomico> descricaoColumn = grid.addColumn(GrupoEconomico::getDescricao).setHeader("Descrição")
				.setKey("descricao");

		// Adiciona a coluna Autor (sem ordenação)
		Grid.Column<GrupoEconomico> autorColumn = grid.addColumn(GrupoEconomico::getAutor).setHeader("Autor")
				.setKey("autor");

		// Adiciona a coluna Criação com formatação de data/hora (sem ordenação)
		Grid.Column<GrupoEconomico> criacaoColumn = grid.addColumn(new TextRenderer<>(item -> {
			return (item.getCriacao() != null)
					? item.getCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
					: "Não Informado"; // Evita valores nulos
		})).setHeader("Criação").setKey("criacao");
	}

}
