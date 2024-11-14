package br.com.tdec.intra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "webclient.properties")
public class WebClientProperties {

	@NotNull
	@NotEmpty
	private String baseUrl;

	@NotNull
	@NotEmpty
	private String username; // ? Este teria que ser variavel.

	@NotNull
	@NotEmpty
	private String password;

}
