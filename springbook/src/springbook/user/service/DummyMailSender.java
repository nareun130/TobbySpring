package springbook.user.service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

//아무런 하는 일이 없는 MailSender -> 테스트용 메일 전송 클래스
public class DummyMailSender implements MailSender {

	@Override
	public void send(SimpleMailMessage arg0) throws MailException {
	}

	@Override
	public void send(SimpleMailMessage[] arg0) throws MailException {
	}

}
