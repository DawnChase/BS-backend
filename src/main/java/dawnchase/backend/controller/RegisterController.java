package dawnchase.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class RegisterController {

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> request) {
        System.out.println("Received data: " + request);
        String username = request.get("username");
        String email = request.get("email");
        String password = request.get("password");
        String verificationCode = request.get("verificationCode");
        String sentCode = request.get("sentCode");

        if (!verificationCode.equals(sentCode)) {
            return ResponseEntity.badRequest().body("验证码错误！");
        }

        // 这里添加用户注册逻辑，例如将用户信息存储到数据库
        return ResponseEntity.ok("注册成功！");
    }
}