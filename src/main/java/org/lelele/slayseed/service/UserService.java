package org.lelele.slayseed.service;

import org.lelele.slayseed.dto.UserDTO;
import org.lelele.slayseed.entity.User;
import org.lelele.slayseed.repository.FollowRepository;
import org.lelele.slayseed.repository.SeedRepository;
import org.lelele.slayseed.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SeedRepository seedRepository;

    @Autowired
    private FollowRepository followRepository;

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("用户不存在"));
        return toDTO(user);
    }

    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
        return toDTO(user);
    }

    public UserDTO getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
        return toDTO(user);
    }

    @Transactional
    public UserDTO updateProfile(Long userId, String nickname, String bio, String avatar) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        if (nickname != null) user.setNickname(nickname);
        if (bio != null) user.setBio(bio);
        if (avatar != null) user.setAvatar(avatar);
        user = userRepository.save(user);
        return toDTO(user);
    }

    public Set<Long> getFollowingIds(Long userId) {
        return followRepository.findFollowingIdsByFollowerId(userId).stream().collect(Collectors.toSet());
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
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