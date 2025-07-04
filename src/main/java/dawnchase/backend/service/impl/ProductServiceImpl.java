package dawnchase.backend.service.impl;

import dawnchase.backend.mapper.ProductMapper;
import dawnchase.backend.model.Favorite;
import dawnchase.backend.model.Product;
import dawnchase.backend.service.EmailService;
import dawnchase.backend.service.FavoriteService;
import dawnchase.backend.service.ProductService;
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
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper ProductMapper;

    @Autowired
    private FavoriteService favoriteService;


    @Value("${chrome.driver.path}")
    private String chromedriverPath;

    private void scrollPage(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }


    public void InsertProduct(Product product) {
        ProductMapper.InsertProduct(product);
    }

    public Product FindProduct(String href) {
        return ProductMapper.FindProduct(href);
    }

    public void InsertToProducts(String category, String href, String imgSrc, String price, String title, String store) {

//        System.out.println("href: " + href);

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

    public List<Map<String, String>> getProducts(String category, String query) {

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
        String url_tb = "https://uland.taobao.com/sem/tbsearch?localImgKey=&page=1&q=" + query + "&tab=all";

        // 用于存储商品数据
        List<Map<String, String>> products = new ArrayList<>();

        if (category.contains("sn")) {
            System.out.println("url: " + url_sn);
            WebDriver driver = new ChromeDriver(options);
            driver.get(url_sn);
            // 显式等待，确保页面加载完成
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'item-bg')]")));
            System.out.println("页面加载完成");
            // 直接滚到底部
            try {
                scrollPage(driver);
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 找到所有的商品列表项
            List<WebElement> productElements = driver.findElements(By.xpath("//div[contains(@class, 'item-bg')]"));
            System.out.println("商品数量: " + productElements.size());
            // 使用 HashSet 来存储唯一的商品详情链接
            Set<String> uniqueDetailLinks = new HashSet<>();
            // 遍历商品元素，提取所需信息
            for (WebElement productElement : productElements) {
                Map<String, String> product = new HashMap<>();

                String href = GetElementAttribute(productElement, "href", ".//div[@class='title-selling-point']/a");
                // 如果商品详情链接已经存在，则跳过
                if (uniqueDetailLinks.contains(href))
                    continue;

                uniqueDetailLinks.add(href);

                String imgSrc = GetElementAttribute(productElement, "src", ".//img[@tabindex='-1']");
                String price = GetElementText(productElement, ".//div[@class='price-box']/span[@class='def-price']");
                price = price.replaceAll("[^0-9.]", "");
                String title = GetElementAttribute(productElement, "alt", ".//img[@tabindex='-1']");
                String store = GetElementText(productElement, ".//div[@class='store-stock']");

                product.put("category", "苏宁易购");
                product.put("href", href);
                product.put("imgSrc", imgSrc);
                product.put("price", price);
                product.put("title", title);
                product.put("store", store);

                // 将商品数据添加到列表中
                products.add(product);
                // 插入数据库
                InsertToProducts("sn", href, imgSrc, price, title, store);
            }

            // 关闭浏览器
            driver.quit();

        }

        if (category.contains("jd"))
        {
            System.out.println("url: " + url_jd);
            WebDriver driver = new ChromeDriver(options);
            driver.get(url_jd);
            // 显式等待，确保页面加载完成
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
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

                String href = GetElementAttribute(productElement, "href", ".//a");
                // 如果商品详情链接已经存在，则跳过
                if (uniqueDetailLinks.contains(href))
                    continue;

                uniqueDetailLinks.add(href);

                String imgSrc = GetElementAttribute(productElement, "src", ".//img[@class='img_k']");
                String price = GetElementText(productElement, ".//div[@class='commodity_info']/span[@class='price']");
                price = price.replaceAll("[^0-9.]", "");
                String title = GetElementText(productElement, ".//div[@class='commodity_tit']");
                String store = "";

                product.put("category", "京东");
                product.put("href", href);
                product.put("imgSrc", imgSrc);
                product.put("price", price);
                product.put("title", title);
                product.put("store", store);
                // 将商品数据添加到列表中
                products.add(product);
                // 输出href的长度
//                System.out.println("href Length:" + href.length());
                InsertToProducts("jd", href, imgSrc, price, title, store);
            }

            // 关闭浏览器
            driver.quit();
        }

        if (category.contains("tb")) {
            System.out.println("url: " + url_tb);
            WebDriver driver = new ChromeDriver(options);
            driver.get(url_tb);
            // 显式等待，确保页面加载完成
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@class, 'Card--doubleCardWrapper')]")));
            System.out.println("页面加载完成");
            // 逐步滚动页面，直到底部
            JavascriptExecutor js = (JavascriptExecutor) driver;
            long scrollHeight = (long) js.executeScript("return document.body.scrollHeight");
            long currentHeight = 0;

            while (currentHeight < scrollHeight) {
                currentHeight += 500; // 每次向下滚动500像素
                js.executeScript("window.scrollTo(0, arguments[0]);", currentHeight);
                try {
                    Thread.sleep(500); // 等待页面内容加载（可根据需要调整时间）
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                scrollHeight = (long) js.executeScript("return document.body.scrollHeight"); // 动态更新scrollHeight
            }

            // 找到所有的商品列表项
            List<WebElement> productElements = driver.findElements(By.xpath("//a[contains(@class, 'Card--doubleCardWrapper')]"));
            System.out.println("商品数量: " + productElements.size());
            // 使用 HashSet 来存储唯一的商品详情链接
            Set<String> uniqueDetailLinks = new HashSet<>();
            // 遍历商品元素，提取所需信息
            for (WebElement productElement : productElements) {
                Map<String, String> product = new HashMap<>();

                String href = GetElementAttribute(productElement, "href", ".");
                // 如果商品详情链接已经存在，则跳过
                if (uniqueDetailLinks.contains(href))
                    continue;

                uniqueDetailLinks.add(href);

                String imgSrc = GetElementAttribute(productElement, "src", ".//img[contains(@class, 'MainPic--mainPic')]");
//                System.out.println("imgSrc: " + imgSrc);

                String priceInt = GetElementText(productElement, ".//div[contains(@class, 'Price--priceWrapper')]//span[contains(@class, 'Price--priceInt')]");
                String priceFloat = GetElementText(productElement, ".//div[contains(@class, 'Price--priceWrapper')]//span[contains(@class, 'Price--priceFloat')]");
                String price = (priceInt + priceFloat).replaceAll("[^0-9.]", "");

                String title = GetElementText(productElement, ".//div[contains(@class, 'Title--title')]//span");
                String store = GetElementText(productElement, ".//a[contains(@class, 'ShopInfo--shopName')]");

                product.put("category", "淘宝");
                product.put("href", href);
                product.put("imgSrc", imgSrc);
                product.put("price", price);
                product.put("title", title);
                product.put("store", store);
                // 将商品数据添加到列表中
                products.add(product);

                InsertToProducts("tb", href, imgSrc, price, title, store);
            }

            // 关闭浏览器
            driver.quit();

        }
        System.out.println("商品爬取完成");
        return products;
    }

    private String GetElementText(WebElement productElement, String path) {
        String ans = "";
        try {
            WebElement Element = productElement.findElement(By.xpath(path));
            ans = Element.getText();
        }
        catch (Exception e){
            ans = "N/A";
        }

        return ans;
    }

    private String GetElementAttribute(WebElement productElement, String attribute, String path) {
        String ans = "";
        try {
            WebElement Element = productElement.findElement(By.xpath(path));
            ans = Element.getAttribute(attribute);
        }
        catch (Exception e){
            ans = "N/A";
        }
        return ans;
    }
}
