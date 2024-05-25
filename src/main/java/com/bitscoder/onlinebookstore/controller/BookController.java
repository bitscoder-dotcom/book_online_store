package com.bitscoder.onlinebookstore.controller;

import com.bitscoder.onlinebookstore.dto.ApiResponse;
import com.bitscoder.onlinebookstore.dto.BookDto;
import com.bitscoder.onlinebookstore.service.BookService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/app")
@AllArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    @PostMapping("/add")
    public String addNewBookToStore(@ModelAttribute BookDto bookDto, RedirectAttributes redirectAttributes, Principal principal) {
        ResponseEntity<ApiResponse<BookDto.Response>> response = bookService.addNewBookToStore(bookDto, principal);
        if (response.getStatusCode() == HttpStatus.OK) {
            // Modify the success message
            String successMessage = "Book with Title " + bookDto.getTitle() + "inserted successfully to " + bookDto.getGenre()
                    + " shelve by User " + principal.getName();

            redirectAttributes.addFlashAttribute("book", response.getBody().getData());
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
            return "redirect:/app/bookDetails";
        } else {
            // handle error
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred: " + response.getBody().getMessage());
            return "redirect:/app/addBook";
        }
    }

    @GetMapping("/bookDetails")
    public String showBookDetails(@ModelAttribute("book") BookDto.Response book, Model model) {
        log.info("Received request to show book details page");
        model.addAttribute("book", book);
        return "bookDetail";
    }

    @GetMapping("/addBook")
    public String showAddBookPage(Model model) {
        log.info("Received request to show add book page");
        model.addAttribute("bookDto", new BookDto());
        return "addBook";
    }

    @GetMapping("/userPage")
    public String showUserPage() {
        log.info("Received request to show user page");
        return "userPage";
    }

    @GetMapping("/books")
    public String showBooksPage(Model model, Principal principal) {
        log.info("Received request to show lists of book page");
        ResponseEntity<ApiResponse<List<BookDto.Response>>> response = bookService.getAllBooks(principal);
        if (response.getStatusCode() == HttpStatus.OK) {
            model.addAttribute("books", response.getBody().getData());
            return "books";
        } else {
            // handle error
            model.addAttribute("errorMessage", "An error occurred: " + response.getBody().getMessage());
            return "redirect:/app/userPage";
        }
    }

    @GetMapping("/book/{id}")
    public String showBookPage(@PathVariable String id, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        log.info("Received request to show book by id page");
        try {
            ResponseEntity<ApiResponse<BookDto.Response>> response = bookService.getBookById(id, principal);
            model.addAttribute("book", response.getBody().getData());
            return "book";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred: " + e.getMessage());
            return "redirect:/app/userPage";
        }
    }

    @GetMapping("/updateBook/{id}")
    public String showUpdateBookPage(@PathVariable String id, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        log.info("Received request to show update book page");
        try {
            ResponseEntity<ApiResponse<BookDto.Response>> response = bookService.getBookById(id, principal);
            model.addAttribute("bookDto", new BookDto());
            model.addAttribute("book", response.getBody().getData());
            return "updateBook";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred: " + e.getMessage());
            return "redirect:/app/userPage";
        }
    }

    @PostMapping("/updateBook/{id}")
    public String updateBook(@PathVariable String id, @ModelAttribute BookDto bookRequest, RedirectAttributes redirectAttributes, Principal principal) {
        log.info("Updating book with id: {}", id);
        ResponseEntity<ApiResponse<BookDto.Response>> response = bookService.updateBook(id, bookRequest, principal);
        if (response.getStatusCode() == HttpStatus.OK) {
            redirectAttributes.addFlashAttribute("book", response.getBody().getData());
            redirectAttributes.addFlashAttribute("successMessage", "Book details updated successfully");
            return "redirect:/app/bookDetails";
        } else {
            // handle error
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred: " + response.getBody().getMessage());
            return "redirect:/app/updateBook/" + id;
        }
    }

    @PostMapping("/removeBook/{id}")
    public String removeBook(@PathVariable String id, RedirectAttributes redirectAttributes, Principal principal) {
        log.info("Removing book with id: {}", id);
        ResponseEntity<ApiResponse<BookDto.Response>> response = bookService.getBookById(id, principal);
        if (response.getStatusCode() == HttpStatus.OK) {
            String bookTitle = response.getBody().getData().getTitle();
            bookService.removeBook(id, principal);
            redirectAttributes.addFlashAttribute("successMessage", "Book with title '" + bookTitle + "' has been successfully removed from the library");
            return "redirect:/app/books";
        } else {
            // handle error
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred: " + response.getBody().getMessage());
            return "redirect:/app/removeBook/" + id;
        }
    }
}