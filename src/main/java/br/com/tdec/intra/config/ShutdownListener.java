package br.com.tdec.intra.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import lombok.Getter;
import lombok.Setter;

public class ShutdownListener implements ApplicationListener<ContextClosedEvent> {

	private WebClientService webClientConfig;

	public ShutdownListener(WebClientService webConfig) {
		this.setWebClientConfig(webConfig);
	}

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		System.out.println("Shutting down DominoServer");
		// dominoServer.getServer().closeServer();
		LogoutResponse ret = webClientConfig.getWebClient().post()//
				.uri("/api/v1/auth/logout")//
				.bodyValue("{\"logout\": \"Yes\"}")//
				.retrieve() //
				.bodyToMono(LogoutResponse.class).block();
		if (ret != null) {
			System.out.println("Logout Status: " + ret.getStatus());
			System.out.println("Logout Message: " + ret.getStatusText());

		} else {
			System.out.println("ERRO - Logout Status: null");
		}
	}

	@Getter
	@Setter
	private class LogoutResponse {
		private String statusText;
		private String status;
		private String message;

	}

	public WebClientService getWebClientConfig() {
		return webClientConfig;
	}

	public void setWebClientConfig(WebClientService webClientConfig) {
		this.webClientConfig = webClientConfig;
	}

}
