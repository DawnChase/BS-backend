package dawnchase.backend.controller;

import dawnchase.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "http://localhost:5173")
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