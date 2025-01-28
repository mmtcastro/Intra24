package br.com.tdec.intra.utils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class UtilsDatas {

	// Method to format the date directly within the model
	public String getFormattedData(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return ""; // or any default value
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		return localDateTime.format(formatter);
	}

	/**
	 * Ferramenta para ver se os conversores realmente estão retornando o padrao
	 * Iso8601 (no meu caso ficam faltando os segundos)
	 * 
	 * @param dateString
	 * @return
	 */
	public static boolean isIso8601(String dateString) {
		try {
			DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
			// Tenta fazer o parse da string usando o padrão ISO 8601
			ZonedDateTime.parse(dateString, isoFormatter);
			return true; // Se o parse foi bem-sucedido, está no formato ISO 8601
		} catch (DateTimeParseException e) {
			return false; // Caso ocorra uma exceção, o formato está incorreto
		}
	}

	public static boolean isIso8601(ZonedDateTime dateTime) {
		if (dateTime == null) {
			return false;
		}
		DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
		// Formata o ZonedDateTime e verifica se corresponde ao padrão ISO 8601
		String formattedDate = dateTime.format(isoFormatter);

		// Tenta reanalisar a string formatada para garantir que ela esteja correta
		try {
			ZonedDateTime.parse(formattedDate, isoFormatter);
			return true; // Se o parse for bem-sucedido, é um formato ISO 8601 válido
		} catch (Exception e) {
			return false; // Caso contrário, não está no formato ISO 8601
		}
	}

	/**
	 * Retorna um string a partir da formatacao de um ZonedDateTime no padrao
	 * Iso8601 (Domino RESTAPI)
	 * 
	 * @param zonedDateTime
	 * @return
	 */
	public static String formatToIso8601(ZonedDateTime zonedDateTime) {
		DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
		return zonedDateTime != null ? zonedDateTime.format(isoFormatter) : null;
	}

}
