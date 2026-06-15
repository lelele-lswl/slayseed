package org.lelele.slayseed.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "seeds", indexes = {
    @Index(name = "idx_tower", columnList = "tower"),
    @Index(name = "idx_character", columnList = "tower_character"),
    @Index(name = "idx_players", columnList = "player_count"),
    @Index(name = "idx_type", columnList = "seed_type"),
    @Index(name = "idx_author", columnList = "author_id"),
    @Index(name = "idx_created", columnList = "created_at"),
    @Index(name = "idx_likes", columnList = "likes"),
    @Index(name = "idx_seedcode", columnList = "seed_code")
})
public class Seed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "seed_code", nullable = false, length = 200)
    private String seedCode;

    @NotBlank
    @Column(name = "tower", nullable = false, length = 50)
    private String tower;

    @NotBlank
    @Column(name = "tower_character", nullable = false, length = 50)
    private String towerCharacter;

    @Column(name = "player_count", length = 20)
    private String playerCount;

    @Column(name = "seed_type", length = 50)
    private String seedType;

    @NotBlank
    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    @Column(name = "tags", length = 500)
    private String tags;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "likes", nullable = false)
    private Integer likes = 0;

    @Column(name = "views", nullable = false)
    private Integer views = 0;

    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;

    @Column(name = "favorite_count", nullable = false)
    private Integer favoriteCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}