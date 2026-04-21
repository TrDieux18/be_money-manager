package com.trandieu.moneymanager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
   private final JavaMailSender mailSender;

   @Value("${spring.mail.properties.mail.smtp.from}")
   private String fromEmail;

   public void sendEmail(String to, String subject, String body) {
      try {
         SimpleMailMessage message = new SimpleMailMessage();
         message.setFrom(fromEmail);
         message.setTo(to);
         message.setSubject(subject);
         message.setText(body);
         mailSender.send(message);
         log.info("Email sent successfully to: {}", to);
      } catch (Exception e) {
         log.error("Failed to send email to {}: ", to, e);
         throw new RuntimeException("Failed to send email to " + to + ": " + e.getMessage(), e);
      }
   }

   public void sendHtmlEmail(String to, String subject, String htmlBody) {
      try {
         MimeMessage message = mailSender.createMimeMessage();
         MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
         helper.setFrom(fromEmail);
         helper.setTo(to);
         helper.setSubject(subject);
         helper.setText(htmlBody, true);
         mailSender.send(message);
         log.info("HTML email sent successfully to: {}", to);
      } catch (Exception e) {
         log.error("Failed to send HTML email to {}: ", to, e);
         throw new RuntimeException("Failed to send HTML email to " + to + ": " + e.getMessage(), e);
      }
   }
}
