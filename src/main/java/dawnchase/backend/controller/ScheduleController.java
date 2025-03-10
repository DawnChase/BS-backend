package dawnchase.backend.controller;

import dawnchase.backend.model.Favorite;
import dawnchase.backend.model.Product;
import dawnchase.backend.service.FavoriteService;
import dawnchase.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ScheduleController {

    @Autowired
    private ProductService productService;

    @Autowired
    private FavoriteService favoriteService;

//    @Scheduled(cron = "*/5 * * * * ?")
//    private void printNowDate() {
//        long nowDateTime = System.currentTimeMillis();
//        System.out.println("固定定时任务执行:--->"+nowDateTime+"，此任务为每五秒执行一次");
//    }

    @Scheduled(cron = "0 44 14 * * ?") // 每天晚上20:44（UTC+8）执行
    public void scheduledCrawl() {
        System.out.println("定时爬取商品");
        List<Favorite> favorites = favoriteService.SelectAll();
        Set<String> uniqueHrefs = new HashSet<>();

        for (Favorite favorite : favorites) {
            String href = favorite.getHref();
            // 根据href去重
            if (!uniqueHrefs.contains(href)) {
                uniqueHrefs.add(href);
                Product product = productService.FindProduct(href);
                System.out.println("category: " + product.getCategory());
                System.out.println("query: " + product.getTitle());
                System.out.println("href: " + product.getHref());
                productService.getProducts(product.getCategory(), product.getTitle());
            }
        }
    }
}