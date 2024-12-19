package com.library.repository;

import com.library.model.Book;
import com.library.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository  extends JpaRepository<Comment, Integer> {
    // Find all comments related to a specific book
    List<Comment> findByBook(Book book);

    // Find all comments that are replies to a specific parent comment
    List<Comment> findByParentComment(Comment parentComment);
}
