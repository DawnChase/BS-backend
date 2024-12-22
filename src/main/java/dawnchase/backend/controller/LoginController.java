package dawnchase.backend.controller;

import dawnchase.backend.model.User;
import dawnchase.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        System.out.println("username: " + username);
        System.out.println("password: " + password);

        if (username == "" || password == "") {
            Map<String, String> response = new HashMap<>();
            response.put("message", "用户名或密码为空");
            return response;
        }

        Map<String, String> response = new HashMap<>();
        User user = userService.findUserByUsername(username);

        if (user == null)
            response.put("message", "用户不存在");
        else
        if (!user.getPassword().equals(password))
            response.put("message", "密码错误");
        else
            response.put("message", "登录成功");
        response.put("email", user.getEmail());
        return response;
    }
}