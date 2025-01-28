package br.com.tdec.intra.utils.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {
	private int status;
	private String message;
	private String details;
	private Integer errorId; // Pode ser nulo
	private String destinationServer;
	private String location;
}
