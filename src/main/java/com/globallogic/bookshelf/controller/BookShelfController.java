package com.globallogic.bookshelf.controller;


import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.service.BookShelfService;
import com.globallogic.bookshelf.utils.CustomObjects.CustomBorrow;
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
     * @param author of book
     * @param title of book
     * @param categoryName name of category
     * @param availability book availability
     * @return ResponseEntity that informs about the creation of the book
     */
    @ApiOperation(value = "Creates a book entity.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Book entry created", response = Book.class),
                            @ApiResponse(code = 400, message = "Bad Request"),
                            @ApiResponse(code = 500, message = "Internal Bookshelf server error")})
    @PostMapping(path = "/bookCreate",produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> create(@RequestParam String author, @RequestParam String title, @RequestParam boolean availability,
                                        @RequestParam(required = false, defaultValue = "Default") String categoryName) {

        try {
            bookShelfService.create(title, author, availability, categoryName);
            return new ResponseEntity<>(String.format("Book %s created", title), HttpStatus.CREATED);
        }catch (BookshelfResourceNotFoundException exception){
            return new ResponseEntity<>(String.format("Category %s not found", categoryName), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * DELETE Request to remove one book based on the id.
     *
     * @param id id of the book
     * @return ResponseEntity that informs about the removal of the book
     */
    @DeleteMapping(path = "/{id}")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Book deleted", response = String.class),
                            @ApiResponse(code = 404, message = "Book not found"),
                            @ApiResponse(code = 409, message = "Can't delete borrowed book"),
                            @ApiResponse(code = 500, message = "Internal Category server error")})
    public ResponseEntity<String> delete(@PathVariable(name = "id") Integer id) {
        try {
            bookShelfService.delete(id);
            return new ResponseEntity<>(String.format("Book with id=%d delete", id), HttpStatus.OK);
        } catch (BookshelfResourceNotFoundException exception) {
            return new ResponseEntity<>(String.format("Book with id=%d doesn't exist", id), HttpStatus.NOT_FOUND);
        } catch (BookshelfConflictException exception) {
            return new ResponseEntity<>(String.format("Book with id=%d is still borrowed. Can't delete",id),
                    HttpStatus.CONFLICT);
        }
    }
    
    /**
     * GET Request to receive a map that shows list of all books Available.
     *
     * @return ResponseEntity that contains history of every book, and it's availability.
     */
    @GetMapping(path = "/getAllBooksAvailable", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Show available books")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "All books available"),
                            @ApiResponse(code = 500, message = "Internal BookShelf server error")})
    public ResponseEntity<HashMap<Book, String>> getAllBooksAvailable() {
        HashMap<Book, String> booksAvailable = bookShelfService.getAllBooksAvailable();
        log.info("Books available = {}", booksAvailable);
        return new ResponseEntity<>(booksAvailable, HttpStatus.OK);
    }

    /**
     * GET Request to receive a map that shows list of all books.
     *
     * @return ResponseEntity that contains history of every book, and it's availability.
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
     * @return ResponseEntity that contains every book, and it's availability (actual owner of the book and borrow date) HashMap
     */
    @GetMapping(path = "/booksAvailability", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Shows map book: availability (info about the owner of the book)")
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
     * @param name
     * @return ResponseEntity that contains history of every book, and it's availability.
     */
    @GetMapping(path = "/bookHistory/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Book History",response = HashMap.class),
    @ApiResponse(code = 500,message = "Internal BookShelf server error")})
    public ResponseEntity<HashMap<Book, List<String>>> getBookHistory(@PathVariable(name = "name") String name) {
        HashMap bookHistoryHashMap = null;
        try {
            bookHistoryHashMap = bookShelfService.getBooksHistory(name);
            log.info("Book History={}", bookHistoryHashMap);
            return new ResponseEntity<>(bookHistoryHashMap, HttpStatus.OK);
        } catch (NullPointerException exception) {
            return new ResponseEntity<>(bookHistoryHashMap, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * GET Request to receive a Hashmap that shows last borrow of book sort by date.
     *
     * @return ResponseEntity that contains book and information about who borrow book at the moment.
     */
    @GetMapping(path = "/getListOfBorrowedBooksSort", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Return list of borrows with availability to sort results")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Books History"),
                            @ApiResponse(code = 500,message = "Internal BookShelf server error")})
    public ResponseEntity<List<CustomBorrow>> getNewestActiveBorrowSort(@RequestHeader(
                                                                            value = "sortOption",
                                                                            defaultValue = "",
                                                                            required = false) String sort) {
        List<CustomBorrow> bookHistoryHashMap = bookShelfService.getListOfBorrowedBooksSort(sort);
        log.info("Books History={}",bookHistoryHashMap);
        return new ResponseEntity<>(bookHistoryHashMap,HttpStatus.OK);
    }

    /**
     * GET Request to receive a Hashmap that shows last borrow of book.
     *
     * @return ResponseEntity that contains book and information about who borrow book at the moment.
     */
    @GetMapping(path = "/getListOfBorrowedBooks", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Books History",response = HashMap.class),
            @ApiResponse(code = 500,message = "Internal BookShelf server error")})
    public ResponseEntity<List<String>> getNewestActiveBorrow(){
        List<String> bookHistoryHashMap = bookShelfService.getListOfBorrowedBooks();
        log.info("Books History={}",bookHistoryHashMap);
        return new ResponseEntity<>(bookHistoryHashMap,HttpStatus.OK);
    }
}