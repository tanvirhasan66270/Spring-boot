package com.example.SCM.Util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;

    public void  SenderGeneralMail(String to , String subject, String body) throws MessagingException {

        MimeMessage message=javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper=new MimeMessageHelper(message,true);
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(body,true);

        javaMailSender.send(message);


    }
}
