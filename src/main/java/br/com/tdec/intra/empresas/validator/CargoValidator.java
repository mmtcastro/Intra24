package br.com.tdec.intra.empresas.validator;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import br.com.tdec.intra.abs.AbstractService;
import br.com.tdec.intra.abs.AbstractValidator;
import br.com.tdec.intra.empresas.model.Cargo;

public class CargoValidator extends AbstractValidator<Cargo> {

	public static class CodigoValidator implements Validator<String> {

		private static final long serialVersionUID = 1L;
		AbstractService<Cargo> service;

		public CodigoValidator(AbstractService<Cargo> service) {
			this.service = service;
		}

		@Override
		public ValidationResult apply(String value, ValueContext context) {
			Cargo cargo = this.service.findByCodigo(value);
			if (cargo != null) {
				return ValidationResult.error("O código já existe");
			} else {
				return ValidationResult.ok();
			}

		}
	}
}
