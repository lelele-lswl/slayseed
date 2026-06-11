package org.lelele.slayseed.service;

import org.lelele.slayseed.dto.CommentDTO;
import org.lelele.slayseed.dto.CommentRequest;
import org.lelele.slayseed.entity.Comment;
import org.lelele.slayseed.entity.Seed;
import org.lelele.slayseed.entity.User;
import org.lelele.slayseed.repository.CommentRepository;
import org.lelele.slayseed.repository.SeedRepository;
import org.lelele.slayseed.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private SeedRepository seedRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<CommentDTO> getCommentsBySeed(Long seedId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return commentRepository.findBySeedIdOrderByCreatedAtDesc(seedId, pageable).map(this::toDTO);
    }

    public Page<CommentDTO> getCommentsByAuthor(Long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return commentRepository.findByAuthorIdOrderByCreatedAtDesc(authorId, pageable).map(this::toDTO);
    }

    @Transactional
    public CommentDTO createComment(Long seedId, Long authorId, CommentRequest request) {
        Seed seed = seedRepository.findById(seedId).orElseThrow(() -> new RuntimeException("种子不存在"));
        User author = userRepository.findById(authorId).orElseThrow(() -> new RuntimeException("用户不存在"));

        Comment comment = new Comment();
        comment.setSeed(seed);
        comment.setAuthor(author);
        comment.setContent(request.getContent());
        comment = commentRepository.save(comment);

        seedRepository.incrementCommentCount(seedId);
        seed.setCommentCount(seed.getCommentCount() + 1);

        return toDTO(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("评论不存在"));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("无权删除");
        }
        commentRepository.delete(comment);
    }

    private CommentDTO toDTO(Comment comment) {
        if (comment == null) return null;
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setSeedId(comment.getSeed().getId());
        dto.setAuthorId(comment.getAuthor().getId());
        dto.setAuthorUsername(comment.getAuthor().getUsername());
        dto.setAuthorNickname(comment.getAuthor().getNickname());
        dto.setAuthorAvatar(comment.getAuthor().getAvatar());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}