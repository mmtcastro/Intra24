package br.com.tdec.intra.empresas.view;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractViewLista;
import br.com.tdec.intra.empresas.model.Empresa;
import br.com.tdec.intra.empresas.services.EmpresaService;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Getter;
import lombok.Setter;

@PageTitle("Empresas")
@Route(value = "empresas", layout = MainLayout.class)
@PermitAll
@Getter
@Setter
public class EmpresasView extends AbstractViewLista<Empresa> {

	private static final long serialVersionUID = 1L;

	public EmpresasView(EmpresaService service) {
		super(Empresa.class, service);

	}

	public void initGrid() {
		Column<Empresa> codigoColumn = grid.addColumn(Empresa::getCodigo).setHeader("Código").setSortable(true);
		codigoColumn.setComparator(Comparator.comparing(Empresa::getCodigo)).setKey("codigo");
		Column<Empresa> nomeColumn = grid.addColumn(Empresa::getNome).setHeader("Nome");
		Column<Empresa> statusColumn = grid.addColumn(Empresa::getStatus).setHeader("Status");
		Column<Empresa> estadoColumn = grid.addColumn(Empresa::getEstado).setHeader("UF");
		Column<Empresa> cgcColumn = grid.addColumn(Empresa::getCgc).setHeader("CNPJ");
		Grid.Column<Empresa> autorColumn = grid.addColumn(Empresa::getAutor).setHeader("Autor");
		// autorColumn.setComparator(Comparator.comparing(Empresa::getAutor)).setKey("autor");
		Grid.Column<Empresa> criacaoColumn = grid.addColumn(new TextRenderer<>(item -> {
			if (item.getCriacao() != null) {
				return item.getCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			} else {
				return null; // Or any placeholder text you prefer
			}
		})).setHeader("Criação");
	}

}
