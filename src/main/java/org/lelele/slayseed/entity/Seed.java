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
    @Index(name = "idx_created", columnList = "createdAt"),
    @Index(name = "idx_likes", columnList = "likes"),
    @Index(name = "idx_seedcode", columnList = "seedCode")
})
public class Seed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String seedCode;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String tower;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String towerCharacter;

    @Column(length = 20)
    private String playerCount;

    @Column(length = 50)
    private String seedType;

    @NotBlank
    @Column(nullable = false, length = 2000)
    private String description;

    @Column(length = 500)
    private String tags;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(nullable = false)
    private Integer likes = 0;

    @Column(nullable = false)
    private Integer views = 0;

    @Column(nullable = false)
    private Integer commentCount = 0;

    @Column(nullable = false)
    private Integer favoriteCount = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}