package org.lelele.slayseed.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private Long id;
    private String content;
    private Long seedId;
    private Long authorId;
    private String authorUsername;
    private String authorNickname;
    private String authorAvatar;
    private LocalDateTime createdAt;
}