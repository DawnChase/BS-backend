package dawnchase.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dawnchase.backend.model.User;
import dawnchase.backend.service.EmailService;
import dawnchase.backend.service.UserService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://10.162.194.150:5173/")
public class RegisterController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody Map<String, String> request) {
        System.out.println("Received data: " + request);
        String username = request.get("username");
        String email = request.get("email");
        String password = request.get("password");
        String verificationCode = request.get("verificationCode");

        Map<String, String> response = new HashMap<>();

        if (!emailService.verifyCode(email, verificationCode)) {
            response.put("message", "验证码错误");
            return response;
        }

        // 检查用户是否已存在
        if (userService.findUserByEmail(email) != null) {
            response.put("message", "用户已存在");
            return response;
        }
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);

        userService.registerUser(user);

        response.put("message", "注册成功");
        return response;
    }
}