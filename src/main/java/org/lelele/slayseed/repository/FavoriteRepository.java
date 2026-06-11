package org.lelele.slayseed.repository;

import org.lelele.slayseed.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserIdAndSeedId(Long userId, Long seedId);
    boolean existsByUserIdAndSeedId(Long userId, Long seedId);
    void deleteByUserIdAndSeedId(Long userId, Long seedId);
    Page<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT f.seed.id FROM Favorite f WHERE f.user.id = :userId")
    List<Long> findFavoritedSeedIdsByUserId(@Param("userId") Long userId);
}