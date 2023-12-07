package br.com.tdec.intra.empresas.view;

import java.util.Comparator;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractViewLista;
import br.com.tdec.intra.empresas.model.Empresa;
import br.com.tdec.intra.empresas.repositories.EmpresaRepository;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Data;
import lombok.EqualsAndHashCode;

@PageTitle("Empresas")
@Route(value = "empresas", layout = MainLayout.class)
@PermitAll
@Data
@EqualsAndHashCode(callSuper = true)
public class EmpresasView extends AbstractViewLista {

	private static final long serialVersionUID = 1L;
	private Grid<Empresa> gridEmpresa;
	private TextField filterText;
	private Button criarEmpresa;

	public EmpresasView(EmpresaRepository repository) {
		super(repository);
		System.out.println(repository.toString());
		add(new H1("Empresas"));

		Button buttonGetAllEmpresas = new Button("getAllEmpresas", e -> repository.getAllEmpresas());
		add(buttonGetAllEmpresas);

		initGridEmpresa();
		initFilter();
		updateList();

		criarEmpresa = new Button("Criar GrupoEconomico", e -> criarEmpresa());
		HorizontalLayout toolbar = new HorizontalLayout(filterText, criarEmpresa);

		add(toolbar, gridEmpresa);

	}

	public void initGridEmpresa() {
		gridEmpresa = new Grid<>();

		Grid.Column<Empresa> codigoColumn = gridEmpresa.addColumn(Empresa::getCodigo).setHeader("Código");
		codigoColumn.setComparator(Comparator.comparing(Empresa::getCodigo)).setKey("codigo");
		// Grid.Column<Empresa> tipoColumn =
		// grid.addColumn(Empresa::getTipo).setHeader("Tipo");
		// tipoColumn.setComparator(Comparator.comparing(Empresa::getTipo)).setKey("tipo");
		// Grid.Column<Empresa> gerenteColumn =
		// grid.addColumn(Empresa::getGerenteConta).setHeader("Gerente");
		// gerenteColumn.setComparator(Comparator.comparing(Empresa::getGerenteConta)).setKey("gerenteConta");

		// Grid.Column<Empresa> descricaoColumn =
		// grid.addColumn(Empresa::getDescricao).setHeader("Descrição");
		// Grid.Column<GrupoEconomico> criacaoColumn =
		// grid.addColumn(GrupoEconomico::getCriacao).setHeader("Criação");
//		Grid.Column<Empresa> criacaoLocalDateTime = grid
//				.addColumn(new LocalDateTimeRenderer<>(Empresa::getCriacaoLocalDateTime, "dd/MM/yyyy HH:mm:ss"))
//				.setHeader("Criação");
//		criacaoLocalDateTime.setComparator(Comparator.comparing(Empresa::getCriacao)).setKey("criacao");

		// grid.asSingleSelect().addValueChangeListener(evt ->
		// editGrupoEconomico(evt.getValue()));

	}

//	public void updateList() {
//		LazyDataView<Empresa> dataView = grid.setItems(q -> repository
//				.findAll(q.getOffset(), q.getLimit(), q.getSortOrders(), q.getFilter(), filterText.getValue())
//				.stream());
//
//		dataView.setItemCountEstimate(8000);
//	}

	public void initFilter() {
		filterText = new TextField();
		filterText.setPlaceholder("filtro...");
		filterText.setClearButtonVisible(true);
		filterText.setValueChangeMode(ValueChangeMode.LAZY);
		filterText.addValueChangeListener(e -> updateList());
	}

	private void criarEmpresa() {
		// TODO Auto-generated method stub

	}

}
