package dawnchase.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.fasterxml.jackson.databind.SerializationFeature;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import dawnchase.backend.model.Product;
import dawnchase.backend.service.ProductService;

@RestController
public class ProductController {

    @Autowired
    private ProductService ProductService;


    @GetMapping("/api/products")
    public List<Map<String, Object>> getProducts() {
        // 固定输入的 query 和 category
        String query = "红米k80";
        String category = "jd";
    // public List<Map<String, Object>> getProducts(@RequestParam String query, @RequestParam String category) {
//        try {

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--enable-unsafe-webgl");
            options.addArguments("--disable-software-rasterizer");
            options.addArguments("--log-level=3");
            options.addArguments("excludeSwitches", "enable-logging");

            String chromedriverPath = new File("chromedriver.exe").getAbsolutePath();
            System.setProperty("webdriver.chrome.driver", chromedriverPath);
            WebDriver driver = new ChromeDriver(options);

            String url_jd = "https://re.jd.com/search?keyword=" + query + "&enc=utf-8";
            String url_sn = "https://search.suning.com/" + query + "/";

            System.out.println("url_jd: " + url_jd);
            System.out.println("url_sn: " + url_sn);
            if (category.equals("sn")) {
                driver.get(url_jd);
                // 等待页面加载完成
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//ul[@class='general clearfix']/li")));

                List<WebElement> liElements = driver.findElements(By.xpath("//ul[@class='general clearfix']/li"));

                // 用于存储爬取的商品数据
                List<Map<String, String>> productList = new ArrayList<>();

                // 遍历每个 li 元素，提取所需数据
                for (WebElement li : liElements) {
                    System.out.println("li: " + li);

                    Map<String, String> product = new HashMap<>();

                    try {
                        // 提取标题
                        WebElement titleElement = li.findElement(By.xpath(".//div[@class='res-info']/div[@class='title-selling-point']/a[1]"));
                        product.put("title", titleElement.getText().replace("\n", ""));
                    } catch (Exception e) {
                        product.put("title", "");
                    }

                    try {
                        // 提取价格
                        WebElement priceElement = li.findElement(By.xpath(".//div[@class='price-box']/span[1]"));
                        product.put("price", priceElement.getText().replace("\n", ""));
                    } catch (Exception e) {
                        product.put("price", "");
                    }

                    try {
                        // 提取类型
                        WebElement typeElement = li.findElement(By.xpath(".//div[@class='info-config']"));
                        product.put("type", typeElement.getAttribute("title"));
                    } catch (Exception e) {
                        product.put("type", "");
                    }

                    try {
                        // 提取评论数
                        WebElement commentNumElement = li.findElement(By.xpath(".//div[@class='info-evaluate']/a[1]"));
                        product.put("comment_num", commentNumElement.getText());
                    } catch (Exception e) {
                        product.put("comment_num", "");
                    }

                    try {
                        // 提取商店名称
                        WebElement shopElement = li.findElement(By.xpath(".//div[@class='store-stock']/a[1]"));
                        product.put("shop", shopElement.getText());
                    } catch (Exception e) {
                        product.put("shop", "");
                    }

                    try {
                        // 提取标签
                        List<WebElement> labelElements = li.findElements(By.xpath(".//div[@class='sales-label']//text()"));
                        StringBuilder labels = new StringBuilder();
                        for (WebElement label : labelElements) {
                            labels.append(label.getText()).append(",");
                        }
                        product.put("label", labels.toString().replaceAll(",$", "")); // 去掉末尾的逗号
                    } catch (Exception e) {
                        product.put("label", "");
                    }

                    try {
                        // 提取图片链接
                        WebElement imgElement = li.findElement(By.xpath(".//div[@class='img-block']/a[1]/img"));
                        String imgLink = imgElement.getAttribute("src");
                        product.put("img_link", imgLink.startsWith("http") ? imgLink : "http:" + imgLink);
                    } catch (Exception e) {
                        product.put("img_link", "");
                    }

                    try {
                        // 提取详情链接
                        WebElement detailElement = li.findElement(By.xpath(".//div[@class='res-info']/div[@class='title-selling-point']/a[1]"));
                        String detailLink = detailElement.getAttribute("href");
                        product.put("detail_link", detailLink.startsWith("http") ? detailLink : "http:" + detailLink);
                    } catch (Exception e) {
                        product.put("detail_link", "");
                    }

                    // 添加商品信息到列表中
                    productList.add(product);
                }

                // 打印结果
                for (Map<String, String> product : productList) {
                    System.out.println(product);
                }

                return null;
            }
            else if (category.equals("jd")) {
                System.out.println("category: jd");
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
                // 用于存储商品数据
                List<Map<String, String>> products = new ArrayList<>();
                // 遍历商品元素，提取所需信息
                for (WebElement productElement : productElements) {
                    Map<String, String> product = new HashMap<>();

                    String href = "";
                    String imgSrc = "";
                    String price = "";
                    String title = "";

                    try {
                        // 提取商品详情链接 (href)
                        WebElement linkElement = productElement.findElement(By.xpath(".//a"));
                        href = linkElement.getAttribute("href");
                        product.put("detail_href", href);
                    } catch (Exception e) {
                        product.put("detail_href", "N/A");
                    }
                    // 如果商品详情链接已经存在，则跳过
                    if (uniqueDetailLinks.contains(href))
                        continue;

                    uniqueDetailLinks.add(href);

                    try {
                        // 提取图片链接 (img_k)
                        WebElement imgElement = productElement.findElement(By.xpath(".//img[@class='img_k']"));
                        imgSrc = imgElement.getAttribute("src");
                        product.put("img_src", imgSrc);
                    } catch (Exception e) {
                        product.put("img_src", "N/A");
                    }

                    try {
                        // 提取价格 (price)
                        WebElement priceElement = productElement.findElement(By.xpath(".//div[@class='commodity_info']/span[@class='price']"));
                        price = priceElement.getText();
                        product.put("price", price);
                    } catch (Exception e) {
                        product.put("price", "N/A");
                    }

                    try {
                        // 提取商品标题 (commodity_tit)
                        WebElement titleElement = productElement.findElement(By.xpath(".//div[@class='commodity_tit']"));
                        title = titleElement.getText();
                        product.put("commodity_tit", title);
                    } catch (Exception e) {
                        product.put("commodity_tit", "N/A");
                    }

                    // 将商品数据添加到列表中
                    products.add(product);

                    Product NewProduct = new Product();
                    NewProduct.setDetail_href(href);
                    NewProduct.setImg(imgSrc);
                    NewProduct.setPrice(price);
                    NewProduct.setTitle(title);

                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedNow = now.format(formatter);
                    NewProduct.setTimestamp(formattedNow);

                    ProductService.InsertProduct(NewProduct);
                }

                // 打印结果
                for (Map<String, String> product : products) {
                    System.out.println("商品数据: " + product);
                }

                // 关闭浏览器
                driver.quit();

                return null;
            } else {
                return null;
            }

//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
    }

    private void saveToMySQL(List<Map<String, String>> products) {
    }

}