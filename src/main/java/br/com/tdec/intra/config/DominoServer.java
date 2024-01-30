package br.com.tdec.intra.config;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.hcl.domino.db.model.Database;
import com.hcl.domino.db.model.Server;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "domino.properties")
@Configuration
public class DominoServer {
	private LoggerFactory log;
	private Server server;
	private DominoProperties dominoProperties;
	private String host;
	private int port;
	private String trustedRoots;
	private String clientCert;
	private String clientKey;
	private String keyPassword;
	private String idFilePassword;

	public DominoServer(DominoProperties dominoProperties) {
		System.out.println("Antes da criacao do componente dominoServer");
		this.dominoProperties = dominoProperties;
		this.host = dominoProperties.getHost();
		this.port = dominoProperties.getPort();
		this.trustedRoots = dominoProperties.getTrustedRoots();
		this.clientCert = dominoProperties.getClientCert();
		this.clientKey = dominoProperties.getClientKey();
		this.keyPassword = dominoProperties.getKeyPassword();
		this.idFilePassword = dominoProperties.getIdPassword();

		try {
			setServer(new Server(host, //
					port, //
					new File(trustedRoots), //
					new File(clientCert), //
					new File(clientKey), //
					keyPassword, // keyPassword
					idFilePassword, // idFilePassword
					Executors.newSingleThreadExecutor()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getHostName() {
		return server.getHostName();
	}

	public int getPort() {
		return server.getPort();
	}

	public Database getDatabase(String databaseNameComNsf) {
		Database db = null;
		if (server != null) {
			db = server.useDatabase(databaseNameComNsf);
			System.out.println("Database " + db.toString() + " conectado...");
		} else {
			System.out.println("PROBLEMA - Server eh null - database " + databaseNameComNsf + " nao foi conectado...");
		}
		return db;
	}

}
