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
@ConfigurationProperties(prefix = "domino.properties")
public class DominoProperties {
	@NotNull
	@NotEmpty
	private String host;

	@NotNull
	private Integer port;

	private String database; // ? Este teria que ser variavel.

	@NotNull
	private Integer executors;

	@NotNull
	@NotEmpty
	private String trustedRoots;

	@NotNull
	@NotEmpty
	private String clientCert;

	@NotNull
	@NotEmpty
	private String clientKey;
	@NotNull
	@NotEmpty
	private String keyPassword;

	@NotNull
	@NotEmpty
	private String idPassword;

}
