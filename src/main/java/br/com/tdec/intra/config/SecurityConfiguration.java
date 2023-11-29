package br.com.tdec.intra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

import br.com.tdec.intra.login.view.LoginView;


@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

	/** Para trocar o form de login do padrão para o form do Vaadin que pode ser customizado
	 */
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);
		setLoginView(http, LoginView.class);
	}
}
