package br.com.tdec.intra.api.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ApolloLinkedinModel {
	private String email;
	private String firstName;
	private String lastName;
	private List<String> emails;
	private LinkedInResponse linkedInResponse;

	// Construtores
	public ApolloLinkedinModel(String email, String firstName, String lastName) {
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public ApolloLinkedinModel(List<String> emails) {
		this.emails = emails;
	}

	public ApolloLinkedinModel(String email2, String firstName2, String lastName2, String id, String title,
			String linkedinUrl, Object object, Object object2, Object object3) {
		// TODO Auto-generated constructor stub
	}

	// 🔹 Corpo da requisição para a API
	@Data
	public static class Body {
		private String api_key;
		private Boolean reveal_personal_emails;
		private List<Detail> details;

		public Body(String api_key, String email, String firstName, String lastName) {
			this.api_key = api_key;
			this.reveal_personal_emails = true;
			this.details = new ArrayList<>();
			this.details.add(new Detail(email, firstName, lastName));
		}
	}

	// 🔹 Representa os detalhes da requisição
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Detail {
		private String email;
		@JsonProperty("first_name")
		private String firstName;
		@JsonProperty("last_name")
		private String lastName;

		public Detail(String email, String firstName, String lastName) {
			this.email = email;
			this.firstName = firstName;
			this.lastName = lastName;
		}
	}

	// 🔹 Resposta da API
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class LinkedInResponse {
		private String status;
		private int totalRequestedEnrichments;
		private int uniqueEnrichedRecords;
		private int missingRecords;
		private List<Match> matches;
	}

	// 🔹 Representa um Match de perfil encontrado
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Match {
		private String id;
		@JsonProperty("first_name")
		private String firstName;
		@JsonProperty("last_name")
		private String lastName;
		@JsonProperty("linkedin_url")
		private String linkedinUrl;
		private String title;
		private Organization organization;
	}

	// 🔹 Representa a organização do Match
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Organization {
		private String id;
		private String name;
		@JsonProperty("linkedin_url")
		private String linkedinUrl;
	}
}
