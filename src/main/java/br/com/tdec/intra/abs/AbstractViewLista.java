package br.com.tdec.intra.abs;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.LazyDataView;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;

import br.com.tdec.intra.config.EmailService;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AbstractViewLista extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	@Autowired
	protected EmailService emailService;
	protected AbstractRepository repository;
	protected Grid<AbstractModelDoc> gridDefault;

	public AbstractViewLista(AbstractRepository repository) {
		this.repository = repository;

	}

	public void initGridDefault() {
		setSizeFull();
		gridDefault = new Grid<>();
		gridDefault.setSizeFull();
		Button criarDocumento = new Button("Criar Documento", e -> criarDocumento());

		Column<AbstractModelDoc> codigoColumn = gridDefault.addColumn(AbstractModelDoc::getCodigo).setHeader("Código")
				.setSortable(true);
		codigoColumn.setComparator(Comparator.comparing(AbstractModelDoc::getCodigo)).setKey("codigo");
		Column<AbstractModelDoc> descricaoColumn = gridDefault.addColumn(AbstractModelDoc::getDescricao)
				.setHeader("Descrição");
		Grid.Column<AbstractModelDoc> autorColumn = gridDefault.addColumn(AbstractModelDoc::getAutor)
				.setHeader("Autor");
		autorColumn.setComparator(Comparator.comparing(AbstractModelDoc::getAutor)).setKey("autor");
//		Grid.Column<AbstractModelDoc> criacaoLocalDateTime = gridDefault
//				.addColumn(new LocalDateTimeRenderer<>(AbstractModelDoc::getCriacao, "dd/MM/yyyy HH:mm:ss"))
//				.setHeader("Criação");
		Grid.Column<AbstractModelDoc> criacaoColumn = gridDefault
				.addColumn(new TextRenderer<>(
						item -> item.getCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))))
				.setHeader("Criação");
//		criacaoColumn.setComparator(Comparator.comparing(AbstractModelDoc::getCriacao)).setKey("criacao");
//
//		Grid.Column<Vertical> valorColumn = gridVertical.addColumn(AbstractModelDoc::getValor).setHeader("Valor");
//		valorColumn.setComparator(Comparator.comparing(Vertical::getId)).setKey("valor");

		TextField filterText = new TextField();
		filterText.setPlaceholder("filtro...");
		filterText.setClearButtonVisible(true);
		filterText.setValueChangeMode(ValueChangeMode.LAZY);
		filterText.addValueChangeListener(e -> updateListDefault(gridDefault, filterText));

		gridDefault.asSingleSelect().addValueChangeListener(evt -> editModel(evt.getValue()));

		updateListDefault(gridDefault, filterText);
		HorizontalLayout toolbar = new HorizontalLayout(filterText, criarDocumento);
		add(toolbar, gridDefault);

	}

	public void editModel(AbstractModelDoc abstractModelDoc) {

	}

	/**
	 * Designa um DataView com LazyLoading para o grid
	 */
	public void updateListDefault(Grid<AbstractModelDoc> grid, TextField filterText) {
		System.out.println("Filtro eh " + filterText.getValue());
		LazyDataView<AbstractModelDoc> dataView = grid.setItems(q -> captureWildcard(this.repository
				.findAll(q.getOffset(), q.getLimit(), q.getSortOrders(), q.getFilter(), filterText.getValue())
				.stream()));

		dataView.setItemCountEstimate(8000);
	}

	@SuppressWarnings({ "unchecked" })
	private Stream<AbstractModelDoc> captureWildcard(Stream<? extends AbstractModelDoc> stream) {
		// This casting operation captures the wildcard and returns a stream of
		// AbstractModelDoc - por causa do <E> no AbstractRepository
		return (Stream<AbstractModelDoc>) stream;
	}

	public void criarDocumento() {
//		AbstractModelDoc model = repository.createModelDoc();
//		AbstractForm form = repository.createForm(model);
//		form.open();
//		form.addOpenedChangeListener(e -> {
//			if (!e.isOpened()) {
//				if (form.isSaved()) {
//					repository.save(model);
//					updateList();
//				}
//			}
//		});

	}

	public void sendMail(String from, String sendTo, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(sendTo);
		message.setSubject(subject);
		message.setText(body);
		emailService.emailSender.send(message);

	}

}
