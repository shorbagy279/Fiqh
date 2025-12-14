package com.fiqhmaster.repository;

import com.fiqhmaster.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Bookmark> findByUserIdAndQuestionId(Long userId, Long questionId);
    Boolean existsByUserIdAndQuestionId(Long userId, Long questionId);
    void deleteByUserIdAndQuestionId(Long userId, Long questionId);
}