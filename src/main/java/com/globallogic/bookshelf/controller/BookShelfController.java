package com.globallogic.bookshelf.controller;


import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
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
     * POST Request to create a book
     *
     * @param book body of the book
     * @return ResponseEntity that informs about the creation of the book
     */
    @ApiOperation(value = "Creates a book entity.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Book entry created", response = Book.class),
                            @ApiResponse(code = 400, message = "Bad Request"),
                            @ApiResponse(code = 500, message = "Internal Bookshelf server error")})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> create(@RequestBody Book book) {
        bookShelfService.create(book);
        log.info("Creating book={}", book);
        return new ResponseEntity<>(String.format("Book %s created successfully", book.getName()), HttpStatus.CREATED);
    }

    /**
     * DELETE Request to remove one book based on the id.
     *
     * @param id id of the book
     * @return ResponseEntity that informs about the removal of the book
     */
    @DeleteMapping(path = "/{id}", produces = "text/plain")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Book deleted", response = String.class),
                            @ApiResponse(code = 404, message = "Book not found"),
                            @ApiResponse(code = 409, message = "Can't delete borrowed book"),
                            @ApiResponse(code = 500, message = "Internal Category server error")})
    public ResponseEntity<String> delete(@PathVariable(name = "id") Integer id) {
        try {
            Book found_book = bookRepository.getById(id);
            bookShelfService.delete(id);
            return new ResponseEntity<>("Book deleted " + found_book.getName(), HttpStatus.OK);
        } catch (BookshelfResourceNotFoundException b1) {
            return new ResponseEntity<>(String.format("Book with id=%d doesn't exist", id), HttpStatus.NOT_FOUND);
        } catch (BookshelfConflictException b2) {
            return new ResponseEntity<>(String.format("Book with id=%d is still borrowed. Can't delete",id),
                    HttpStatus.CONFLICT);
        }
    }
    /**
     * GET Request to receive a map that shows list of all books Available.
     *
     * @return ResponseEntity that contains history of every book and it's availability.
     */

    @GetMapping(path = "/listOfBooksAvailable", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "All books available", response = HashMap.class),
                            @ApiResponse(code = 500, message = "Internal BookShelf server error")})
    public ResponseEntity<HashMap<Book, String>> getAllBooksAvailable() {
        HashMap<Book, String> booksAvailable = bookShelfService.getAllBooksAvailable();
        return new ResponseEntity<>(booksAvailable, HttpStatus.OK);
    }

    /**
     * GET Request to receive a map that shows list of all books.
     *
     * @return ResponseEntity that contains history of every book and it's availability.
     */
    @GetMapping(path = "/listOfBooks", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "All books", response = HashMap.class),
                            @ApiResponse(code = 500, message = "Internal BookShelf server error")})
    public ResponseEntity<HashMap<String, String>> getAllBooks() {
        HashMap<String, String> booksAvailable = bookShelfService.getAllBooks();
        return new ResponseEntity<>(booksAvailable, HttpStatus.OK);
    }

    /**
     * GET Request to receive a map that shows every book availability.
     *
     * @return ResponseEntity that contains every book and it's availability (actual owner of the book and borrow date) HashMap
     */
    @GetMapping(path = "/booksAvailability", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Shows map book : availability (info about the owner of the book)")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "All books", response = HashMap.class),
                            @ApiResponse(code = 500, message = "Internal BookShelf server error")})
    public ResponseEntity<HashMap<Book, String>> getBooksAvailability() {
        HashMap<Book, String> booksAvailability = bookShelfService.getBooksAvailability();
        log.info("Books availability={}", booksAvailability);
        return new ResponseEntity<>(booksAvailability, HttpStatus.OK);
    }

    /**
     * GET Request to receive a map that shows history of all book.
     *
     * @return ResponseEntity that contains history of every book and it's availability.
     */
    @GetMapping(path = "/bookHistory/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Book History",response = HashMap.class),
    @ApiResponse(code = 500,message = "Internal BookShelf server error")})
    public ResponseEntity<HashMap<Book, List<String>>> getBookHistory(@PathVariable(name = "name") String name){
        HashMap<Book,List<String>> bookHistoryHashMap = bookShelfService.getBooksHistory(name);
        log.info("Book History={}",bookHistoryHashMap);
        return new ResponseEntity<>(bookHistoryHashMap,HttpStatus.OK);
    }

    /**
     * GET Request to receive a map that shows last borrow of book.
     *
     * @return ResponseEntity that contains book and information about who borrow book at the moment.
     */
    @GetMapping(path = "/getListOfBorrowedBooksWithNewestBorrow", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Books History",response = HashMap.class),
            @ApiResponse(code = 500,message = "Internal BookShelf server error")})
    public ResponseEntity<HashMap<Book, String>> getHashMapResponseEntity(){
        HashMap<Book,String> bookHistoryHashMap = bookShelfService.getListOfBorrowedBooksWithNewestBorrow();
        log.info("Books History={}",bookHistoryHashMap);
        return new ResponseEntity<>(bookHistoryHashMap,HttpStatus.OK);
    }
}