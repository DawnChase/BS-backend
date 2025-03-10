package dawnchase.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dawnchase.backend.service.EmailService;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-verification-code")
    public Map<String, String> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        emailService.generateAndSendVerificationCode(email);

        Map<String, String> response = new HashMap<>();
        response.put("message", "验证码已发送");
        return response;
    }

    @GetMapping("/send")
    public String sendEmail() {
        String to = "853609691@qq.com";
        String subject = "Test";
        String content = "Test";
        emailService.sendSimpleEmail(to, subject, content);
        return "发送成功";
    }
}