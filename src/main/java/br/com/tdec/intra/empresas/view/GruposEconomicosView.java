package br.com.tdec.intra.empresas.view;

import java.time.format.DateTimeFormatter;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.data.renderer.ComponentRenderer;
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

	public GruposEconomicosView() {
		super();
	}

	@SuppressWarnings("unused")
	public void initGrid() {
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		// Adiciona a coluna Código (única coluna ordenável)
		grid.addColumn(new ComponentRenderer<Anchor, GrupoEconomico>(grupoEconomico -> {
			Anchor link = new Anchor("grupoeconomico/" + grupoEconomico.getUnid(), grupoEconomico.getCodigo());
			link.getElement().setAttribute("router-link", true); // Permite navegação sem recarregar
			return link;
		})).setHeader("Código").setSortable(true).setResizable(true).setAutoWidth(true);

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
