package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/email")
public class EmailSenderController {

    @Autowired
    private EmailSenderService emailService;

    
    @PostMapping("/sendEmail")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
        boolean emailSent = emailService.sendEmail(request.getTo(), request.getSubject(), request.getBody());
        if (emailSent) {
            return ResponseEntity.ok("Email sent successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email.");
        }
    }
    
//    @GetMapping("/send/attachment")
//    public String sendEmailWithAttachment() throws MessagingException {
//        // Replace with actual email details
//    	emailService.sendEmailWithAttachment("s@gmail.com",
//				"This is Email Body with Attachment...",
//				"This email has attachment",
//				"C:\\Users\\sidheshwars\\Desktop\\COMMONSPRINGBOOTPROPERTIES.docx");
//        return "Email sent successfully!";
//    }
}

