package br.com.tdec.intra.config;

import java.util.Properties;

import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
public class MailService {
	public MailProperties mailProperties;
	public JavaMailSenderImpl mailSender;

	public MailService(MailProperties mailProperties) {
		this.mailProperties = mailProperties;
		mailSender = new JavaMailSenderImpl();
		mailSender.setHost(mailProperties.getHost());
		mailSender.setPort(mailProperties.getPort());

		mailSender.setUsername(mailProperties.getUsername());
		mailSender.setPassword(mailProperties.getPassword());

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", mailProperties.getProtocol());
		props.put("mail.smtp.auth", mailProperties.getSmtp().isAuth());
		props.put("mail.smtp.starttls.enable", mailProperties.getSmtp().isStarttlsEnable());

	}

//	@Bean
//	public JavaMailSender emailSender() {
//		// JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//		mailSender.setHost(mailProperties.getHost());
//		mailSender.setPort(mailProperties.getPort());
//
//		mailSender.setUsername(mailProperties.getUsername());
//		mailSender.setPassword(mailProperties.getPassword());
//
//		Properties props = mailSender.getJavaMailProperties();
//		props.put("mail.transport.protocol", mailProperties.getProtocol());
//		props.put("mail.smtp.auth", mailProperties.getSmtp().isAuth());
//		props.put("mail.smtp.starttls.enable", mailProperties.getSmtp().isStarttlsEnable());
//
//		return mailSender;
//	}

	public void sendSimpleMessage(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("your-email@example.com"); // Specify the 'from' address
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		mailSender.send(message);
	}

}
