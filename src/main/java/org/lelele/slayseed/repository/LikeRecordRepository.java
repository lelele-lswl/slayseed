package org.lelele.slayseed.repository;

import org.lelele.slayseed.entity.LikeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRecordRepository extends JpaRepository<LikeRecord, Long> {

    Optional<LikeRecord> findByUserIdAndSeedId(Long userId, Long seedId);

    boolean existsByUserIdAndSeedId(Long userId, Long seedId);

    void deleteByUserIdAndSeedId(Long userId, Long seedId);

    @Query("SELECT lr.seed.id FROM LikeRecord lr WHERE lr.user.id = :userId")
    List<Long> findLikedSeedIdsByUserId(@Param("userId") Long userId);
}