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

    Page<Seed> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Seed> findAllByOrderByLikesDesc(Pageable pageable);

    Page<Seed> findByTowerOrderByCreatedAtDesc(String tower, Pageable pageable);

    Page<Seed> findByTowerCharacterOrderByCreatedAtDesc(String towerCharacter, Pageable pageable);

    Page<Seed> findByTowerAndTowerCharacterOrderByCreatedAtDesc(String tower, String towerCharacter, Pageable pageable);

    Page<Seed> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    @Query("SELECT s FROM Seed s WHERE LOWER(s.seedCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.tags) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY s.createdAt DESC")
    Page<Seed> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT s FROM Seed s WHERE " +
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

    @Query("SELECT s FROM Seed s WHERE " +
           "(:tower IS NULL OR s.tower = :tower) AND " +
           "(:towerCharacter IS NULL OR s.towerCharacter = :towerCharacter) AND " +
           "(:playerCount IS NULL OR s.playerCount = :playerCount) AND " +
           "(:seedType IS NULL OR s.seedType = :seedType) " +
           "ORDER BY s.likes DESC")
    Page<Seed> findByFiltersOrderByLikes(
            @Param("tower") String tower,
            @Param("towerCharacter") String towerCharacter,
            @Param("playerCount") String playerCount,
            @Param("seedType") String seedType,
            Pageable pageable);

    @Query("SELECT s FROM Seed s JOIN Favorite f ON s.id = f.seed.id WHERE f.user.id = :userId ORDER BY f.createdAt DESC")
    Page<Seed> findFavoritesByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT s FROM Seed s JOIN Follow fo ON s.author.id = fo.following.id WHERE fo.follower.id = :userId ORDER BY s.createdAt DESC")
    Page<Seed> findByFollowingUsers(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(s) FROM Seed s WHERE s.author.id = :authorId")
    long countByAuthorId(@Param("authorId") Long authorId);

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