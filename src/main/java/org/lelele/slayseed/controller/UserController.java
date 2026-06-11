package org.lelele.slayseed.controller;

import org.lelele.slayseed.dto.SeedDTO;
import org.lelele.slayseed.dto.UserDTO;
import org.lelele.slayseed.entity.User;
import org.lelele.slayseed.repository.UserRepository;
import org.lelele.slayseed.service.FavoriteService;
import org.lelele.slayseed.service.FollowService;
import org.lelele.slayseed.service.LikeService;
import org.lelele.slayseed.service.SeedService;
import org.lelele.slayseed.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SeedService seedService;

    @Autowired
    private FollowService followService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(userService.getCurrentUser(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id, Authentication authentication) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/seeds")
    public ResponseEntity<Page<SeedDTO>> getUserSeeds(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        Set<Long> likedIds = currentUserId != null ? likeService.getLikedSeedIds(currentUserId) : null;
        Set<Long> favoritedIds = currentUserId != null ? favoriteService.getFavoritedSeedIds(currentUserId) : null;
        return ResponseEntity.ok(seedService.getSeedsByAuthor(id, page, size, currentUserId, likedIds, favoritedIds));
    }

    @GetMapping("/{id}/favorites")
    public ResponseEntity<Page<SeedDTO>> getUserFavorites(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        Set<Long> likedIds = currentUserId != null ? likeService.getLikedSeedIds(currentUserId) : null;
        Set<Long> favoritedIds = currentUserId != null ? favoriteService.getFavoritedSeedIds(currentUserId) : null;
        return ResponseEntity.ok(seedService.getFavoriteSeeds(id, page, size, currentUserId, likedIds, favoritedIds));
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<Page<UserDTO>> getFollowers(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(followService.getFollowers(id, page, size));
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<Page<UserDTO>> getFollowing(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(followService.getFollowing(id, page, size));
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<?> toggleFollow(@PathVariable Long id, Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        if (currentUserId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "请先登录");
            return ResponseEntity.status(401).body(error);
        }
        try {
            boolean following = followService.toggleFollow(currentUserId, id);
            Map<String, Object> result = new HashMap<>();
            result.put("following", following);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}/is-following")
    public ResponseEntity<Boolean> isFollowing(@PathVariable Long id, Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        if (currentUserId == null) {
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(followService.isFollowing(currentUserId, id));
    }

    @PutMapping("/me/profile")
    public ResponseEntity<?> updateProfile(
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String bio,
            @RequestParam(required = false) String avatar,
            Authentication authentication) {
        Long currentUserId = getCurrentUserId(authentication);
        if (currentUserId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "请先登录");
            return ResponseEntity.status(401).body(error);
        }
        return ResponseEntity.ok(userService.updateProfile(currentUserId, nickname, bio, avatar));
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