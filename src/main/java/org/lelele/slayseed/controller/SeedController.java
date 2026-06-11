package org.lelele.slayseed.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.lelele.slayseed.dto.SeedDTO;
import org.lelele.slayseed.dto.SeedRequest;
import org.lelele.slayseed.entity.User;
import org.lelele.slayseed.repository.UserRepository;
import org.lelele.slayseed.service.FavoriteService;
import org.lelele.slayseed.service.LikeService;
import org.lelele.slayseed.service.SeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/seeds")
@CrossOrigin(origins = "*")
public class SeedController {

    @Autowired
    private SeedService seedService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Page<SeedDTO>> getSeeds(
            @RequestParam(required = false) String tower,
            @RequestParam(required = false) String towerCharacter,
            @RequestParam(required = false) String playerCount,
            @RequestParam(required = false) String seedType,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        Set<Long> likedIds = currentUserId != null ? likeService.getLikedSeedIds(currentUserId) : null;
        Set<Long> favoritedIds = currentUserId != null ? favoriteService.getFavoritedSeedIds(currentUserId) : null;
        return ResponseEntity.ok(seedService.getSeeds(tower, towerCharacter, playerCount, seedType, sortBy, page, size, currentUserId, likedIds, favoritedIds));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<SeedDTO>> searchSeeds(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        Set<Long> likedIds = currentUserId != null ? likeService.getLikedSeedIds(currentUserId) : null;
        Set<Long> favoritedIds = currentUserId != null ? favoriteService.getFavoritedSeedIds(currentUserId) : null;
        return ResponseEntity.ok(seedService.searchSeeds(keyword, page, size, currentUserId, likedIds, favoritedIds));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeedDTO> getSeedById(@PathVariable Long id, Authentication authentication,
                                                HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(authentication);
        String clientIp = request.getRemoteAddr();
        SeedDTO seed = seedService.getSeedById(id, currentUserId, clientIp);
        if (seed == null) {
            return ResponseEntity.notFound().build();
        }
        if (currentUserId != null) {
            seed.setLikedByCurrentUser(likeService.isLiked(id, currentUserId));
            seed.setFavoritedByCurrentUser(favoriteService.isFavorited(id, currentUserId));
        }
        return ResponseEntity.ok(seed);
    }

    @PostMapping
    public ResponseEntity<?> createSeed(@Valid @RequestBody SeedRequest request, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "请先登录");
            return ResponseEntity.status(401).body(error);
        }
        try {
            return ResponseEntity.ok(seedService.createSeed(request, userId));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSeed(@PathVariable Long id, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "请先登录");
            return ResponseEntity.status(401).body(error);
        }
        try {
            seedService.deleteSeed(id, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long id, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "请先登录");
            return ResponseEntity.status(401).body(error);
        }
        boolean liked = likeService.toggleLike(id, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<?> toggleFavorite(@PathVariable Long id, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "请先登录");
            return ResponseEntity.status(401).body(error);
        }
        boolean favorited = favoriteService.toggleFavorite(id, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("favorited", favorited);
        return ResponseEntity.ok(result);
    }

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        return userRepository.findByUsername(authentication.getName())
                .map(User::getId)
                .orElse(null);
    }
}