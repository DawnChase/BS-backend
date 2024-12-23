package dawnchase.backend.service;

import dawnchase.backend.model.Product;

import java.util.List;
import java.util.Map;

public interface ProductService {

    public void InsertProduct(Product product);

    public Product FindProduct(String href);

    public void InsertToProducts(String category, String href, String imgSrc, String price, String title, String store) ;

    public List<Map<String, String>> getProducts(String category, String query);
}