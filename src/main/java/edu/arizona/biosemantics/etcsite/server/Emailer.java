package edu.arizona.biosemantics.etcsite.server;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import edu.arizona.biosemantics.etcsite.shared.log.LogLevel;


public class Emailer {
	
	private Session session;
	
	public Emailer(){
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", Configuration.emailSMTPServer);
		props.put("mail.smtp.port", Configuration.emailSMTPPort);
 
		session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(Configuration.emailAddress, Configuration.emailPassword);
			}
		});
	}
	
	public void sendEmail(final String to, final String subjectLine, final String bodyText) {
		try {
			final Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(Configuration.emailAddress));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subjectLine);
			message.setText(bodyText);
			Thread sendThread = new Thread(){
				public void run(){
					try {
						Transport.send(message);
					} catch (MessagingException e) {
						e.printStackTrace();
					}
				}
			};
			sendThread.start();
		} catch(MessagingException e) {
			log(LogLevel.ERROR, "Couldn't sent email", e);
		}
	}
}
