package org.lelele.slayseed.service;

import org.lelele.slayseed.entity.LikeRecord;
import org.lelele.slayseed.entity.Seed;
import org.lelele.slayseed.entity.User;
import org.lelele.slayseed.repository.LikeRecordRepository;
import org.lelele.slayseed.repository.SeedRepository;
import org.lelele.slayseed.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LikeService {

    @Autowired
    private LikeRecordRepository likeRecordRepository;

    @Autowired
    private SeedRepository seedRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public boolean toggleLike(Long seedId, Long userId) {
        if (likeRecordRepository.existsByUserIdAndSeedId(userId, seedId)) {
            likeRecordRepository.deleteByUserIdAndSeedId(userId, seedId);
            seedRepository.decrementLikes(seedId);
            return false;
        } else {
            Seed seed = seedRepository.findById(seedId).orElseThrow(() -> new RuntimeException("种子不存在"));
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
            LikeRecord record = new LikeRecord();
            record.setSeed(seed);
            record.setUser(user);
            likeRecordRepository.save(record);
            seedRepository.incrementLikes(seedId);
            return true;
        }
    }

    public boolean isLiked(Long seedId, Long userId) {
        return likeRecordRepository.existsByUserIdAndSeedId(userId, seedId);
    }

    public Set<Long> getLikedSeedIds(Long userId) {
        return likeRecordRepository.findLikedSeedIdsByUserId(userId).stream().collect(Collectors.toSet());
    }
}