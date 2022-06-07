package com.globallogic.bookshelf.controller;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.utils.UserHistory;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Interface for BorrowController
 *
 * @author Przemysław Kondaszewski
 */
public interface BorrowInterface {

    @ApiOperation(value = "Borrows a book based on the id")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Book borrowed"),
                            @ApiResponse(code = 404, message = "Bad Request"),
                            @ApiResponse(code = 500, message = "Internal Bookshelf server error"),
                            @ApiResponse(code = 409, message = "Book is already borrowed")})
    @PostMapping(path = "/byId", produces = MediaType.TEXT_PLAIN_VALUE)
    ResponseEntity<String> borrowBookById(@RequestParam Integer BookId,
                                                 @RequestParam String FirstName,
                                                 @RequestParam String LastName,
                                                 @RequestParam(required = false)
                                                    @DateTimeFormat(pattern = "yyyy-MM-dd") Date BorrowDate,
                                                 @RequestParam(required = false) String Comment);

    @PostMapping(path = "/byAuthorAndTitle")
    @ApiOperation(value = "Borrows a book based on author and title")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Book borrowed"),
                            @ApiResponse(code = 404, message = "Bad Request"),
                            @ApiResponse(code = 500, message = "Internal Bookshelf server error"),
                            @ApiResponse(code = 409, message = "Book is already borrowed")})
    ResponseEntity<String> borrowBookByAuthorAndTitle(@RequestParam String BookAuthor,
                                                      @RequestParam String BookTitle,
                                                      @RequestParam String FirstName,
                                                      @RequestParam String LastName,
                                                      @RequestParam(required = false)
                                                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date BorrowDate,
                                                      @RequestParam(required = false) String Comment);

    @PutMapping(path = "/bookReturn")
    @ApiOperation(value = "Returning a book")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Book returned", response = Book.class),
                            @ApiResponse(code = 400, message = "Bad Request"),
                            @ApiResponse(code = 500, message = "Internal Bookshelf server error"),
                            @ApiResponse(code = 404, message = "Book no found")})
    ResponseEntity<String> returnBorrow(@RequestParam Integer borrowId);

    @DeleteMapping(path = "/borrowDelete")
    @ApiOperation(value = "Deleting specific borrow by id")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Borrow deleted"),
                            @ApiResponse(code = 404, message = "Borrow not found"),
                            @ApiResponse(code = 409, message = "Borrow is still active"),
                            @ApiResponse(code = 500, message = "Internal Bookshelf server error")})
    ResponseEntity<String> deleteBorrow(@RequestParam Integer borrowId);

    @GetMapping(path = "/userHistory")
    @ApiOperation(value = "Getting specific user borrow history")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Borrow history returned"),
                            @ApiResponse(code = 500, message = "Internal Bookshelf server error")})
    ResponseEntity<UserHistory> getUserBorrowHistory(@RequestParam String firstName,
                                                     @RequestParam String lastName);
}
