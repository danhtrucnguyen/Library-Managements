package com.library.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String content;

    @ManyToOne(optional = true)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    private LocalDateTime commentDate= LocalDateTime.now();
}
