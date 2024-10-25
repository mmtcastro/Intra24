package br.com.tdec.intra.utils.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomWebClientException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private int status;
	private ErrorResponse errorResponse;

	public CustomWebClientException(String message, int status) {
		super(message);
		this.status = status;

	}

	public CustomWebClientException(String message, int status, ErrorResponse errorResponse) {
		super(message);
		this.status = status;
		this.errorResponse = errorResponse;
	}

}