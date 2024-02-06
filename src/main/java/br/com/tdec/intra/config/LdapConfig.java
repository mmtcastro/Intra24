package br.com.tdec.intra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.ldap.LdapBindAuthenticationManagerFactory;
import org.springframework.security.ldap.userdetails.LdapUserDetailsManager;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
public class LdapConfig {

	private LdapProperties ldapProperties;

	public LdapConfig(LdapProperties ldapProperties) {
		this.setLdapProperties(ldapProperties);
	}

	@Bean
	LdapContextSource contextSource() {

		LdapContextSource contextSource = new LdapContextSource();
		// contextSource.setUrl("ldap://luvox.tdec.com.br");
		// contextSource.setBase("O=TDec");
		contextSource.setUrl(ldapProperties.getUrl());
		// contextSource.setUserDn("mcastro");
		contextSource.setUserDn(ldapProperties.getManagerDn());
		// contextSource.setPassword("Hodge$404");
		contextSource.setPassword(ldapProperties.getManagerPassword());
		contextSource.setPooled(true);

		return contextSource;

	}

	@Bean
	AuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource) {
		LdapBindAuthenticationManagerFactory factory = new LdapBindAuthenticationManagerFactory(contextSource);
		factory.setUserSearchBase("O=TDec");
		factory.setUserSearchFilter("(|(cn={0})(uid={0}))"); // Marcelo Castro OU mcastro

		return factory.createAuthenticationManager();
	}

	@Bean
	UserDetailsContextMapper userDetailsContextMapper() {
		return new LdapUserDetailsMapper();
	}

	@Bean
	LdapUserDetailsManager ldapUserDetailsManager() {
		LdapUserDetailsManager userDetailsManager = new LdapUserDetailsManager(contextSource());
		userDetailsManager.setUserDetailsMapper(userDetailsContextMapper());
		return userDetailsManager;
	}

	@Bean
	LdapTemplate ldapTemplate() {
		return new LdapTemplate(contextSource());
	}

}
