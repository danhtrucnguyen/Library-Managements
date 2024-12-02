package com.library.service.impl;

import com.library.model.Book;
import com.library.model.Comment;
import com.library.model.User;
import com.library.repository.CommentRepository;
import com.library.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public List<Comment> getCommentsByBook(int bookId) {
        return commentRepository.findByBookId(bookId);
    }

    @Override
    public List<Comment> getReplies(int parentCommentId) {
        return commentRepository.findByParentCommentId(parentCommentId);
    }

    @Override
    public Comment addComment(int bookId, int userId, String content, Integer parentCommentId) {
        Comment comment = new Comment();

        Book book = new Book();
        book.setId(bookId);
        comment.setBook(book);

        User user = new User();
        user.setId(userId);
        comment.setUser(user);

        comment.setContent(content);

        if (parentCommentId != null) {
            Comment parentComment = new Comment();
            parentComment.setId(parentCommentId);
            comment.setParentComment(parentComment);
        }
        return commentRepository.save(comment);
    }
}
