package edu.arizona.biosemantics.etcsite.server;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;


public class Emailer {
	
	private Session session;
	
	@Inject
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
						log(LogLevel.ERROR, "Couldn't send email", e);
					}
				}
			};
			sendThread.start();
		} catch(MessagingException e) {
			log(LogLevel.ERROR, "Couldn't sent email", e);
		}
	}
	
	public void sendEmailAttachment(final String to, final String subjectLine, final String bodyText, final String input, final String output) {
		try {
			final Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(Configuration.emailAddress));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subjectLine);
			Multipart multipart = new MimeMultipart();
			MimeBodyPart messageBodyPart1 = new MimeBodyPart();
		    messageBodyPart1.setText(bodyText);
		    multipart.addBodyPart(messageBodyPart1);	  
		    addAttachment(multipart,input);
		    addAttachment(multipart,output);
		    message.setContent(multipart);
			Thread sendThread = new Thread(){
				public void run(){
					try {
						Transport.send(message);
					} catch (MessagingException e) {
						log(LogLevel.ERROR, "Couldn't send email", e);
					}
				}
			};
			sendThread.start();
		} catch(MessagingException e) {
			log(LogLevel.ERROR, "Couldn't sent email", e);
		}
	}
	
	private void addAttachment(Multipart multipart, String filename){
	    DataSource source = new FileDataSource(filename);
	    BodyPart messageBodyPart = new MimeBodyPart();        
	    try {
			messageBodyPart.setDataHandler(new DataHandler(source));
		    messageBodyPart.setFileName(filename);
		    multipart.addBodyPart(messageBodyPart);
		} catch (MessagingException e) {
			log(LogLevel.ERROR, "Couldn't send email", e);
		}

	}
}
