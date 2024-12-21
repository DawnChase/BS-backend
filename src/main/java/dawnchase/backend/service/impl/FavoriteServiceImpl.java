package dawnchase.backend.service.impl;

import dawnchase.backend.mapper.FavoriteMapper;
import dawnchase.backend.model.Favorite;
import dawnchase.backend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Favorite FindFavorite(String href, String username) {
        return favoriteMapper.FindFavorite(href, username);
    }

    public List<String> FindFavoriteByUsername(String username) {
        return favoriteMapper.FindFavoriteByUsername(username);
    }

    public List<Favorite> FindFavoriteByHref(String href) {
        return favoriteMapper.FindFavoriteByHref(href);
    }
}
