package org.lelele.slayseed.repository;

import org.lelele.slayseed.entity.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
    @Query("SELECT f FROM Follow f LEFT JOIN FETCH f.follower WHERE f.following.id = :followingId ORDER BY f.createdAt DESC")
    Page<Follow> findByFollowingIdOrderByCreatedAtDesc(@Param("followingId") Long followingId, Pageable pageable);

    @Query("SELECT f FROM Follow f LEFT JOIN FETCH f.following WHERE f.follower.id = :followerId ORDER BY f.createdAt DESC")
    Page<Follow> findByFollowerIdOrderByCreatedAtDesc(@Param("followerId") Long followerId, Pageable pageable);
    long countByFollowerId(Long followerId);
    long countByFollowingId(Long followingId);

    @Query("SELECT f.following.id FROM Follow f WHERE f.follower.id = :followerId")
    List<Long> findFollowingIdsByFollowerId(@Param("followerId") Long followerId);
}