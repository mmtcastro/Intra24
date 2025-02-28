package br.com.tdec.intra.api.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.tdec.intra.api.model.Receitaws;

@Service
public class ReceitawsService {
	private static final String API_URL = "https://receitaws.com.br/v1/cnpj/";

	public static Receitaws findCnpj(String cnpj) throws IOException, InterruptedException {
		// Constrói a URI com o CNPJ fornecido
		String url = API_URL + cnpj;

		// Cria o cliente HTTP
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().header("Accept", "application/json")//
				.build();

		// Envia a requisição e recebe a resposta
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		// Verifica se o status é 200 OK
		if (response.statusCode() == HttpURLConnection.HTTP_OK) {
			// Converte JSON para o objeto Cnpj usando Jackson
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(response.body(), Receitaws.class);
		} else {
			throw new IOException("Erro ao consultar CNPJ. Status: " + response.statusCode());
		}
	}
}
