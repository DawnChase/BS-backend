package dawnchase.backend.mapper;

import dawnchase.backend.model.Favorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    @Insert("INSERT INTO favorites (href, username, price, email, timestamp) VALUES (#{href}, #{username}, #{price}, #{email}, #{timestamp})")
    void InsertFavorite(Favorite favorite);

    @Select("DELETE FROM favorites WHERE href = #{href} AND username = #{username}")
    void DeleteFavorite(@Param("href") String href, @Param("username") String username);

    // 用于查找是否存在该商品
    @Select("SELECT * FROM favorites WHERE href = #{href} AND username = #{username}")
    List<Favorite> FindFavorite(@Param("href")String href, @Param("username")String username);

    // 用于收藏夹中显示
    // 取同名中timestamp最大的记录（最接近现在）
    @Select("SELECT href FROM favorites WHERE username = #{username} GROUP BY href ORDER BY MAX(STR_TO_DATE(timestamp, '%Y-%m-%d %H:%i:%s')) DESC")
    List<String> FindFavoriteByUsername(@Param("username")String username);

    // 用于判断是否降价
    @Select("SELECT * FROM favorites WHERE href = #{href}")
    List<Favorite> FindFavoriteByHref(@Param("href")String href);

}