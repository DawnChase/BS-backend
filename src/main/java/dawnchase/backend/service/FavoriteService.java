package dawnchase.backend.service;

import dawnchase.backend.model.Favorite;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public interface FavoriteService {

    public void InsertFavorite(Favorite favorite);

    public void DeleteFavorite(String href, String username);

    public List<Favorite> FindFavorite(String href, String username);

    public List<String> FindFavoriteByUsername(String username);

    public List<Favorite> FindFavoriteByHref(String href);

    public void InsertToFavorites(String href, String username, String price, String email) ;
}