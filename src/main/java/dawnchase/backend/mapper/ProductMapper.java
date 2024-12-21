package dawnchase.backend.mapper;

import dawnchase.backend.model.Product;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ProductMapper {

    @Insert("INSERT INTO products (detail_href, img, price, title, timestamp) VALUES (#{detail_href}, #{img}, #{price}, #{title}, #{timestamp})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void InsertProduct(Product product);

}