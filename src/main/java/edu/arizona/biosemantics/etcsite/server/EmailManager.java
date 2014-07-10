package edu.arizona.biosemantics.etcsite.server;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EmailManager {
	
	private static EmailManager instance;
	
	private Session session;
	
	public EmailManager(){
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", Configuration.emailSMTPServer);
		props.put("mail.smtp.port", "587");
 
		session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(Configuration.emailAddress, Configuration.emailPassword);
			}
		});
	}
	
	public static EmailManager getInstance(){
		if (instance == null)
			instance = new EmailManager();
		return instance;
	}
	
	public void sendEmail(final String to, final String subjectLine, final String bodyText) throws MessagingException {
		final Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(Configuration.emailAddress));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
		message.setSubject(subjectLine);
		message.setText(bodyText);

		Thread sendThread = new Thread(){
			public void run(){
				try {
					Transport.send(message);
					//System.out.println("Sent message to " + to + ". Subject line: " + subjectLine + ", Body: " + bodyText);
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		};
		sendThread.start();
	}
}
