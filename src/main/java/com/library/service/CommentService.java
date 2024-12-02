package com.library.service;

import com.library.model.Comment;
import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByBook(int bookId);
    List<Comment> getReplies(int parentCommentId);
    Comment addComment(int bookId, int userId, String content, Integer parentCommentId);
}
