package org.lelele.slayseed.service;

import org.lelele.slayseed.entity.Favorite;
import org.lelele.slayseed.entity.Seed;
import org.lelele.slayseed.entity.User;
import org.lelele.slayseed.repository.FavoriteRepository;
import org.lelele.slayseed.repository.SeedRepository;
import org.lelele.slayseed.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private SeedRepository seedRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public boolean toggleFavorite(Long seedId, Long userId) {
        if (favoriteRepository.existsByUserIdAndSeedId(userId, seedId)) {
            favoriteRepository.deleteByUserIdAndSeedId(userId, seedId);
            seedRepository.decrementFavoriteCount(seedId);
            return false;
        } else {
            Seed seed = seedRepository.findById(seedId).orElseThrow(() -> new RuntimeException("种子不存在"));
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
            Favorite favorite = new Favorite();
            favorite.setSeed(seed);
            favorite.setUser(user);
            favoriteRepository.save(favorite);
            seedRepository.incrementFavoriteCount(seedId);
            return true;
        }
    }

    public boolean isFavorited(Long seedId, Long userId) {
        return favoriteRepository.existsByUserIdAndSeedId(userId, seedId);
    }

    public Set<Long> getFavoritedSeedIds(Long userId) {
        return favoriteRepository.findFavoritedSeedIdsByUserId(userId).stream().collect(Collectors.toSet());
    }
}