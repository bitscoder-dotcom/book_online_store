package com.bitscoder.onlinebookstore.repository;

import com.bitscoder.onlinebookstore.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *  This interface enables CRUD operations to be performed on Book entities identified by a String ID
 */
public interface BookRepository extends JpaRepository<Book, String> {
}
