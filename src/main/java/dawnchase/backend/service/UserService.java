package dawnchase.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import dawnchase.backend.mapper.UserMapper;
import dawnchase.backend.model.User;

public interface UserService {

    public User registerUser(User user);

    public User findUserByEmail(String email);
}