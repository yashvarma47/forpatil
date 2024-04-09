package com.example.demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailSenderService {

	@Autowired
	private JavaMailSender javaMailSender;
	
//	@Value("${spring.mail.username}")
//	private String username;

	public boolean sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom("Thinknxt-Capstoneproject@maveric-systems.com");
            message.setSubject(subject);
            message.setText(body);
            javaMailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

//	public void sendEmailWithAttachment(String toEmail,
//                String body,
//                String subject,
//                String attachment) throws MessagingException {
//
//		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//
//		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
//
//		mimeMessageHelper.setFrom("spring.email.from@gmail.com");
//		mimeMessageHelper.setTo(toEmail);
//		mimeMessageHelper.setText(body);
//		mimeMessageHelper.setSubject(subject);
//		
////		FileSystemResource fileSystem = new FileSystemResource(new File(attachment));
//		Path attachmentPath = Paths.get(attachment);
//		File attachmentFile = attachmentPath.toFile();
//
//		FileSystemResource fileSystemResource = new FileSystemResource(attachmentFile);
//
//		String attachmentFileName = fileSystemResource.getFilename();
//
//		mimeMessageHelper.addAttachment(attachmentFileName, fileSystemResource);
//
//		javaMailSender.send(mimeMessage);
//		System.out.println("Mail Sent with attachment...");
//	}	
}