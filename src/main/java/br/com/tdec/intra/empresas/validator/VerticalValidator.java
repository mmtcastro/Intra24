package br.com.tdec.intra.empresas.validator;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.abs.AbstractValidator;
import br.com.tdec.intra.empresas.model.Vertical;

public class VerticalValidator extends AbstractValidator<Vertical> {

	public static class CodigoValidator implements Validator<String> {

		private static final long serialVersionUID = 1L;
		AbstractService<Vertical> service;

		public CodigoValidator(AbstractService<Vertical> service) {
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
}
