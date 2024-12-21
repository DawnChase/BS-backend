package dawnchase.backend.mapper;

import dawnchase.backend.model.Product;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ProductMapper {

    @Insert("INSERT INTO products (category, href, imgSrc, price, title, store, timestamp) VALUES (#{category}, #{href}, #{imgSrc}, #{price}, #{title}, #{store}, #{timestamp})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void InsertProduct(Product product);

}