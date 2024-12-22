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
import dawnchase.backend.service.EmailService;
import dawnchase.backend.service.FavoriteService;
import dawnchase.backend.service.UserService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


import dawnchase.backend.model.Product;
import dawnchase.backend.service.ProductService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://10.162.194.150:5173/")
public class ProductController {

    @Autowired
    private ProductService ProductService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private EmailService emailService;

    @Value("${chrome.driver.path}")
    private String chromedriverPath;

    private void scrollPage(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    @PostMapping("/products")
    public List<Map<String, String>> getProducts(@RequestBody Map<String, Object> params) {

        String query = (String) params.get("query");
        List<String> categories = (List<String>) params.get("platforms");
        String category = "";
        for (String c : categories) {
            System.out.println("c: " + c);
            category += c;
        }

        System.out.println("query: " + query);
        System.out.println("category: " + category);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--enable-unsafe-webgl");
        options.addArguments("--disable-software-rasterizer");
        options.addArguments("--log-level=3");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("excludeSwitches", "enable-logging");

        // 使用properties中的设置路径
        System.setProperty("webdriver.chrome.driver", chromedriverPath);

        String url_jd = "https://re.jd.com/search?keyword=" + query + "&enc=utf-8";
        String url_sn = "https://search.suning.com/" + query + "/";

        // 用于存储商品数据
        List<Map<String, String>> products = new ArrayList<>();

        if (category.contains("sn")) {
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
                    WebElement priceElement = productElement.findElement(By.xpath(".//div[@class='price-box']/span[@class='def-price']"));
                    price = (priceElement.getText()).replaceAll("[^0-9.]", "");
                    while (price.isEmpty()) {
                        scrollPage(driver);
                        Thread.sleep(200); // 等待页面加载
                        priceElement = productElement.findElement(By.xpath(".//div[@class='price-box']/span[@class='def-price']"));
                        price = (priceElement.getText()).replaceAll("[^0-9.]", "");
                    }
                    product.put("price", price);
                } catch (Exception e) {
                    product.put("price", "N/A");
                }
                System.out.println("price: " + price);

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

        if (category.contains("jd")) {
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

    private void InsertToProducts(String category, String href, String imgSrc, String price, String title, String store) {

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

        ProductService.InsertProduct(NewProduct);

        if (price.equals("N/A") || price.equals(""))
            return;
        // 收藏的商品是否有降价
        double Price = Double.parseDouble(price);
        List<Favorite> favorites = favoriteService.FindFavoriteByHref(href);
        for (Favorite favorite : favorites) {
            if (favorite.getPrice() > Price) {
                System.out.println("href: " + href);
                System.out.println("商品降价");
                // 发送邮件
                String to = favorite.getEmail();
                String subject = "商品降价通知";
                String content = "您收藏的商品 " + title + " 降价了，当前价格为 " + Price + " 元";
                emailService.sendSimpleEmail(to, subject, content);
            }
            if (favorite.getPrice() != Price)
                favoriteService.InsertToFavorites(href, favorite.getUsername(), price, favorite.getEmail());
        }
    }

}