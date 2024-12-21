package dawnchase.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.fasterxml.jackson.databind.SerializationFeature;
import dawnchase.backend.model.Favorite;
import dawnchase.backend.model.User;
import dawnchase.backend.service.FavoriteService;
import dawnchase.backend.service.UserService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import dawnchase.backend.model.Product;
import dawnchase.backend.service.ProductService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    @Autowired
    private ProductService ProductService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserService userService;

    @PostMapping("/products")
    public List<Map<String, String>> getProducts(@RequestBody Map<String, String> params) {

        String query = params.get("query");
        String category = params.get("category");
        System.out.println("query: " + query);
        System.out.println("category: " + category);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--enable-unsafe-webgl");
        options.addArguments("--disable-software-rasterizer");
        options.addArguments("--log-level=3");
        options.addArguments("excludeSwitches", "enable-logging");

        String chromedriverPath = new File("chromedriver.exe").getAbsolutePath();
        System.setProperty("webdriver.chrome.driver", chromedriverPath);

        String url_jd = "https://re.jd.com/search?keyword=" + query + "&enc=utf-8";
        String url_sn = "https://search.suning.com/" + query + "/";

        // 用于存储商品数据
        List<Map<String, String>> products = new ArrayList<>();

        if (category.equals("sn") | category.equals("all")) {
            System.out.println("category: sn");
            WebDriver driver = new ChromeDriver(options);
            driver.get(url_sn);
            // 显式等待，确保页面加载完成
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'item-bg')]")));
            System.out.println("页面加载完成");
            // 找到所有的商品列表项
            List<WebElement> productElements = driver.findElements(By.xpath("//div[contains(@class, 'item-bg')]"));
            System.out.println("商品数量: " + productElements.size());
            // 使用 HashSet 来存储唯一的商品详情链接
            Set<String> uniqueDetailLinks = new HashSet<>();
            // 遍历商品元素，提取所需信息
            for (WebElement productElement : productElements) {
                Map<String, String> product = new HashMap<>();

                String href = "";
                String imgSrc = "";
                String price = "";
                String title = "";
                String store = "";

                try {
                    // 提取商品详情链接 (href)
                    WebElement linkElement = productElement.findElement(By.xpath(".//div[@class='title-selling-point']/a"));
                    href = linkElement.getAttribute("href");
//                        System.out.println("href: " + href);
                    product.put("href", href);
                } catch (Exception e) {
                    product.put("href", "N/A");
                }

                // 如果商品详情链接已经存在，则跳过
                if (uniqueDetailLinks.contains(href))
                    continue;

                uniqueDetailLinks.add(href);

                try {
                    // 提取图片链接 (img_k)
                    WebElement imgElement = productElement.findElement(By.xpath(".//img[@tabindex='-1']"));
                    imgSrc = imgElement.getAttribute("src");
//                        System.out.println("imgSrc: " + imgSrc);
                    product.put("imgSrc", imgSrc);
                } catch (Exception e) {
                    product.put("imgSrc", "N/A");
                }

                try {
                    // 提取价格 (price)
                    WebDriverWait priceWait = new WebDriverWait(driver, Duration.ofSeconds(20));
                    WebElement priceElement = priceWait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath(".//div[@class='price-box']/span[@class='def-price']")));
                    price = (priceElement.getText()).replaceAll("[^0-9.]", "");
//                    System.out.println("price: " + price);
                    product.put("price", price);
                } catch (Exception e) {
                    product.put("price", "N/A");
                }

                try {
                    // 提取商品标题 (title)
                    WebElement titleElement = productElement.findElement(By.xpath(".//img[@tabindex='-1']"));
                    title = titleElement.getAttribute("alt");
//                        System.out.println("title: " + title);
                    product.put("title", title);
                } catch (Exception e) {
                    product.put("title", "N/A");
                }

                try {
                    // 提取商家 (store)
                    WebElement StoreElement = productElement.findElement(By.xpath(".//div[@class='store-stock']"));
                    store = StoreElement.getText();
//                        System.out.println("store: " + store);
                    product.put("store", store);
                } catch (Exception e) {
                    product.put("store", "N/A");
                }

                product.put("category", "苏宁易购");
                // 将商品数据添加到列表中
                products.add(product);

                InsertToProducts("sn", href, imgSrc, price, title, store);
            }

            // 关闭浏览器
            driver.quit();

        }

        if (category.equals("jd") | category.equals("all")) {
            System.out.println("category: jd");
            WebDriver driver = new ChromeDriver(options);
            driver.get(url_jd);
            // 显式等待，确保页面加载完成
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'li_cen')]")));
            System.out.println("页面加载完成");
            // 找到所有的商品列表项
            List<WebElement> productElements = driver.findElements(By.xpath("//div[contains(@class, 'li_cen')]"));
            System.out.println("商品数量: " + productElements.size());
            // 使用 HashSet 来存储唯一的商品详情链接
            Set<String> uniqueDetailLinks = new HashSet<>();
            // 遍历商品元素，提取所需信息
            for (WebElement productElement : productElements) {
                Map<String, String> product = new HashMap<>();

                String href = "";
                String imgSrc = "";
                String price = "";
                String title = "";
                String store = "";

                try {
                    // 提取商品详情链接 (href)
                    WebElement linkElement = productElement.findElement(By.xpath(".//a"));
                    href = linkElement.getAttribute("href");
                    product.put("href", href);
                } catch (Exception e) {
                    product.put("href", "N/A");
                }
                // 如果商品详情链接已经存在，则跳过
                if (uniqueDetailLinks.contains(href))
                    continue;

                uniqueDetailLinks.add(href);

                try {
                    // 提取图片链接 (img_k)
                    WebElement imgElement = productElement.findElement(By.xpath(".//img[@class='img_k']"));
                    imgSrc = imgElement.getAttribute("src");
                    product.put("imgSrc", imgSrc);
                } catch (Exception e) {
                    product.put("imgSrc", "N/A");
                }

                try {
                    // 提取价格 (price)
                    WebElement priceElement = productElement.findElement(By.xpath(".//div[@class='commodity_info']/span[@class='price']"));
                    price = (priceElement.getText()).replaceAll("[^0-9.]", "");
                    product.put("price", price);
                } catch (Exception e) {
                    product.put("price", "N/A");
                }

                try {
                    // 提取商品标题 (commodity_tit)
                    WebElement titleElement = productElement.findElement(By.xpath(".//div[@class='commodity_tit']"));
                    title = titleElement.getText();
                    product.put("title", title);
                } catch (Exception e) {
                    product.put("title", "N/A");
                }

                product.put("category", "京东");
                // 将商品数据添加到列表中
                products.add(product);

                InsertToProducts("jd", href, imgSrc, price, title, store);
            }

            // 关闭浏览器
            driver.quit();

        }

        System.out.println("商品爬取完成");
        return products;

    }

    @PostMapping("/addToFavorites")
    public Map<String, String> addToFavorites(@RequestBody Map<String, String> params) {

        String href = params.get("href");
        String username = params.get("username");
        String price = params.get("price");

        Favorite favorite = favoriteService.FindFavorite(href, username);
        if (favorite != null)
        {
            System.out.println("商品已经收藏");
            Map<String, String> response = new HashMap<>();
            response.put("message", "商品已经收藏");
            return response;
        }

        User user = userService.findUserByUsername(username);
        String email = user.getEmail();

        InsertToFavorites(href, username, price, email);

        System.out.println("添加收藏成功");

        Map<String, String> response = new HashMap<>();
        response.put("message", "添加收藏成功");
        return response;
    }

    @PostMapping("/deleteFromFavorites")
    public Map<String, String> deleteFromFavorites(@RequestBody Map<String, String> params) {

        String href = params.get("href");
        String username = params.get("username");

        Favorite favorite = favoriteService.FindFavorite(href, username);
        if (favorite != null)
        {
            System.out.println("商品尚未收藏");
            Map<String, String> response = new HashMap<>();
            response.put("message", "商品尚未收藏");
            return response;
        }

        favoriteService.DeleteFavorite(href, username);

        Map<String, String> response = new HashMap<>();
        response.put("message", "删除收藏成功");
        return response;
    }

    @PostMapping("/displayFavorites")
    public List<Map<String, String>> displayFavorites(@RequestBody Map<String, String> params) {

        String username = params.get("username");
        System.out.println("username: " + username);

        List<Favorite> favorites = favoriteService.FindFavoriteByUsername(username);
        // 输出收藏夹商品数
        System.out.println("收藏夹商品数: " + favorites.size());

        List<Map<String, String>> products = new ArrayList<>();

        for (Favorite favorite : favorites) {
            Map<String, String> product = new HashMap<>();
//            System.out.println("favorite href: " + favorite.getHref());
            Product currentProduct = ProductService.FindProduct(favorite.getHref());

            String category = currentProduct.getCategory();
            System.out.println("category: " + category);
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

    private void InsertToFavorites(String href, String username, String price, String email) {

        Favorite NewFavorite = new Favorite();

        NewFavorite.setHref(href);
        NewFavorite.setUsername(username);

        double Price = Double.parseDouble(price);
        NewFavorite.setPrice(Price);

        NewFavorite.setEmail(email);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNow = now.format(formatter);
        NewFavorite.setTimestamp(formattedNow);

        favoriteService.InsertFavorite(NewFavorite);
    }


    private void InsertToProducts(String category, String href, String imgSrc, String price, String title, String store) {

        Product NewProduct = new Product();

        NewProduct.setCategory(category);
        NewProduct.setHref(href);
        NewProduct.setImgSrc(imgSrc);

        double Price = Double.parseDouble(price);
        NewProduct.setPrice(Price);

        NewProduct.setTitle(title);
        NewProduct.setStore(store);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNow = now.format(formatter);
        NewProduct.setTimestamp(formattedNow);

        ProductService.InsertProduct(NewProduct);
    }

}