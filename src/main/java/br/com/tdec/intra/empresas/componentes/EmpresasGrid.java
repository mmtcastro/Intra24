package br.com.tdec.intra.empresas.componentes;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import br.com.tdec.intra.empresas.model.Empresa;
import br.com.tdec.intra.empresas.services.GrupoEconomicoService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresasGrid extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	private Grid<Empresa> grid;
	private GrupoEconomicoService grupoEconomicoService;
	private List<Empresa> empresas;
	private String codigoGrupoEconomico;

	public EmpresasGrid(String codigoGrupoEconomico, GrupoEconomicoService grupoEconomicoService) {
		this.grupoEconomicoService = grupoEconomicoService;
		this.codigoGrupoEconomico = codigoGrupoEconomico;
		configureGrid();
		loadEmpresasByGrupoEconomico(codigoGrupoEconomico);

		setWidthFull(); // Garante que o layout ocupe toda a largura
		setPadding(false);
		setSpacing(false);
		setMargin(false);
	}

	private void configureGrid() {
		grid = new Grid<>(Empresa.class, false);
		grid.addThemeVariants(GridVariant.LUMO_COMPACT);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

		// Ocupa toda a largura disponível
		grid.setWidthFull();

		// Permite que a altura seja ajustada dinamicamente
		grid.setHeight(null);

		// Adicionando colunas à Grid
		// grid.addColumn(Empresa::getCodigo).setHeader("Código").setSortable(true).setResizable(true).setAutoWidth(true);
		grid.addColumn(new ComponentRenderer<Anchor, Empresa>(empresa -> {
			Anchor link = new Anchor("empresa/" + empresa.getUnid(), empresa.getCodigo());
			link.getElement().setAttribute("router-link", true); // Permite navegação sem recarregar
			return link;
		})).setHeader("Código").setSortable(true).setResizable(true).setAutoWidth(true);

		grid.addColumn(Empresa::getCliente).setHeader("Razão Social").setResizable(true);
		grid.addColumn(Empresa::getGerenteConta).setHeader("Gerente Conta").setResizable(true);
		grid.addColumn(Empresa::getStatus).setHeader("Status").setResizable(true);
		grid.addColumn(Empresa::getStatusCnpj).setHeader("Status CNPJ").setResizable(true);
		grid.addColumn(Empresa::getSit).setHeader("Status").setResizable(true);
		// grid.addColumn(Empresa::getEstado).setHeader("UF").setResizable(true);
		grid.addColumn(Empresa::getCgc).setHeader("CNPJ").setResizable(true);
		grid.addColumn(Empresa::getAutor).setHeader("Autor").setResizable(true);
		grid.addColumn(
				item -> item.getCriacao() != null ? item.getCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
						: "Não Informado")
				.setHeader("Criação");

		Span titulo = new Span("Empresas do Grupo Econômico");
		titulo.getElement().getStyle().set("font-size", "var(--lumo-font-size-s)");
		titulo.getElement().getStyle().set("color", "var(--lumo-header-text-color)"); // Cor padrão para títulos
		titulo.getStyle().set("margin-bottom", "10px"); // Pequena margem abaixo do título
		titulo.getStyle().set("margin-top", "10px"); // Pequena margem abaixo do título

		// Adicionamos o Grid ao layout (para ser usado corretamente no AbstractViewDoc)
		add(titulo, grid);
	}

	public void loadEmpresasByGrupoEconomico(String codigoGrupoEconomico) {
		empresas = grupoEconomicoService.findEmpresasByGrupoEconomico(codigoGrupoEconomico);
		grid.setItems(empresas);
	}

	public Grid<Empresa> getGrid() {
		return grid;
	}
}
