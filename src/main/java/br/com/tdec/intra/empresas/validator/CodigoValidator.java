package br.com.tdec.intra.empresas.validator;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import br.com.tdec.intra.empresas.model.Vertical;
import br.com.tdec.intra.empresas.services.VerticalService;

public class CodigoValidator implements Validator<String> {

	private static final long serialVersionUID = 1L;
	VerticalService service;

	public CodigoValidator(VerticalService service) {
		this.service = service;
	}

	@Override
	public ValidationResult apply(String value, ValueContext context) {
		Vertical vertical = this.service.findByCodigo(value);
		if (vertical != null) {
			return ValidationResult.error("O código já existe");
		} else {
			return ValidationResult.ok();
		}

	}

}
