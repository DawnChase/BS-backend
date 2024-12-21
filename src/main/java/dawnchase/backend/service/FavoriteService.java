package dawnchase.backend.service;

import dawnchase.backend.model.Favorite;

import java.util.List;

public interface FavoriteService {

    public void InsertFavorite(Favorite favorite);

    public void DeleteFavorite(String href, String username);

    public Favorite FindFavorite(String href, String username);

    public List<Favorite> FindFavoriteByUsername(String username);
}