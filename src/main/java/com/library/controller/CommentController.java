package com.library.controller;

import org.springframework.ui.Model;
import com.library.model.Comment;
import com.library.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;

//    @GetMapping
//    public String getComments(@PathVariable int bookId, Model model) {
//        List<Comment> comments = commentService.getCommentsByBook(bookId);

//    comments.forEach(comment -> {
//        comment.setFormattedDate(comment.getCommentDate().toString()); // Hoặc sử dụng DateTimeFormatter
//        if (comment.getReplies() != null) {
//            comment.getReplies().forEach(reply ->
//                    reply.setFormattedDate(reply.getCommentDate().toString())
//            );
//        }
//    });
//        model.addAttribute("comments", comments);
//        model.addAttribute("bookId", bookId);
//        return "view_book";
//    }

//    @PostMapping("/book/{bookId}")
//    public String addComment(@PathVariable int bookId,
//                             @RequestParam int userId,
//                             @RequestParam String content,
//                             @RequestParam(required = false) Integer parentCommentId) {
//
//        // Kiểm tra nội dung bình luận không rỗng
//        if (content == null || content.trim().isEmpty()) {
//            throw new IllegalArgumentException("Nội dung bình luận không được để trống.");
//        }
//
//        commentService.addComment(bookId, userId, content, parentCommentId);
//        return "redirect:/book/" + bookId;
//    }
}

