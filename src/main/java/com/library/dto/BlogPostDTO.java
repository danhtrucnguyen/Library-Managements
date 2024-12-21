package com.library.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogPostDTO {
    @JsonProperty("author_id")
    private Integer authorId;

    private String pcontent;

    private String title;

    private String imageUrl;
}
