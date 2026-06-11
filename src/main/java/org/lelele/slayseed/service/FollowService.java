package org.lelele.slayseed.service;

import org.lelele.slayseed.dto.UserDTO;
import org.lelele.slayseed.entity.Follow;
import org.lelele.slayseed.entity.User;
import org.lelele.slayseed.repository.FollowRepository;
import org.lelele.slayseed.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public boolean toggleFollow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new RuntimeException("不能关注自己");
        }

        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);

            User follower = userRepository.findById(followerId).orElseThrow();
            User following = userRepository.findById(followingId).orElseThrow();
            if (follower.getFollowingCount() > 0) follower.setFollowingCount(follower.getFollowingCount() - 1);
            if (following.getFollowerCount() > 0) following.setFollowerCount(following.getFollowerCount() - 1);
            userRepository.save(follower);
            userRepository.save(following);
            return false;
        } else {
            User follower = userRepository.findById(followerId).orElseThrow(() -> new RuntimeException("用户不存在"));
            User following = userRepository.findById(followingId).orElseThrow(() -> new RuntimeException("目标用户不存在"));
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowing(following);
            followRepository.save(follow);

            follower.setFollowingCount(follower.getFollowingCount() + 1);
            following.setFollowerCount(following.getFollowerCount() + 1);
            userRepository.save(follower);
            userRepository.save(following);
            return true;
        }
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    public Page<UserDTO> getFollowers(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return followRepository.findByFollowingIdOrderByCreatedAtDesc(userId, pageable)
                .map(f -> toDTO(f.getFollower()));
    }

    public Page<UserDTO> getFollowing(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return followRepository.findByFollowerIdOrderByCreatedAtDesc(userId, pageable)
                .map(f -> toDTO(f.getFollowing()));
    }

    private UserDTO toDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setAvatar(user.getAvatar());
        dto.setBio(user.getBio());
        dto.setSeedCount(user.getSeedCount());
        dto.setFollowerCount(user.getFollowerCount());
        dto.setFollowingCount(user.getFollowingCount());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}