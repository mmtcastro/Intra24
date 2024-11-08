package br.com.tdec.intra.empresas.view;

import java.time.LocalDate;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.tdec.intra.abs.AbstractValidator;
import br.com.tdec.intra.abs.AbstractViewDoc;
import br.com.tdec.intra.empresas.model.Vertical;
import br.com.tdec.intra.empresas.services.VerticalService;
import br.com.tdec.intra.utils.converters.ProperCaseConverter;
import br.com.tdec.intra.utils.converters.RemoveSimbolosEAcentos;
import br.com.tdec.intra.utils.converters.RichTextToMimeConverter;
import br.com.tdec.intra.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PageTitle("Vertical")
@Route(value = "vertical", layout = MainLayout.class)
@RolesAllowed("ROLE_EVERYONE")
public class VerticalView extends AbstractViewDoc<Vertical> {

	private static final long serialVersionUID = 1L;
	private DatePicker dataField = new DatePicker("Data");
	private TextField codigoField = new TextField("Código");
	private TextField descricaoField = new TextField("Descrição");
	private RichTextEditor bodyField = new RichTextEditor();

	public VerticalView(VerticalService service) {
		super(Vertical.class, service);
		addClassNames("abstract-view-doc");
	}

	public void initBinder() {

		if (isNovo) {
			// model.setData(ZonedDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT,
			// ZoneId.systemDefault()));
			model.setData(LocalDate.now());
			binder.forField(codigoField).asRequired("Entre com um código").withNullRepresentation("")
					.withConverter(new ProperCaseConverter())//
					.withConverter(new RemoveSimbolosEAcentos())
					.withValidator(new AbstractValidator.CodigoValidator<>(service))
					.bind(Vertical::getCodigo, Vertical::setCodigo);
		} else {
			binder.forField(codigoField).asRequired("Entre com um código").withNullRepresentation("")
					.bind(Vertical::getCodigo, Vertical::setCodigo);
			readOnlyFields.add(codigoField);
		}
		binder.forField(dataField)//
				.asRequired("Formato esperado: DD/MM/AAAA")//
				// .withConverter(new ZonedDateTimeToIso8601Converter())//
				.bind(Vertical::getData, Vertical::setData);

		binder.forField(descricaoField).asRequired("Entre com uma descrição").bind(Vertical::getDescricao,
				Vertical::setDescricao);

		binder.forField(bodyField).withNullRepresentation("") // Representação nula para o campo de entrada
				.withConverter(new RichTextToMimeConverter()) // Aplicando o converter
				.bind(Vertical::getBody, Vertical::setBody);

		binder.setBean(model);

		// Adicione os campos na ordem correta (caso contrario o updateView troca a
		// ordem)
		binderFields.add(codigoField);
		binderFields.add(dataField);
		binderFields.add(descricaoField);
		binderFields.add(bodyField);
		print("Nomes dos arquivos eh " + model.getFileNames());

		// dataField.addValueChangeListener(e -> System.out.println("Data
		// (ZonedDateTime): " + model.getData()));

	}

}
