package br.com.tdec.intra.abs;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;

import br.com.tdec.intra.config.EmailService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Abstract {

	@Autowired
	private EmailService emailService;

	public static void print(Object object) {
		System.out.print(object);
	}

	public String toString() {
		 return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public void sendMail(String sendTo, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("noreply@example.com");
		message.setTo(sendTo);
		message.setSubject(subject);
		message.setText(body);
		emailService.emailSender.send(message);

	}
	
	
}
