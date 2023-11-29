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
@ConfigurationProperties(prefix = "ldap.properties")
public class LdapProperties {
	@NotNull
	@NotEmpty
	private String userSearchFilter;
	@NotNull
	@NotEmpty
	private String userSearchBase;
	@NotNull
	@NotEmpty
	private String url;
	@NotNull
	private int port;
	@NotNull
	@NotEmpty
	private String managerDn;
	@NotNull
	@NotEmpty
	private String managerPassword;

}
