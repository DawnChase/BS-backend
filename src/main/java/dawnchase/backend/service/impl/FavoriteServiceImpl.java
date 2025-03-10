package dawnchase.backend.service.impl;

import dawnchase.backend.mapper.FavoriteMapper;
import dawnchase.backend.model.Favorite;
import dawnchase.backend.service.EmailService;
import dawnchase.backend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private EmailService emailService;

    public List<Favorite> SelectAll() {
        return favoriteMapper.SelectAll();
    }

    public void InsertFavorite(Favorite favorite) {
        favoriteMapper.InsertFavorite(favorite);
    }

    public void DeleteFavorite(String href, String username) {
        favoriteMapper.DeleteFavorite(href, username);
    }

    public List<Favorite> FindFavorite(String href, String username) {
        return favoriteMapper.FindFavorite(href, username);
    }

    public List<String> FindFavoriteByUsername(String username) {
        return favoriteMapper.FindFavoriteByUsername(username);
    }

    public List<Favorite> FindFavoriteByHref(String href) {
        return favoriteMapper.FindFavoriteByHref(href);
    }

    public void CheckPrice(String href, String title, double Price) {
        // 寻找最新的商品价格，并比较是否降价
        List<Favorite> favorites = FindFavoriteByHref(href);
        if (favorites.size() == 0)
        {
//            System.out.println("没有找到收藏夹中的商品");
            return;
        }
        Favorite Newestfavorite = favorites.get(0);
        for (Favorite favorite : favorites)
            if (favorite.getTimestamp().compareTo(Newestfavorite.getTimestamp()) > 0)
                Newestfavorite = favorite;

        if (Price < Newestfavorite.getPrice()) {
            System.out.println("href: " + href);
            System.out.println("商品降价");
            // 发送邮件
            String to = Newestfavorite.getEmail();
            String subject = "商品降价通知";
            String content = "您收藏的商品 " + title + " 降价了，当前价格为 " + Price + " 元";
            emailService.sendSimpleEmail(to, subject, content);
        }
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNow = now.format(formatter);
        Newestfavorite.setTimestamp(formattedNow);
        Newestfavorite.setPrice(Price);
        InsertFavorite(Newestfavorite);
    }

    public void InsertToFavorites(String href, String username, String price, String email) {
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

        InsertFavorite(NewFavorite);
    }
}
