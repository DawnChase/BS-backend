package dawnchase.backend.service;

import dawnchase.backend.model.User;

public interface UserService {

    public void registerUser(User user);

    public User findUserByEmail(String email);

    public User findUserByUsername(String username);

}