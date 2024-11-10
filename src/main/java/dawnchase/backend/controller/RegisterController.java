package dawnchase.backend.controller;

import java.util.HashMap;
import java.util.Map;

import dawnchase.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class RegisterController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody Map<String, String> request) {
        System.out.println("Received data: " + request);
        String username = request.get("username");
        String email = request.get("email");
        String password = request.get("password");
        String verificationCode = request.get("verificationCode");
        String sentCode = request.get("sentCode");

        Map<String, String> response = new HashMap<>();

        if (!emailService.verifyCode(email, verificationCode)) {
            response.put("message", "验证码错误");
            return response;
        }

        // 这里添加用户注册逻辑，例如将用户信息存储到数据库
        response.put("message", "注册成功");
        return response;
    }
}