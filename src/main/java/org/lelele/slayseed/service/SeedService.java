package org.lelele.slayseed.service;

import org.lelele.slayseed.dto.SeedDTO;
import org.lelele.slayseed.dto.SeedRequest;
import org.lelele.slayseed.entity.Seed;
import org.lelele.slayseed.entity.User;
import org.lelele.slayseed.repository.SeedRepository;
import org.lelele.slayseed.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SeedService {

    private static final String VIEWS_CACHE_PREFIX = "seed:viewed:";

    @Autowired
    private SeedRepository seedRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    public Page<SeedDTO> getSeeds(String tower, String towerCharacter, String playerCount,
                                   String seedType, String sortBy, int page, int size,
                                   Long currentUserId, Set<Long> likedIds, Set<Long> favoritedIds) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Seed> seedPage;

        if (tower != null || towerCharacter != null || playerCount != null || seedType != null) {
            if ("likes".equals(sortBy)) {
                seedPage = seedRepository.findByFiltersOrderByLikes(tower, towerCharacter, playerCount, seedType, pageable);
            } else {
                seedPage = seedRepository.findByFilters(tower, towerCharacter, playerCount, seedType, pageable);
            }
        } else if ("likes".equals(sortBy)) {
            seedPage = seedRepository.findAllByOrderByLikesDesc(pageable);
        } else {
            seedPage = seedRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        return seedPage.map(s -> toDTO(s, currentUserId, likedIds, favoritedIds));
    }

    public Page<SeedDTO> searchSeeds(String keyword, int page, int size, Long currentUserId,
                                      Set<Long> likedIds, Set<Long> favoritedIds) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Seed> seedPage = seedRepository.searchByKeyword(keyword.trim(), pageable);
        return seedPage.map(s -> toDTO(s, currentUserId, likedIds, favoritedIds));
    }

    public Page<SeedDTO> getSeedsByAuthor(Long authorId, int page, int size, Long currentUserId,
                                           Set<Long> likedIds, Set<Long> favoritedIds) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Seed> seedPage = seedRepository.findByAuthorIdOrderByCreatedAtDesc(authorId, pageable);
        return seedPage.map(s -> toDTO(s, currentUserId, likedIds, favoritedIds));
    }

    public Page<SeedDTO> getFavoriteSeeds(Long userId, int page, int size, Long currentUserId,
                                           Set<Long> likedIds, Set<Long> favoritedIds) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Seed> seedPage = seedRepository.findFavoritesByUserId(userId, pageable);
        return seedPage.map(s -> toDTO(s, currentUserId, likedIds, favoritedIds));
    }

    public Page<SeedDTO> getFollowingSeeds(Long userId, int page, int size, Long currentUserId,
                                            Set<Long> likedIds, Set<Long> favoritedIds) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Seed> seedPage = seedRepository.findByFollowingUsers(userId, pageable);
        return seedPage.map(s -> toDTO(s, currentUserId, likedIds, favoritedIds));
    }

    @Transactional
    public SeedDTO getSeedById(Long id, Long currentUserId, String clientIp) {
        Seed seed = seedRepository.findById(id).orElse(null);
        if (seed == null) return null;

        boolean shouldIncrement = true;
        if (redisTemplate != null) {
            String cacheKey = VIEWS_CACHE_PREFIX + id + ":" + (currentUserId != null ? currentUserId : clientIp);
            if (Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
                shouldIncrement = false;
            } else {
                redisTemplate.opsForValue().set(cacheKey, "1", 5, TimeUnit.MINUTES);
            }
        }

        if (shouldIncrement) {
            seedRepository.incrementViews(id);
            seed.setViews(seed.getViews() + 1);
        }

        SeedDTO dto = toDTO(seed, currentUserId, null, null);
        return dto;
    }

    @Transactional
    public SeedDTO createSeed(SeedRequest request, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Seed seed = new Seed();
        seed.setSeedCode(request.getSeedCode());
        seed.setTower(request.getTower());
        seed.setTowerCharacter(request.getTowerCharacter());
        seed.setPlayerCount(request.getPlayerCount());
        seed.setSeedType(request.getSeedType());
        seed.setDescription(request.getDescription());
        seed.setTags(request.getTags());
        seed.setAuthor(author);
        seed.setLikes(0);
        seed.setViews(0);
        seed.setCommentCount(0);
        seed.setFavoriteCount(0);

        seed = seedRepository.save(seed);

        author.setSeedCount(author.getSeedCount() + 1);
        userRepository.save(author);

        return toDTO(seed, authorId, null, null);
    }

    @Transactional
    public void deleteSeed(Long id, Long userId) {
        Seed seed = seedRepository.findById(id).orElseThrow(() -> new RuntimeException("种子不存在"));
        if (!seed.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("无权删除");
        }
        seedRepository.delete(seed);

        User author = seed.getAuthor();
        if (author.getSeedCount() > 0) {
            author.setSeedCount(author.getSeedCount() - 1);
            userRepository.save(author);
        }
    }

    private SeedDTO toDTO(Seed seed, Long currentUserId, Set<Long> likedIds, Set<Long> favoritedIds) {
        if (seed == null) return null;
        SeedDTO dto = new SeedDTO();
        dto.setId(seed.getId());
        dto.setSeedCode(seed.getSeedCode());
        dto.setTower(seed.getTower());
        dto.setTowerCharacter(seed.getTowerCharacter());
        dto.setPlayerCount(seed.getPlayerCount());
        dto.setSeedType(seed.getSeedType());
        dto.setDescription(seed.getDescription());
        dto.setTags(seed.getTags());
        dto.setLikes(seed.getLikes());
        dto.setViews(seed.getViews());
        dto.setCommentCount(seed.getCommentCount());
        dto.setFavoriteCount(seed.getFavoriteCount());
        dto.setCreatedAt(seed.getCreatedAt());

        if (seed.getAuthor() != null) {
            dto.setAuthorId(seed.getAuthor().getId());
            dto.setAuthorUsername(seed.getAuthor().getUsername());
            dto.setAuthorNickname(seed.getAuthor().getNickname());
            dto.setAuthorAvatar(seed.getAuthor().getAvatar());
        }

        if (currentUserId != null) {
            dto.setLikedByCurrentUser(likedIds != null && likedIds.contains(seed.getId()));
            dto.setFavoritedByCurrentUser(favoritedIds != null && favoritedIds.contains(seed.getId()));
        }

        return dto;
    }
}