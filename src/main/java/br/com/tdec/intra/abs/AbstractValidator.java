package br.com.tdec.intra.abs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbstractValidator<T> extends Abstract {

	private T model;
}
