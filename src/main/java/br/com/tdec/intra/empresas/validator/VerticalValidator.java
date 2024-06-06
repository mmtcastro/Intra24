package br.com.tdec.intra.empresas.validator;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import br.com.tdec.intra.abs.AbstractValidator;
import br.com.tdec.intra.empresas.services.VerticalService;

public class VerticalValidator extends AbstractValidator {

	public class codigoValidator implements Validator<String> {

		private static final long serialVersionUID = 1L;
		VerticalService service;

		public codigoValidator(VerticalService service) {
			this.service = service;
		}

		@Override
		public ValidationResult apply(String value, ValueContext context) {

			return ValidationResult.error("O código já existe");
		}

	}
}
