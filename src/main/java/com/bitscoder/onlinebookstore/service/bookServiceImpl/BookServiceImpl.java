package com.bitscoder.onlinebookstore.service.bookServiceImpl;

import com.bitscoder.onlinebookstore.constant.Roles;
import com.bitscoder.onlinebookstore.dto.ApiResponse;
import com.bitscoder.onlinebookstore.dto.BookDto;
import com.bitscoder.onlinebookstore.exception.ResourceNotFoundException;
import com.bitscoder.onlinebookstore.exception.UnauthorizedException;
import com.bitscoder.onlinebookstore.models.Book;
import com.bitscoder.onlinebookstore.models.User;
import com.bitscoder.onlinebookstore.repository.BookRepository;
import com.bitscoder.onlinebookstore.repository.UserRepository;
import com.bitscoder.onlinebookstore.service.BookService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.beans.FeatureDescriptor;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The BookServiceImpl class is an implementation of the BookService interface.
 * It provides methods to manage books in a bookstore, including adding a new book, retrieving all books, retrieving a book by its ID, updating a book's details, and removing a book.
 * It uses a BookRepository to interact with the database and a UserRepository to fetch user details.
 * The methods throw exceptions for invalid operations (like a non-user trying to insert a book) and for resources not found (like a user or a book not found).
 * The methods convert DTOs to entities before saving them in the database and convert entities to DTOs before returning them in the response.
 */
@Slf4j
@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<ApiResponse<BookDto.Response>> addNewBookToStore(BookDto bookDto, Principal principal) {
        log.info("Inserting book with title: {}", bookDto.getTitle());
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        if (user.getRoles() != Roles.USER) {
            throw new UnauthorizedException("Only a User can insert a book");
        }
        Book book = convertDtoToEntity(bookDto, user);
        book.setPublicationYear(bookDto.getPublicationYear());
        bookRepository.save(book);
        BookDto.Response bookResponse = convertEntityToDto(book);
        ApiResponse<BookDto.Response> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Book inserted successfully to "+ bookDto.getGenre()+ " shelve by "+ user.getName(),
                bookResponse
        );
        log.info("Book inserted successfully with title: {}", bookDto.getTitle());
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<List<BookDto.Response>>> getAllBooks(Principal principal) {
        log.info("Fetching all books for user: {}", principal.getName());
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        List<Book> books = bookRepository.findAll();
        List<BookDto.Response> bookResponses = books.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
        ApiResponse<List<BookDto.Response>> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Fetched all books for user: " + user.getName(),
                bookResponses
        );
        log.info("Fetched all books for user: {}", principal.getName());
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<BookDto.Response>> getBookById(String id, Principal principal) {
        log.info("Fetching book with id: {}", id);
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        BookDto.Response bookResponse = convertEntityToDto(book);
        ApiResponse<BookDto.Response> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Fetched book with id: " + id + " for user: " + user.getName(),
                bookResponse
        );
        log.info("Fetched book with id: {}", id);
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<BookDto.Response>> updateBook(String id, BookDto bookRequest, Principal principal) {
        log.info("Updating book with id: {}", id);
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        if (user.getRoles() != Roles.USER) {
            throw new UnauthorizedException("Only a User can update book details");
        }
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        book.setTitle(bookRequest.getTitle());
        book.setAuthor(bookRequest.getAuthor());
        book.setIsbn(bookRequest.getIsbn());
        book.setGenre(bookRequest.getGenre());
        book.setQuantity(bookRequest.getQuantity());
        book.setPublicationYear(bookRequest.getPublicationYear());
        book.setUser(user);
        bookRepository.save(book);
        BookDto.Response bookResponse = convertEntityToDto(book);
        ApiResponse<BookDto.Response> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Updated book with id: " + id + " for user: " + user.getName(),
                bookResponse
        );
        log.info("Updated book with id: {}", id);
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public void removeBook(String id, Principal principal) {
        log.info("Removing book with id: {}", id);
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getName()));
        if (user.getRoles() != Roles.USER) {
            throw new UnauthorizedException("Only a User can remove a book");
        }
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        bookRepository.delete(book);
        ApiResponse<String> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Removed book with id: " + id + " by user: " + user.getName(),
                "Book removed successfully"
        );
        log.info("Removed book with id: {}", id);
        ResponseEntity.ok(apiResponse);
    }


    // HELPER METHODS
    private BookDto.Response convertEntityToDto(Book book) {
        BookDto.Response bookResponse = new BookDto.Response();
        bookResponse.setId(book.getId());
        bookResponse.setTitle(book.getTitle());
        bookResponse.setAuthor(book.getAuthor());
        bookResponse.setIsbn(book.getIsbn());
        bookResponse.setGenre(book.getGenre());
        bookResponse.setPublicationYear(book.getPublicationYear());
        bookResponse.setQuantity(book.getQuantity());
        return bookResponse;
    }


    private Book convertDtoToEntity(BookDto bookDto, User user) {
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setIsbn(bookDto.getIsbn());
        book.setGenre(bookDto.getGenre());
        book.setQuantity(bookDto.getQuantity());
        book.setPublicationYear(bookDto.getPublicationYear());
        book.setUser(user);
        return book;
    }
}
