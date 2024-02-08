package br.com.tdec.intra.directory.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenData {

	private String bearer;
	private Claims claims;
	private int leeway;
	@JsonProperty("expSeconds")
	private int expSeconds;
	@JsonProperty("issueDate")
	private String issueDate;

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Claims {
		private String iss;
		private String sub;
		private long iat;
		private long exp;
		private List<String> aud;
		private String CN;
		private String scope;
		private String email;
	}
}
