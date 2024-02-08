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
import br.com.tdec.intra.config.MailService;
import br.com.tdec.intra.empresas.model.Cargo;
import br.com.tdec.intra.empresas.services.CargoService;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Data;
import lombok.EqualsAndHashCode;

@PageTitle("Cargos")
@Route(value = "cargos", layout = MainLayout.class)
//@PermitAll
@RolesAllowed("ROLE_EVERYONE")
@Data
@EqualsAndHashCode(callSuper = false)
public class CargosView extends AbstractViewLista {

	private static final long serialVersionUID = 1L;
	private CargoService service;
	private MailService mailService;
	private Grid<Cargo> grid = new Grid<>(Cargo.class, false);

	public CargosView(CargoService service, MailService mailService) {
		setSizeFull();
		this.service = service;
		this.mailService = mailService;
		Button sendMailButton = new Button("Send Mail", e -> sendMail());
		setGrid();
		updateGrid(grid, "");
		add(sendMailButton, grid);
	}

	private void sendMail() {
		mailService.sendSimpleMessage("mcastro@tdec.com.br", "Teste", "Conteudo de mensagem em texto simples.");

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

		grid.asSingleSelect().addValueChangeListener(evt -> openPageCargo(evt.getValue()));
	}

	private void openPageCargo(Cargo cargo) {
		getUI().ifPresent(ui -> ui.navigate("cargo/" + cargo.getUnid()));
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

}
