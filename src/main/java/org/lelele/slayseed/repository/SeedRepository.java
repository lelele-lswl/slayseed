package org.lelele.slayseed.repository;

import org.lelele.slayseed.entity.Seed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeedRepository extends JpaRepository<Seed, Long> {

    @Query("SELECT s FROM Seed s LEFT JOIN FETCH s.author ORDER BY s.createdAt DESC")
    Page<Seed> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT s FROM Seed s LEFT JOIN FETCH s.author ORDER BY s.likes DESC, s.createdAt DESC")
    Page<Seed> findAllByOrderByLikesDesc(Pageable pageable);

    @Query("SELECT s FROM Seed s LEFT JOIN FETCH s.author WHERE s.tower = :tower ORDER BY s.createdAt DESC")
    Page<Seed> findByTowerOrderByCreatedAtDesc(@Param("tower") String tower, Pageable pageable);

    @Query("SELECT s FROM Seed s LEFT JOIN FETCH s.author WHERE s.towerCharacter = :towerCharacter ORDER BY s.createdAt DESC")
    Page<Seed> findByTowerCharacterOrderByCreatedAtDesc(@Param("towerCharacter") String towerCharacter, Pageable pageable);

    @Query("SELECT s FROM Seed s LEFT JOIN FETCH s.author WHERE s.tower = :tower AND s.towerCharacter = :towerCharacter ORDER BY s.createdAt DESC")
    Page<Seed> findByTowerAndTowerCharacterOrderByCreatedAtDesc(@Param("tower") String tower, @Param("towerCharacter") String towerCharacter, Pageable pageable);

    @Query("SELECT s FROM Seed s LEFT JOIN FETCH s.author WHERE s.author.id = :authorId ORDER BY s.createdAt DESC")
    Page<Seed> findByAuthorIdOrderByCreatedAtDesc(@Param("authorId") Long authorId, Pageable pageable);

    @Query("SELECT s FROM Seed s LEFT JOIN FETCH s.author WHERE " +
           "LOWER(s.seedCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.tags) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY s.createdAt DESC")
    Page<Seed> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT s FROM Seed s LEFT JOIN FETCH s.author WHERE " +
           "(:tower IS NULL OR s.tower = :tower) AND " +
           "(:towerCharacter IS NULL OR s.towerCharacter = :towerCharacter) AND " +
           "(:playerCount IS NULL OR s.playerCount = :playerCount) AND " +
           "(:seedType IS NULL OR s.seedType = :seedType) " +
           "ORDER BY s.createdAt DESC")
    Page<Seed> findByFilters(
            @Param("tower") String tower,
            @Param("towerCharacter") String towerCharacter,
            @Param("playerCount") String playerCount,
            @Param("seedType") String seedType,
            Pageable pageable);

    @Query("SELECT s FROM Seed s LEFT JOIN FETCH s.author WHERE " +
           "(:tower IS NULL OR s.tower = :tower) AND " +
           "(:towerCharacter IS NULL OR s.towerCharacter = :towerCharacter) AND " +
           "(:playerCount IS NULL OR s.playerCount = :playerCount) AND " +
           "(:seedType IS NULL OR s.seedType = :seedType) " +
           "ORDER BY s.likes DESC, s.createdAt DESC")
    Page<Seed> findByFiltersOrderByLikes(
            @Param("tower") String tower,
            @Param("towerCharacter") String towerCharacter,
            @Param("playerCount") String playerCount,
            @Param("seedType") String seedType,
            Pageable pageable);

    @Query("SELECT s FROM Seed s LEFT JOIN FETCH s.author JOIN Favorite f ON s.id = f.seed.id WHERE f.user.id = :userId ORDER BY f.createdAt DESC")
    Page<Seed> findFavoritesByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT s FROM Seed s LEFT JOIN FETCH s.author JOIN Follow fo ON s.author.id = fo.following.id WHERE fo.follower.id = :userId ORDER BY s.createdAt DESC")
    Page<Seed> findByFollowingUsers(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(s) FROM Seed s WHERE s.author.id = :authorId")
    long countByAuthorId(@Param("authorId") Long authorId);

    @Query("SELECT s FROM Seed s LEFT JOIN FETCH s.author WHERE s.id = :id")
    java.util.Optional<Seed> findByIdWithAuthor(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Seed s SET s.likes = s.likes + 1 WHERE s.id = :id")
    void incrementLikes(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Seed s SET s.likes = s.likes - 1 WHERE s.id = :id AND s.likes > 0")
    void decrementLikes(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Seed s SET s.views = s.views + 1 WHERE s.id = :id")
    void incrementViews(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Seed s SET s.commentCount = s.commentCount + 1 WHERE s.id = :id")
    void incrementCommentCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Seed s SET s.favoriteCount = s.favoriteCount + 1 WHERE s.id = :id")
    void incrementFavoriteCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Seed s SET s.favoriteCount = s.favoriteCount - 1 WHERE s.id = :id AND s.favoriteCount > 0")
    void decrementFavoriteCount(@Param("id") Long id);

    boolean existsBySeedCodeAndTowerAndTowerCharacter(String seedCode, String tower, String towerCharacter);
}