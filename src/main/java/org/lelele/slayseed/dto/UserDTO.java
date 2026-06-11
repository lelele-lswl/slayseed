package org.lelele.slayseed.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String bio;
    private Integer seedCount;
    private Integer followerCount;
    private Integer followingCount;
    private LocalDateTime createdAt;
}