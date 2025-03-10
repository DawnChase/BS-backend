package dawnchase.backend.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    private Map<String, String> verificationCodes = new HashMap<>();

    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(from);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
    
    public String generateAndSendVerificationCode(String email) {
        String verificationCode = String.valueOf(new Random().nextInt(900000) + 100000);
        sendSimpleEmail(email, "BS比价网站验证码", "你的验证码是：" + verificationCode);
        verificationCodes.put(email, verificationCode);
        return verificationCode;
    }

    public boolean verifyCode(String email, String code) {
        String storedCode = verificationCodes.get(email);
        return storedCode != null && storedCode.equals(code);
    }
}