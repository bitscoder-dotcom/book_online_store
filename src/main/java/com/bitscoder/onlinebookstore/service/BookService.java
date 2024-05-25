package com.bitscoder.onlinebookstore.service;

import com.bitscoder.onlinebookstore.dto.ApiResponse;
import com.bitscoder.onlinebookstore.dto.BookDto;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

/**
 * The BookService interface defines the contract for a service that manages books in a store.
 * It provides methods to add a new book to the store, retrieve all books, retrieve a book by its ID, update a book's details, and remove a book from the store.
 * Each method requires the Principal object of the user performing the operation.
 * The methods return a ResponseEntity with an ApiResponse object that contains the operation's result.
 */

public interface BookService {

    ResponseEntity<ApiResponse<BookDto.Response>> addNewBookToStore(BookDto bookDto, Principal principal);
    ResponseEntity<ApiResponse<List<BookDto.Response>>> getAllBooks(Principal principal);
    ResponseEntity<ApiResponse<BookDto.Response>> getBookById(String id, Principal principal);
    ResponseEntity<ApiResponse<BookDto.Response>> updateBook(String id, BookDto bookRequest, Principal principal);
    void removeBook(String id, Principal principal);
}
