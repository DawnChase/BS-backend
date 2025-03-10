package dawnchase.backend.mapper;

import dawnchase.backend.model.Product;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ProductMapper {

    @Insert("INSERT INTO products (category, href, imgSrc, price, title, store, timestamp) VALUES (#{category}, #{href}, #{imgSrc}, #{price}, #{title}, #{store}, #{timestamp})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void InsertProduct(Product product);

    // 用于展示收藏夹
    // 取同名中timestamp最大的记录（最接近现在）
    @Select("SELECT * FROM products WHERE href = #{href} ORDER BY timestamp DESC LIMIT 1")
    Product FindProduct(@Param("href")String href);

}