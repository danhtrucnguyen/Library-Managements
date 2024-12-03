package com.library.repository;

import com.library.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository  extends JpaRepository<Comment, Integer> {
    List<Comment> findByBookId(int bookId);
    List<Comment> findByParentCommentId(int parentCommentId);
}
