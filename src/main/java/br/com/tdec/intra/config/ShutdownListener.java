package br.com.tdec.intra.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

public class ShutdownListener implements ApplicationListener<ContextClosedEvent> {

	private DominoServer dominoServer;

	public ShutdownListener(DominoServer dominoServer) {
		this.setDominoServer(dominoServer);
	}

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		System.out.println("Shutting down DominoServer");
		dominoServer.getServer().closeServer();

	}

	public DominoServer getDominoServer() {
		return dominoServer;
	}

	public void setDominoServer(DominoServer dominoServer) {
		this.dominoServer = dominoServer;
	}

}
