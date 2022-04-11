package com.globallogic.bookshelf.controller;


import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.service.BookShelfService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

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
     * POST Request to create a book
     *
     * @param bookSO DTO of the book entity
     * @return DTO of the created book
     */
    @ApiOperation(value = "Creates a book entity.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Book entry created", response = Book.class),
                            @ApiResponse(code = 400, message = "Bad Request"),
                            @ApiResponse(code = 500, message = "Internal Bookshelf server error")})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookSO> create(@RequestBody BookSO bookSO) {
        return new ResponseEntity<>(bookShelfService.create(bookSO),HttpStatus.CREATED);
    }


    /**
     * DELETE Request to remove one book based on the id.
     *
     * @param id id of the book
     * @return ResponseEntity that informs about the removal of the book
     */
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Book deleted", response = String.class),
                            @ApiResponse(code = 404, message = "Book not found"),
                            @ApiResponse(code = 409, message = "Can't delete borrowed book"),
                            @ApiResponse(code = 500, message = "Internal Category server error")})
    public ResponseEntity<String> delete(@PathVariable(name = "id") Integer id) {
        Book found_book = bookRepository.getById(id);
        bookShelfService.delete(id);
        return new ResponseEntity<>("Book deleted" + found_book.getName(), HttpStatus.OK);
    }

    @GetMapping(path = "/listOfBooksAvailable", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "All books available", response = HashMap.class),
                            @ApiResponse(code = 500, message = "Internal BookShelf server error")})
    public ResponseEntity<HashMap<String, Boolean>> getAllBooksAvailable() {
        HashMap<String, Boolean> booksAvailable = bookShelfService.getAllBooksAvailable();
        return new ResponseEntity<>(booksAvailable, HttpStatus.OK);
    }

    @GetMapping(path = "/listOfBooks", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "All books", response = HashMap.class),
                            @ApiResponse(code = 500, message = "Internal BookShelf server error")})
    public ResponseEntity<HashMap<String, String>> getAllBooks() {
        HashMap<String, String> booksAvailable = bookShelfService.getAllBooks();
        return new ResponseEntity<>(booksAvailable, HttpStatus.OK);
    }
}