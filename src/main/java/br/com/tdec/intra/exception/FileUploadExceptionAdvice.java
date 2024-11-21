package br.com.tdec.intra.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class FileUploadExceptionAdvice {

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<Map<String, String>> handleMaxSizeException(MaxUploadSizeExceededException exc) {
		Map<String, String> responseBody = new HashMap<>();
		responseBody.put("error", "O arquivo enviado excede o tamanho máximo permitido!");

		return ResponseEntity.badRequest().body(responseBody);
	}
}
