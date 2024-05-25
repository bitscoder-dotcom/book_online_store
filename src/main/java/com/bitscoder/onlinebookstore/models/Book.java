package com.bitscoder.onlinebookstore.models;

import com.bitscoder.onlinebookstore.constant.Genre;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

/**
 * The entity class for the book object
 */
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "books")
public class Book {

    @Id
    private String id;
    private String title;
    private String author;
    private String isbn;
    @Enumerated(EnumType.STRING)
    private Genre genre;
    private LocalDate publicationYear;
    private long quantity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    public Book() {
        this.setId(generateCustomUUID());
    }


    private String generateCustomUUID() {
        return "Book"+ UUID.randomUUID().toString().substring(0, 5);
    }
}

