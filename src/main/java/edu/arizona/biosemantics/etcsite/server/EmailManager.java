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
	
	public static final String PASSWORD_RESET_SUBJECT = "Password Reset Code";
	public static final String PASSWORD_RESET_BODY = "A password reset authentication code has been generated for your account (<openidproviderid>). You can use this code to reset your password. \n\nCode: <code>\n\nThis code will expire in <expire>.\n\n\n(You are receiving this email because you recently requested an authentication code to reset your account password. If you did not request an authentication code, ignore this email.)";
	
	private final String username = "etcsite.norespond@gmail.com";
	private final String password = "biosemantics";
	private static EmailManager instance;
	
	private Session session;
	
	public EmailManager(){
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
 
		session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
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
		message.setFrom(new InternetAddress(username));
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
