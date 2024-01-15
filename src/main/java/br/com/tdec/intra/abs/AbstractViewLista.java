package br.com.tdec.intra.abs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.LazyDataView;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;

import br.com.tdec.intra.config.EmailService;
import br.com.tdec.intra.utils.Utils;
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
	protected FormLayout formLayoutDefault;
	protected AbstractViewDoc form;
	protected AbstractModelDoc model;
	// protected FormLayout form = new FormLayout();

	public AbstractViewLista(AbstractRepository repository) {
		this.repository = repository;

	}

	public void initGridDefault() {
		setSizeFull();
		gridDefault = new Grid<>();
		gridDefault.setSizeFull();
		formLayoutDefault = new FormLayout();

		// initFormDefault();

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
		Grid.Column<AbstractModelDoc> criacaoColumn = gridDefault.addColumn(new TextRenderer<>(item -> {
			if (item.getCriacao() != null) {
				return item.getCriacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
			} else {
				return null; // Or any placeholder text you prefer
			}
		})).setHeader("Criação");
//		criacaoColumn.setComparator(Comparator.comparing(AbstractModelDoc::getCriacao)).setKey("criacao");
//
//		Grid.Column<Vertical> valorColumn = gridVertical.addColumn(AbstractModelDoc::getValor).setHeader("Valor");
//		valorColumn.setComparator(Comparator.comparing(Vertical::getId)).setKey("valor");

		TextField searchText = new TextField();
		searchText.setPlaceholder("buscar...");
		searchText.setClearButtonVisible(true);
		searchText.setValueChangeMode(ValueChangeMode.LAZY);
		searchText.addValueChangeListener(e -> updateListDefault(gridDefault, searchText.getValue()));

		updateListDefault(gridDefault, searchText.getValue());
		HorizontalLayout toolbar = new HorizontalLayout(searchText, criarDocumento);
		// add(toolbar, gridDefault, formDefault);
		add(toolbar, getContent(), formLayoutDefault);
		formLayoutDefault.setVisible(false);

		gridDefault.asSingleSelect().addValueChangeListener(evt -> editModel(evt.getValue()));

	}

	public void initDefaultForm(AbstractModelDoc model) {
		// formDefault = new FormLayout();
		System.out.println(model.getCodigo());
		try {
			Class<?> classForm = Utils.getViewDocClassFromViewListaClass(this.getClass());
//			Constructor<?> constructor = classForm.getDeclaredConstructor(AbstractModelDoc.class);
//			form = (AbstractViewDoc) constructor.newInstance(model);
			Constructor<?> constructor = classForm.getDeclaredConstructor();
			form = (AbstractViewDoc) constructor.newInstance();
			form.setModel(model);
			formLayoutDefault.setVisible(true);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private HorizontalLayout getContent() {
		HorizontalLayout content = new HorizontalLayout(gridDefault, formLayoutDefault);
		content.setFlexGrow(2, gridDefault);
		content.setFlexGrow(1, formLayoutDefault);
		content.addClassNames("content");
		content.setSizeFull();
		return content;
	}

	public void editModel(AbstractModelDoc model) {

		if (model == null) {
			closeFormDefault();
		} else {

			initDefaultForm(model);
			formLayoutDefault.setVisible(true);
		}

	}

	/**
	 * Designa um DataView com LazyLoading para o grid
	 */
	public void updateListDefault(Grid<AbstractModelDoc> grid, String searchText) {
		System.out.println("Search eh " + searchText);
		LazyDataView<AbstractModelDoc> dataView = grid.setItems(q -> captureWildcard(this.repository
				.findAll(q.getOffset(), q.getLimit(), q.getSortOrders(), q.getFilter(), searchText).stream()));

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

	public void closeFormDefault() {
		removeClassName("editing");
		formLayoutDefault.setVisible(false);
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
