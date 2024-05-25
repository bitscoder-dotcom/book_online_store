package com.bitscoder.onlinebookstore.dto;

import com.bitscoder.onlinebookstore.constant.Genre;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * This is the book dto request and response class
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookDto {

    private String title;
    private String author;
    private String isbn;
    private Genre genre;
    private long quantity;
    private LocalDate publicationYear;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        private String id;
        private String title;
        private String author;
        private String isbn;
        private Genre genre;
        private LocalDate publicationYear;
        private long quantity;
        private LocalDateTime borrowedAt;
    }
}

