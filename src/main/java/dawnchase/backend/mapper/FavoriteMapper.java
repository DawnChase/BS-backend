package dawnchase.backend.mapper;

import dawnchase.backend.model.Favorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    @Insert("INSERT INTO favorites (href, username, price, email, timestamp) VALUES (#{href}, #{username}, #{price}, #{email}, #{timestamp})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void InsertFavorite(Favorite favorite);

    @Select("DELETE * FROM favorites WHERE href = #{href} AND username = #{username}")
    void DeleteFavorite(@Param("href") String href, @Param("username") String username);

    @Select("SELECT * FROM favorites WHERE href = #{href} AND username = #{username}")
    Favorite FindFavorite(@Param("href")String href, @Param("username")String username);

    @Select("SELECT * FROM favorites WHERE username = #{username}")
    List<Favorite> FindFavoriteByUsername(@Param("username")String username);
}