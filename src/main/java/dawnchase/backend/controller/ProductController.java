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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;


import dawnchase.backend.model.Product;
import dawnchase.backend.service.ProductService;

@RestController
@Component
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

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

        List<Map<String, String>> products = productService.getProducts(category, query);

        System.out.println("选中平台的商品爬取完成");
        return products;

    }


}