package com.bitscoder.onlinebookstore;

import com.bitscoder.onlinebookstore.constant.Genre;
import com.bitscoder.onlinebookstore.constant.Roles;
import com.bitscoder.onlinebookstore.dto.ApiResponse;
import com.bitscoder.onlinebookstore.dto.BookDto;
import com.bitscoder.onlinebookstore.exception.ResourceNotFoundException;
import com.bitscoder.onlinebookstore.exception.UnauthorizedException;
import com.bitscoder.onlinebookstore.models.Book;
import com.bitscoder.onlinebookstore.models.User;
import com.bitscoder.onlinebookstore.repository.BookRepository;
import com.bitscoder.onlinebookstore.repository.UserRepository;
import com.bitscoder.onlinebookstore.service.bookServiceImpl.BookServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookServiceImplTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Test to add book to store by User")
    public void testAddNewBookToStore() {
        // Create a new BookDto object
        BookDto bookDto = new BookDto("Test Book", "Test Author", "123-456-789", Genre.FICTION, 10, LocalDate.of(2022, 1, 1));

        // Mock the Principal
        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("test@test.com");

        // Create a new User object and mock the UserRepository
        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test User");
        user.setRoles(Roles.USER);
        Mockito.when(userRepository.findByEmail(principal.getName())).thenReturn(Optional.of(user));

        // Create a new Book object and mock the BookRepository
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setIsbn(bookDto.getIsbn());
        book.setGenre(bookDto.getGenre());
        book.setQuantity(bookDto.getQuantity());
        book.setPublicationYear(bookDto.getPublicationYear());
        book.setUser(user);
        Mockito.when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Call the method under test and check the response
        ResponseEntity<ApiResponse<BookDto.Response>> response = bookService.addNewBookToStore(bookDto, principal);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Book inserted successfully to "+ bookDto.getGenre()+ " shelve by "+user.getName(), response.getBody().getMessage());
    }

    @Test
    @DisplayName("Test to add book to store by non user")
    public void testAddNewBookToStore_NotAUser() {
        // Creating a new BookDto object
        BookDto bookDto = new BookDto("Test Book", "Test Author", "123-456-789", Genre.FICTION, 10, LocalDate.of(2022, 1, 1));

        // Mocking the Principal
        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("test@test.com");

        // Creating a new User object with a different role and mock the UserRepository
        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test User");
        user.setRoles(Roles.NOT_ADMIN); // Setting a different role here
        Mockito.when(userRepository.findByEmail(principal.getName())).thenReturn(Optional.of(user));

        // Expect an UnauthorizedException to be thrown
        assertThrows(UnauthorizedException.class, () -> {
            bookService.addNewBookToStore(bookDto, principal);
        });
    }

    @Test
    @DisplayName("Test to get all books")
    public void testGetAllBooks() {
        // Mocking the Principal
        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("test@test.com");

        // Creating a new User object and mock the UserRepository
        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test User");
        user.setRoles(Roles.USER);
        Mockito.when(userRepository.findByEmail(principal.getName())).thenReturn(Optional.of(user));

        // Creating a list of Book objects and mock the BookRepository
        List<Book> books = new ArrayList<>();
        Book book1 = new Book("Book1AD3", "Test Book 1", "Test Author 1", "123-456-789", Genre.FICTION, LocalDate.of(2022, 1, 1), 7, user);
        Book book2 = new Book("Book1123","Test Book 2", "Test Author 2", "987-654-321", Genre.DRAMA, LocalDate.of(2021, 1, 1), 8, user);
        books.add(book1);
        books.add(book2);
        Mockito.when(bookRepository.findAll()).thenReturn(books);

        // Calling the method under test and check the response
        ResponseEntity<ApiResponse<List<BookDto.Response>>> response = bookService.getAllBooks(principal);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Fetched all books for user: Test User", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().size());
    }

    @Test
    @DisplayName("Test to get book by id")
    public void testGetBookById() {
        // Mocking the Principal
        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("test@test.com");

        // Creating a new User object and mock the UserRepository
        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test User");
        user.setRoles(Roles.USER);
        Mockito.when(userRepository.findByEmail(principal.getName())).thenReturn(Optional.of(user));

        Book book = new Book("BookLL45MN", "Test Book", "Test Author", "123-456-789", Genre.FICTION, LocalDate.of(2022, 1, 1), 10, user);
        Mockito.when(bookRepository.findById(anyString())).thenReturn(Optional.of(book));

        ResponseEntity<ApiResponse<BookDto.Response>> response = bookService.getBookById(book.getId(), principal);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Fetched book with id: " + book.getId() + " for user: Test User", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Test when a book id is not found")
    public void testGetBookById_NotFound() {
        // Mock the Principal
        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("test@test.com");

        // Create a new User object and mock the UserRepository
        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test User");
        user.setRoles(Roles.USER);
        Mockito.when(userRepository.findByEmail(principal.getName())).thenReturn(Optional.of(user));

        // Mock the BookRepository to return an empty Optional when findById is called
        Mockito.when(bookRepository.findById(anyString())).thenReturn(Optional.empty());

        // Expect a ResourceNotFoundException to be thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.getBookById("unknown-id", principal);
        });
    }

    @Test
    @DisplayName("Test to remove book by calling the book id")
    public void testRemoveBook() {
        // mock the Principal
        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("test@test.com");

        // create a new User object and mock the UserRepository
        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test User");
        user.setRoles(Roles.USER);
        Mockito.when(userRepository.findByEmail(principal.getName())).thenReturn(Optional.of(user));

        // create a new Book object and mock the BookRepository
        Book book = new Book("BookJ7EY", "Test Book", "Test Author", "123-456-789", Genre.FICTION, LocalDate.of(2022, 1, 1), 10, user);
        Mockito.when(bookRepository.findById(anyString())).thenReturn(Optional.of(book));

        // call the method under test
        bookService.removeBook(book.getId(), principal);

        // to verify that the delete method was called on the BookRepository
        Mockito.verify(bookRepository, Mockito.times(1)).delete(book);
    }
}
