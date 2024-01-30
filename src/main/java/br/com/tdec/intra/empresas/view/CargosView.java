package br.com.tdec.intra.empresas.view;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.stream.Stream;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.data.provider.LazyDataView;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractModelDoc;
import br.com.tdec.intra.abs.AbstractViewLista;
import br.com.tdec.intra.empresas.model.Cargo;
import br.com.tdec.intra.empresas.services.CargoService;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Data;
import lombok.EqualsAndHashCode;

@PageTitle("Cargos")
@Route(value = "cargos", layout = MainLayout.class)
@PermitAll
@Data
@EqualsAndHashCode(callSuper = false)
public class CargosView extends AbstractViewLista {

	private static final long serialVersionUID = 1L;
	private CargoService service;
	private Grid<Cargo> grid = new Grid<>(Cargo.class, false);

	public CargosView(CargoService service) {
		setSizeFull();
		this.service = service;
		Button sendMailButton = new Button("Send Mail", e -> sendMail());
		setGrid();
		updateGrid(grid, "");
		add(sendMailButton, grid);
	}

	private void setGrid() {
		grid.setSizeFull();
		Column<Cargo> codigoColumn = grid.addColumn(Cargo::getCodigo).setHeader("Código").setSortable(true);
		codigoColumn.setComparator(Comparator.comparing(Cargo::getCodigo)).setKey("codigo");
		Column<Cargo> descricaoColumn = grid.addColumn(Cargo::getDescricao).setHeader("Descrição");
		Grid.Column<Cargo> autorColumn = grid.addColumn(Cargo::getAutor).setHeader("Autor");
		// autorColumn.setComparator(Comparator.comparing(Cargo::getAutor)).setKey("autor");
		Grid.Column<Cargo> criacaoColumn = grid.addColumn(new TextRenderer<>(item -> {
			if (item.getCriacao() != null) {
				return item.getCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			} else {
				return null; // Or any placeholder text you prefer
			}
		})).setHeader("Criação");
	}

	public void updateGrid(Grid<Cargo> grid, String searchText) {
		System.out.println("Search eh " + searchText);
		LazyDataView<Cargo> dataView = grid.setItems(q -> captureWildcard(this.service
				.findAllByCodigo(q.getOffset(), q.getLimit(), q.getSortOrders(), q.getFilter(), searchText).stream()));

		dataView.setItemCountEstimate(8000);
	}

	@SuppressWarnings("unchecked")
	private Stream<Cargo> captureWildcard(Stream<? extends AbstractModelDoc> stream) {
		// This casting operation captures the wildcard and returns a stream of
		// AbstractModelDoc - por causa do <E> no AbstractRepository
		return (Stream<Cargo>) stream;
	}

	public void sendMail() {
		// sendMail("mcastro@tdec.com.br", "mcastro@tdec.com.br", "Subject Teste", "Body
		// Teste");
		emailService.sendSimpleMessage("mcastro@tdec.com.br", "mcastro@tdec.com.br", "Subject Teste", "Body Teste");
	}

}
