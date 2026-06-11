package org.lelele.slayseed.service;

import org.lelele.slayseed.dto.LoginRequest;
import org.lelele.slayseed.dto.LoginResponse;
import org.lelele.slayseed.dto.RegisterRequest;
import org.lelele.slayseed.dto.UserDTO;
import org.lelele.slayseed.entity.User;
import org.lelele.slayseed.repository.UserRepository;
import org.lelele.slayseed.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setEnabled(true);
        user.setSeedCount(0);
        user.setFollowerCount(0);
        user.setFollowingCount(0);

        user = userRepository.save(user);

        String token = jwtTokenUtil.generateToken(user.getUsername());
        return new LoginResponse(token, toDTO(user));
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        String token = jwtTokenUtil.generateToken(user.getUsername());
        return new LoginResponse(token, toDTO(user));
    }

    public UserDTO toDTO(User user) {
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