package dawnchase.backend.controller;

import dawnchase.backend.model.Favorite;
import dawnchase.backend.model.Product;
import dawnchase.backend.model.User;
import dawnchase.backend.service.FavoriteService;
import dawnchase.backend.service.ProductService;
import dawnchase.backend.service.UserService;
import dawnchase.backend.service.impl.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class FavoriteController {

    @Autowired
    private ProductService productService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserService userService;
    @Autowired
    private ProductController productController;
    @Autowired
    private ProductServiceImpl productServiceImpl;

    @PostMapping("/addToFavorites")
    public Map<String, String> addToFavorites(@RequestBody Map<String, String> params) {

        String href = params.get("href");
        String username = params.get("username");
        String price = params.get("price");
        System.out.println("href: " + href);
        System.out.println("username: " + username);

        List<Favorite> favorite = favoriteService.FindFavorite(href, username);
        if (!favorite.isEmpty())
        {
            System.out.println("商品已经收藏");
            Map<String, String> response = new HashMap<>();
            response.put("message", "商品已经收藏");
            return response;
        }

        User user = userService.findUserByUsername(username);
        String email = user.getEmail();

        favoriteService.InsertToFavorites(href, username, price, email);

        System.out.println("添加收藏成功");

        Map<String, String> response = new HashMap<>();
        response.put("message", "添加收藏成功");
        return response;
    }

    @PostMapping("/deleteFromFavorites")
    public Map<String, String> deleteFromFavorites(@RequestBody Map<String, String> params) {

        String href = params.get("href");
        String username = params.get("username");

        System.out.println("href: " + href);
        System.out.println("username: " + username);

        List<Favorite> favorite = favoriteService.FindFavorite(href, username);
        if (favorite == null)
        {
            System.out.println("商品尚未收藏");
            Map<String, String> response = new HashMap<>();
            response.put("message", "商品尚未收藏");
            return response;
        }

        favoriteService.DeleteFavorite(href, username);

        Map<String, String> response = new HashMap<>();
        System.out.println("删除收藏成功");
        response.put("message", "删除收藏成功");
        return response;
    }

    @PostMapping("/displayFavorites")
    public List<Map<String, String>> displayFavorites(@RequestBody Map<String, String> params) {

        String username = params.get("username");
        System.out.println("username: " + username);
        String email = params.get("email");
        System.out.println("email: " + email);

        List<String> favoriteHrefs = favoriteService.FindFavoriteByUsername(username);
        // 输出收藏夹商品数
        System.out.println("收藏夹商品数: " + favoriteHrefs.size());

        List<Map<String, String>> products = new ArrayList<>();

        for (String favoriteHref : favoriteHrefs) {
            Map<String, String> product = new HashMap<>();
//            System.out.println("favorite href: " + favoriteHref);
            Product currentProduct = productService.FindProduct(favoriteHref);
            String category = currentProduct.getCategory();
            if (category.equals("tb")) product.put("category", "淘宝");
            if (category.equals("sn")) product.put("category", "苏宁易购");
            if (category.equals("jd")) product.put("category", "京东");
            product.put("href", currentProduct.getHref());
            product.put("price", String.valueOf(currentProduct.getPrice()));
            product.put("imgSrc", currentProduct.getImgSrc());
            product.put("title", currentProduct.getTitle());
            product.put("store", currentProduct.getStore());

            products.add(product);
        }

        return products;

    }

    @PostMapping("/priceHistory")
    public List<Map<String, String>> priceHistory(@RequestBody Map<String, String> params) {

        String href = params.get("href");
        System.out.println("price history href: " + href);

        List<Favorite> favorites = favoriteService.FindFavoriteByHref(href);
        List<Map<String, String>> priceHistory = new ArrayList<>();

        for (Favorite favorite : favorites) {
            Map<String, String> price = new HashMap<>();
            price.put("price", String.valueOf(favorite.getPrice()));
            price.put("timestamp", favorite.getTimestamp());
            priceHistory.add(price);
            System.out.println("price: " + favorite.getPrice());
            System.out.println("timestamp: " + favorite.getTimestamp());
        }

        return priceHistory;

    }

    @PostMapping("/updatePrice")
    public Map<String, String> updatePrice(@RequestBody Map<String, String> params) {

        String href = params.get("href");
        String username = params.get("username");
        String price = params.get("price");
        String email = params.get("email");

        Product product = productService.FindProduct(href);
        productService.InsertToProducts(product.getCategory(), href, product.getImgSrc(), price, product.getTitle(), product.getStore());
        favoriteService.InsertToFavorites(href, username, price, email);

        Map<String, String> response = new HashMap<>();
        response.put("message", "价格更新成功");
        return response;

    }



}
