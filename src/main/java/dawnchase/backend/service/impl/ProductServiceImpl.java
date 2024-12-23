package dawnchase.backend.service.impl;

import dawnchase.backend.mapper.ProductMapper;
import dawnchase.backend.model.Favorite;
import dawnchase.backend.model.Product;
import dawnchase.backend.service.EmailService;
import dawnchase.backend.service.FavoriteService;
import dawnchase.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper ProductMapper;

    @Autowired
    private FavoriteService favoriteService;


    public void InsertProduct(Product product) {
        ProductMapper.InsertProduct(product);
    }

    public Product FindProduct(String href) {
        return ProductMapper.FindProduct(href);
    }

    public void InsertToProducts(String category, String href, String imgSrc, String price, String title, String store) {

        Product NewProduct = new Product();

        NewProduct.setCategory(category);
        NewProduct.setHref(href);
        NewProduct.setImgSrc(imgSrc);

        if (price.equals("N/A") || price.equals(""))
            NewProduct.setPrice(0);
        else
            NewProduct.setPrice(Double.parseDouble(price));

        NewProduct.setTitle(title);
        NewProduct.setStore(store);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNow = now.format(formatter);
        NewProduct.setTimestamp(formattedNow);

        InsertProduct(NewProduct);

        if (price.equals("N/A") || price.equals(""))
            return;
        // 收藏的商品是否有降价
        favoriteService.CheckPrice(href, title, Double.parseDouble(price));
    }

}
