package br.com.tdec.intra.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.Data;

@Service
@Data
public class EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	public JavaMailSender emailSender;

	public EmailService(JavaMailSender emailSender) {

		this.emailSender = emailSender;
	}

	public void sendSimpleMessage(String from, String sendTo, String subject, String text) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(from);
			message.setTo(sendTo);
			message.setSubject(subject);
			message.setText(text);
			emailSender.send(message);
		} catch (MailException e) {
			logger.error("Logger -> sendSimpleMessage: Falha no envio de email.", e);
			e.printStackTrace();
		}
	}

	public void sendSimpleMessage(String from, List<String> sendTo, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		String[] toArray = sendTo.toArray(new String[0]);
		message.setFrom(from);
		message.setTo(toArray);
		message.setSubject(subject);
		message.setText(text);
		emailSender.send(message);
	}
}
