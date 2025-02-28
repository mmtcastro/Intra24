package br.com.tdec.intra.abs;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import br.com.tdec.intra.services.Response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbstractValidator<T extends AbstractModelDoc> extends Abstract {

	private T model;

	public AbstractValidator(T model) {
		this.model = model;
	}

	public static class CodigoValidator<T extends AbstractModelDoc> implements Validator<String> {

		private static final long serialVersionUID = 1L;
		private final AbstractService<T> service;

		public CodigoValidator(AbstractService<T> service) {
			this.service = service;
		}

		@Override
		public ValidationResult apply(String value, ValueContext context) {
			Response<T> response = this.service.findByCodigo(value);
			if (response.getModel() != null) {
				return ValidationResult.error("O código " + response.getModel().getCodigo() + " já existe");
			} else {
				return ValidationResult.ok();
			}
		}
	}
}
