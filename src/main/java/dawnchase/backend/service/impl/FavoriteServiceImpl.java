package dawnchase.backend.service.impl;

import dawnchase.backend.mapper.FavoriteMapper;
import dawnchase.backend.model.Favorite;
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
