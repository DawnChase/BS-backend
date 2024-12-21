package dawnchase.backend.service.impl;

import dawnchase.backend.mapper.ProductMapper;
import dawnchase.backend.model.Product;
import dawnchase.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper ProductMapper;

    public void InsertProduct(Product product) {
        ProductMapper.InsertProduct(product);
    }

    public Product FindProduct(String href) {
        return ProductMapper.FindProduct(href);
    }

}
