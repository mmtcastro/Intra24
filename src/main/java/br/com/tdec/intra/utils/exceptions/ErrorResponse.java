package br.com.tdec.intra.utils.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
	private int status;
	private String message;
	private String details;
	private Integer errorId; // Pode ser nulo
	private String destinationServer;
}
