package br.com.tdec.intra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

import br.com.tdec.intra.login.view.LoginViewAsync;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// 🔓 Desabilita CSRF e form padrão
		http.csrf(csrf -> csrf.disable());
		http.formLogin(login -> login.disable());
		http.logout(logout -> logout.disable());

		// 🎯 Define rotas públicas (antes de super.configure)
		http.authorizeHttpRequests(auth -> auth.requestMatchers("/login", "/images/**", "/styles/**").permitAll());

		// 🔐 Criação de sessão padrão
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

		// 🔒 Configuração do Vaadin com anyRequest().authenticated()
		super.configure(http);

		// 📍 Define a view de login personalizada
		setLoginView(http, LoginViewAsync.class);
	}

    @Bean
    PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); // Substitui NoOpPasswordEncoder por uma opção segura
	}
}
