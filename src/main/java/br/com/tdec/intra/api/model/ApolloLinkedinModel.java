package br.com.tdec.intra.api.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor // 🔹 Adiciona um construtor sem argumentos automaticamente
@AllArgsConstructor // 🔹 Mantém o construtor com argumentos para o uso normal
public class ApolloLinkedinModel {

	private String email;
	private String firstName;
	private String lastName;
	private String linkedinId;
	private String linkedinTitle;
	private String linkedinUrl;
	private String organizationId;
	private String organizationName;
	private String organizationLinkedinUrl;

	public ApolloLinkedinModel(String email, String firstName, String lastName) {
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	// 🔹 Classe para requisição à Apollo API
	@Data
	public static class Body {
		private String api_key;
		private Boolean reveal_personal_emails;
		private List<Detail> details;

		// ✅ Construtor atual (email + nome)
		public Body(String api_key, String email, String firstName, String lastName) {
			this.api_key = api_key;
			this.reveal_personal_emails = true;
			this.details = new ArrayList<>();
			this.details.add(new Detail(email, firstName, lastName));
		}

		// ✅ Novo construtor que aceita uma lista de detalhes
		public Body(String api_key, List<Detail> details) {
			this.api_key = api_key;
			this.reveal_personal_emails = true;
			this.details = details;
		}
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Detail {
		private String email;
		private String first_name;
		private String last_name;
	}

	// 🔹 Classe para resposta da Apollo API
	@Data
	@NoArgsConstructor
	public static class LinkedInResponse {
		private String status;
		private List<Match> matches;
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Match {
		private String id;
		private String firstName;
		private String lastName;
		private String title;
		private String linkedinUrl;
		private String email; // Adicione esse campo
		private Organization organization;
	}

	@Data
	@NoArgsConstructor
	public static class Organization {
		private String id;
		private String name;
		private String linkedinUrl;
	}

}
