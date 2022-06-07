package com.globallogic.bookshelf.controller;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.utils.CustomObjects.CustomBorrow;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

/**
 * Interface for BookShelfController
 *
 * @author Przemyslaw Kondaszewski
 */
public interface BookShelfInterface {

    @PostMapping(path = "/bookCreate",produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Create a book entity.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Book entry created"),
                            @ApiResponse(code = 400, message = "Bad Request"),
                            @ApiResponse(code = 500, message = "Internal Bookshelf server error")})
    ResponseEntity<String> create(@RequestParam String author, @RequestParam String title, @RequestParam boolean availability,
                                  @RequestParam(required = false, defaultValue = "Default") String categoryName);

    @DeleteMapping(path = "/{id}")
    @ApiOperation(value = "Delete a book entity.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Book deleted"),
                            @ApiResponse(code = 404, message = "Book not found"),
                            @ApiResponse(code = 409, message = "Can't delete borrowed book"),
                            @ApiResponse(code = 500, message = "Internal Category server error")})
    ResponseEntity<String> delete(@PathVariable(name = "id") Integer id);

    @GetMapping(path = "/getAllBooksAvailable", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Show available books")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "All books available"),
                            @ApiResponse(code = 500, message = "Internal BookShelf server error")})
    ResponseEntity<HashMap<Book, String>> getAllBooksAvailable();

    @GetMapping(path = "/listOfBooks", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all books")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "All books", response = HashMap.class),
                            @ApiResponse(code = 500, message = "Internal BookShelf server error")})

    ResponseEntity<HashMap<String, String>> getAllBooks();

    @GetMapping(path = "/booksAvailability", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Shows map book: availability (info about the owner of the book)")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "All books", response = HashMap.class),
                            @ApiResponse(code = 500, message = "Internal BookShelf server error")})
    ResponseEntity<HashMap<Book, String>> getBooksAvailability();

    @GetMapping(path = "/bookHistory/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get borrow history of a specific book")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Book History",response = HashMap.class),
                            @ApiResponse(code = 500, message = "Internal BookShelf server error")})
    ResponseEntity<HashMap<Book, List<String>>> getBookHistory(@PathVariable(name = "name") String name);

    @GetMapping(path = "/getListOfBorrowedBooksSort", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Return list of borrows with availability to sort results")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Books History"),
                            @ApiResponse(code = 500, message = "Internal BookShelf server error")})
    ResponseEntity<List<CustomBorrow>> getNewestActiveBorrowSort(@RequestHeader(
                                                                    value = "sortOption",
                                                                    defaultValue = "",
                                                                    required = false) String sort);

    @GetMapping(path = "/getListOfBorrowedBooks", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get borrowed books with newest active borrow")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Books History"),
                            @ApiResponse(code = 500, message = "Internal BookShelf server error")})
    ResponseEntity<List<String>> getNewestActiveBorrow();

    @PutMapping(path = "/reservation")
    @ApiOperation(value = "Reserving an available book. Makes the book unavailable to borrow for a specified period of time.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Book reserved successfully"),
                            @ApiResponse(code = 404, message = "Book not found"),
                            @ApiResponse(code = 409,
                                    message = "Book is already reserved / Book is borrowed / Given date is before actual date"),
                            @ApiResponse(code = 500, message = "Internal BookShelf server error")})
    ResponseEntity<String> reservation(@RequestParam Integer bookId,
                                              @RequestParam
                                                @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                              @RequestParam
                                                @DateTimeFormat(pattern = "HH:mm") LocalTime time,
                                              @RequestParam String firstname, @RequestParam String lastname,
                                              @RequestParam(required = false) String comment);
}
