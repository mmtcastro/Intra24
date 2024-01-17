package br.com.tdec.intra.abs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.LazyDataView;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;

import br.com.tdec.intra.config.EmailService;
import br.com.tdec.intra.utils.Utils;
import br.com.tdec.intra.utils.UtilsConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AbstractViewLista extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	@Autowired
	protected EmailService emailService;
	protected AbstractRepository repository;
	protected Grid<AbstractModelDoc> defaultGrid;
	protected AbstractViewDoc form;
	protected AbstractModelDoc model;
	// protected FormLayout form = new FormLayout();
	protected DefaultForm defaultForm;

	public AbstractViewLista(AbstractRepository repository) {
		addClassName("abstract-view-lista");
		this.repository = repository;

	}

	public void initDefaultGrid() {
		setSizeFull();
		defaultGrid = new Grid<>();
		defaultGrid.setSizeFull();
	    defaultGrid.addClassName("abstract-view-lista-grid");

		Button criarDocumento = new Button("Criar Documento", e -> criarDocumento());

		Column<AbstractModelDoc> codigoColumn = defaultGrid.addColumn(AbstractModelDoc::getCodigo).setHeader("Código")
				.setSortable(true);
		codigoColumn.setComparator(Comparator.comparing(AbstractModelDoc::getCodigo)).setKey("codigo");
		Column<AbstractModelDoc> descricaoColumn = defaultGrid.addColumn(AbstractModelDoc::getDescricao)
				.setHeader("Descrição");
		Grid.Column<AbstractModelDoc> autorColumn = defaultGrid.addColumn(AbstractModelDoc::getAutor)
				.setHeader("Autor");
		autorColumn.setComparator(Comparator.comparing(AbstractModelDoc::getAutor)).setKey("autor");
//		Grid.Column<AbstractModelDoc> criacaoLocalDateTime = gridDefault
//				.addColumn(new LocalDateTimeRenderer<>(AbstractModelDoc::getCriacao, "dd/MM/yyyy HH:mm:ss"))
//				.setHeader("Criação");
		Grid.Column<AbstractModelDoc> criacaoColumn = defaultGrid.addColumn(new TextRenderer<>(item -> {
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
		searchText.addValueChangeListener(e -> updateListDefault(defaultGrid, searchText.getValue()));

		updateListDefault(defaultGrid, searchText.getValue());
		HorizontalLayout toolbar = new HorizontalLayout(searchText, criarDocumento);
	
		defaultForm = new DefaultForm();
		defaultForm.setWidth("25cm");
		add(toolbar,getDefaultContent());
		
		defaultGrid.asSingleSelect().addValueChangeListener(evt -> editModel(evt.getValue()));

	}
	
	private Component getDefaultContent() {
        HorizontalLayout content = new HorizontalLayout(defaultGrid, defaultForm);
        content.setFlexGrow(2, defaultGrid); 
        content.setFlexGrow(1, defaultForm);
        content.addClassNames("abstract-view-lista-content");
        content.setSizeFull();
        return content;
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
			System.out.println(form.getModel().getCodigo());
			//formLayoutDefault.setVisible(true);
		
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

	

	public void editModel(AbstractModelDoc model) {

		if (model == null) {
			closeFormDefault();
		} else {

			defaultForm.setModel(model);
			
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
		//formLayoutDefault.setVisible(false);
	}

	public void sendMail(String from, String sendTo, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(sendTo);
		message.setSubject(subject);
		message.setText(body);
		emailService.emailSender.send(message);

	}
	
	public class DefaultForm extends FormLayout {

		private static final long serialVersionUID = 1L;
		TextField codigo = new TextField("Código");
		TextField descricao = new TextField("Descrição");
		TextField status = new TextField("Status");
		TextField autor = new TextField("Autor");
		TextField criacao = new TextField("Criação");

		Button save = new Button("Salvar");
		Button delete = new Button("Apagar");
		Button close = new Button("Cancelar");

		BeanValidationBinder<AbstractModelDoc> binder;

		public DefaultForm() {
			addClassName("default-form");
			binder = new BeanValidationBinder<>(AbstractModelDoc.class);
			binder.forField(criacao).withConverter(new UtilsConverter.ZonedDateTimeToStringConverter())
					.bind(AbstractModelDoc::getCriacao, AbstractModelDoc::setCriacao);
			binder.bindInstanceFields(this);

			add(codigo, descricao, status, autor, criacao, createButtonsLayout());
		}

		public void setModel(AbstractModelDoc model) {
			binder.setBean(model);
		}

		private HorizontalLayout createButtonsLayout() {
			save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
			delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
			close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

			save.addClickShortcut(Key.ENTER);
			close.addClickShortcut(Key.ESCAPE);

			return new HorizontalLayout(save, delete, close);
		}
	}

}
