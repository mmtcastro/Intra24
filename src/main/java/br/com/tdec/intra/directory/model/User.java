package br.com.tdec.intra.directory.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

	private String username;
	@JsonProperty("sub")
	private String sub;
	@JsonProperty("name")
	private String name;
	@JsonProperty("given_name")
	private String givenName;
	@JsonProperty("family_name")
	private String familyName;
	@JsonProperty("preferred_username")
	private String preferredUsername;
	@JsonProperty("email")
	private String email;
	@JsonProperty("mailServer")
	private String mailServer;
	@JsonProperty("mailFile")
	private String mailFile;
	@JsonProperty("scope")
	private String scope;
	@JsonProperty("names")
	private List<String> names;
	private TokenData tokenData;
	private String token;

//	public User(String username) {
//		this.username = username;
//	}

	/**
	 * No loginForm, primeiro trago as informacoes do TokenData para, com o acesso
	 * do token, poder acessar o restApi para buscar o restante das informacoes do
	 * usuarios
	 * 
	 * @param json
	 */
	public void addAddicionalUserInformation(User tempUser) {
		try {
			this.setSub(tempUser.getSub());
			this.setName(tempUser.getName());
			this.setGivenName(tempUser.getGivenName());
			this.setFamilyName(tempUser.getFamilyName());
			this.setPreferredUsername(tempUser.getPreferredUsername());
			this.setEmail(tempUser.getEmail());
			this.setMailServer(tempUser.getMailServer());
			this.setMailFile(tempUser.getMailFile());
			this.setScope(tempUser.getScope());
			this.setNames(tempUser.getNames());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getCommonName() {
		return this.givenName + " " + this.familyName;
	}

	public String getFullName() {
		return this.givenName + " " + this.familyName;
	}

	public List<String> getRoles() {
		return names.stream().map(this::convertGroupNameToRole).toList();
	}

	public String convertGroupNameToRole(String groupName) {
		String role = "ROLE_" + groupName.trim().toUpperCase();
		return role;
	}

}
