package org.lelele.slayseed.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SeedDTO {
    private Long id;
    private String seedCode;
    private String tower;
    private String towerCharacter;
    private String playerCount;
    private String seedType;
    private String description;
    private String tags;
    private Long authorId;
    private String authorUsername;
    private String authorNickname;
    private String authorAvatar;
    private Integer likes;
    private Integer views;
    private Integer commentCount;
    private Integer favoriteCount;
    private Boolean likedByCurrentUser;
    private Boolean favoritedByCurrentUser;
    private LocalDateTime createdAt;
}