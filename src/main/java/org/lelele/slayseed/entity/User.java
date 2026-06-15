package org.lelele.slayseed.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 100, message = "昵称长度不能超过100个字符")
    @Column(name = "nickname", length = 100)
    private String nickname;

    @Size(max = 255, message = "头像URL长度不能超过255")
    @Column(name = "avatar", length = 255)
    private String avatar;

    @Size(max = 500, message = "简介长度不能超过500")
    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "seed_count", nullable = false)
    private Integer seedCount = 0;

    @Column(name = "follower_count", nullable = false)
    private Integer followerCount = 0;

    @Column(name = "following_count", nullable = false)
    private Integer followingCount = 0;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}