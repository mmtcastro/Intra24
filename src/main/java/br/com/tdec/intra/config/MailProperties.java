package br.com.tdec.intra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class MailProperties {

	private String host;
	private int port;
	private String protocol;
	private String username;
	private String password;
	private final Smtp smtp = new Smtp();

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
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

	public Smtp getSmtp() {
		return smtp;
	}

	public static class Smtp {
		private boolean auth;
		private boolean starttlsEnable;
		private boolean debug;

		public boolean isAuth() {
			return auth;
		}

		public void setAuth(boolean auth) {
			this.auth = auth;
		}

		public boolean isStarttlsEnable() {
			return starttlsEnable;
		}

		public void setStarttlsEnable(boolean starttlsEnable) {
			this.starttlsEnable = starttlsEnable;
		}

		public boolean isDebug() {
			return debug;
		}

		public void setDebug(boolean debug) {
			this.debug = debug;
		}
	}
}
