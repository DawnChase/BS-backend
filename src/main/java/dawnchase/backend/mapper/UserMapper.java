package dawnchase.backend.mapper;

import dawnchase.backend.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Insert("INSERT INTO users (username, email, password, timestamp) VALUES (#{username}, #{email}, #{password}), #{timestamp}")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertUser(User user);

    @Select("SELECT * FROM users WHERE email = #{email}")
    User findUserByEmail(@Param("email") String email);

    @Select("SELECT * FROM users WHERE username = #{username}")
    User findUserByUsername(@Param("username") String username);
}