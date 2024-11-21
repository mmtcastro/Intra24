package br.com.tdec.intra.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Validated
@Component
@ConfigurationProperties(prefix = "webclient.properties")
public class WebClientProperties {

	@NotNull
	@NotEmpty
	private List<String> baseUrls; // Lista de URLs para balanceamento

	@NotNull
	@NotEmpty
	private String baseUrl;

	@NotNull
	@NotEmpty
	private String username; // ? Este teria que ser variavel.

	@NotNull
	@NotEmpty
	private String password;

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getBaseUrls() {
		return baseUrls;
	}

	public void setBaseUrls(List<String> baseUrls) {
		this.baseUrls = baseUrls;
	}

}
