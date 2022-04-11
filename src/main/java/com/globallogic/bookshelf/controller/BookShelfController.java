package com.globallogic.bookshelf.controller;


import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.service.BookShelfService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * Client-server communication class that's processes /bookshelf requests
 *
 * @author Bartlomiej Chojnacki
 * @author Przemyslaw Kondaszewski
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/bookshelf")
@Slf4j
@Api("Management Api")
public class BookShelfController {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookShelfService bookShelfService;

    /**
     * GET Request to find one book based on the name.
     *
     * @param name name of the book
     * @return DTO of the selected book
     */
    @ApiOperation(value = "Returns a DTO of the specified book entity")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Book found", response = BookSO.class),
            @ApiResponse(code = 404, message = "Book not found"),
            @ApiResponse(code = 500, message = "Internal Bookshelf server error")})
    @GetMapping(path = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.OK)
    public BookSO get(@PathVariable(name = "name") String name) {
        BookSO bookSO = bookShelfService.get(name);
        log.info("Returning book={}", bookSO);
        return bookSO;
    }

    /**
     * POST Request to create a book
     *
     * @param bookSO DTO of the book entity
     * @return DTO of the created book
     */
    @ApiOperation(value = "Creates a book entity.")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Book entry created", response = Book.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Bookshelf server error")})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public BookSO create(@RequestBody BookSO bookSO) {
        return bookShelfService.create(bookSO);
    }


    /**
     * GET Request to receive a list of all books available
     *
     * @return String that contains all books available
     */
    @GetMapping(path = "/list_of_books_available", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "All books available found", response = List.class),
            @ApiResponse(code = 500, message = "Internal BookShelf server error")})
    @ResponseStatus(code = HttpStatus.OK)
    public String getAllBooksAvailable() {
        Book booksAvailable = bookShelfService.getAllBooksAvailable();
        log.info("Books available found= {}", booksAvailable);
        return String.format("Books available %s", booksAvailable);
    }

    /**
     * GET Request to receive a list of all books
     *
     * @return String that contains all books available
     */
    @GetMapping(path = "/list_of_books", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "All books found", response = List.class),
            @ApiResponse(code = 500, message = "Internal BookShelf server error")})
    @ResponseStatus(code = HttpStatus.OK)
    public String getAllBooks() {
        Book books = bookShelfService.getAllBooks();
        log.info("Books found={}", books);
        return String.format("Books found %s", books);
    }

    /**
     * DELETE Request to remove one book based on the id.
     *
     * @param id id of the book
     * @return String that informs about the removal of the book
     */
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Book deleted", response = String.class),
            @ApiResponse(code = 404, message = "Book not found"),
            @ApiResponse(code = 409, message = "Can't delete borrowed book"),
            @ApiResponse(code = 500, message = "Internal Category server error")})
    @ResponseStatus(code = HttpStatus.OK)
    public String delete(@PathVariable(name = "id") Integer id) {
        Book found_book = bookRepository.getById(id);
        bookShelfService.delete(id);
        log.info("Deleting Book={}", found_book.getName());
        return String.format("Book %s deleted successfully", found_book.getName());
    }
}