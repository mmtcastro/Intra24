package br.com.tdec.intra.services;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response<T> {
	private T model;
	private String message;
	private int status;
	private boolean success;

	public Response(T body, String message, int status, boolean success) {
		this.model = body;
		this.message = message;
		this.status = status;
		this.success = success;
	}

}
