package com.library.service.impl;

import com.library.model.Book;
import com.library.model.Comment;
import com.library.model.User;
import com.library.repository.BookRepository;
import com.library.repository.CommentRepository;
import com.library.repository.UserRepository;
import com.library.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<Comment> getCommentsByBook(int bookId) {
        // Find the book by its ID
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Không tìm thấy sách"));
        return commentRepository.findByBook(book); // Use the Book entity here
    }

    @Override
    public List<Comment> getReplies(int parentCommentId) {
        // Find the parent comment by its ID
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận"));
        return commentRepository.findByParentComment(parentComment); // Use the Comment entity here
    }

    @Override
    public Comment addComment(int bookId, int userId, String content, Integer parentCommentId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Không tìm thấy sách"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Comment comment = new Comment();
        book.setId(bookId);
        comment.setBook(book);
        user.setId(userId);
        comment.setUser(user);
        comment.setContent(content);
        comment.setCommentDate(LocalDateTime.now());

        if (parentCommentId != null) {
            Comment parentComment = commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận"));
            comment.setParentComment(parentComment); // Set parent comment for replies
        }

        return commentRepository.save(comment);
    }

//    public List<Comment> prepareComments(int bookId) {
//        List<Comment> comments = getCommentsByBook(bookId);
//
//        // Định dạng ngày tháng hoặc thêm thông tin phụ
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
//        comments.forEach(comment -> {
//            comment .setFormattedDate(comment.getCommentDate().format(formatter));
//            if (comment.getReplies() != null) {
//                comment.getReplies().forEach(reply ->
//                        reply.setFormattedDate(reply.getCommentDate().format(formatter))
//                );
//            }
//        });
//
//        return comments;
//    }
}
