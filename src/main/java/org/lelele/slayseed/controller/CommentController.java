package org.lelele.slayseed.controller;

import jakarta.validation.Valid;
import org.lelele.slayseed.dto.CommentRequest;
import org.lelele.slayseed.entity.User;
import org.lelele.slayseed.repository.UserRepository;
import org.lelele.slayseed.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/seed/{seedId}")
    public ResponseEntity<?> getCommentsBySeed(
            @PathVariable Long seedId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(commentService.getCommentsBySeed(seedId, page, size));
    }

    @PostMapping("/seed/{seedId}")
    public ResponseEntity<?> createComment(
            @PathVariable Long seedId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "请先登录");
            return ResponseEntity.status(401).body(error);
        }
        try {
            return ResponseEntity.ok(commentService.createComment(seedId, userId, request));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id, Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        if (userId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "请先登录");
            return ResponseEntity.status(401).body(error);
        }
        try {
            commentService.deleteComment(id, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        return userRepository.findByUsername(authentication.getName())
                .map(User::getId)
                .orElse(null);
    }
}