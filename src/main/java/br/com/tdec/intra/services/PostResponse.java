package br.com.tdec.intra.services;

public class PostResponse {
	private String details;
	private String message;
	private int status;

	// Default constructor
	public PostResponse() {
	}

	// Parameterized constructor
	public PostResponse(String details, String message, int status) {
		this.details = details;
		this.message = message;
		this.status = status;
	}

	// Getters and setters
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ErrorResponse{" + "details='" + details + '\'' + ", message='" + message + '\'' + ", status=" + status
				+ '}';
	}
}
