package edu.arizona.biosemantics.etcsite.server;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EmailManager {
	
	public static final String PASSWORD_RESET_SUBJECT = "Password Reset Code";
	public static final String PASSWORD_RESET_BODY = "A password reset authentication code has been generated for your account (<nonuniqueid>). With this code you may reset your password. \n\n\tAuthentication code: <code>\n\nThis code will expire in <expire>.\n\n\n(This email is being sent because you recently requested an authentication to reset your account password. If you did not request an authentication code, ignore this email.)";
	
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
	
	public void sendEmail(String to, String subjectLine, String bodyText) throws AddressException, MessagingException {
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(username));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
		message.setSubject(subjectLine);
		message.setText(bodyText);

		Transport.send(message);
		
		System.out.println("Sent message to " + to + ". Subject line: " + subjectLine + ", Body: " + bodyText);
	}
}
