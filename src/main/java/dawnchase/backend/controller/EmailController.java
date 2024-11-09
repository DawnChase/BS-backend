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
        String verificationCode = String.valueOf(new Random().nextInt(900000) + 100000);

        emailService.sendSimpleEmail(email, "BS比价网站验证码", "你的验证码是：" + verificationCode);

        Map<String, String> response = new HashMap<>();
        response.put("verificationCode", verificationCode);
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