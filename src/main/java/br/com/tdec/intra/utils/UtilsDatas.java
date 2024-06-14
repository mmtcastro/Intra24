package br.com.tdec.intra.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UtilsDatas {

	// Method to format the date directly within the model
	public String getFormattedData(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return ""; // or any default value
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		return localDateTime.format(formatter);
	}
}
