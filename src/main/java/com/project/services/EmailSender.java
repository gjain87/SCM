package com.project.services;

import java.util.Properties;

import org.springframework.stereotype.Service;
import javax.mail.*;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class EmailSender {
	public boolean sendemail(String message, String subject, String to)
	{
		boolean flag=false;
		//Sender
		 String from="jgaurav117@gmail.com";
		//variable for gmail
		String host="smtp.gmail.com";
		
		//get the system properties
		Properties properties = System.getProperties();
		
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
		//step1: get the session object
		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication("jgaurav117@gmail.com","avhq cmhj voyc wjrg");
			}
			
		});
		
		session.setDebug(true);
		
		//step2: compose message
		MimeMessage m = new MimeMessage(session);
		try {
			//sender
			m.setFrom(from);
			//reciever
			m.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
			//subject
			m.setSubject(subject);
			//message
			m.setText(message);
			
			
			//send msg
			Transport.send(m);
			flag=true;
			System.out.println("Msg sent....");
		} catch (MessagingException e) {
		
			e.printStackTrace();
		}
		return flag;
	}
}
