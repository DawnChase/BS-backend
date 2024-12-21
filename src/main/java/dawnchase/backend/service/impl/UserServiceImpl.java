package dawnchase.backend.service.impl;

import dawnchase.backend.mapper.UserMapper;
import dawnchase.backend.model.User;
import dawnchase.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    public void registerUser(User user) {
        userMapper.insertUser(user);
    }

    public User findUserByEmail(String email) {
        return userMapper.findUserByEmail(email);
    }

    public User findUserByUsername(String username) {
        return userMapper.findUserByUsername(username);
    }
}
