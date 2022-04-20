package com.globallogic.bookshelf.controller;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.BorrowRepository;
import com.globallogic.bookshelf.service.BorrowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.util.HashMap;
import java.util.List;

/**
 * Client-server communication class that's processes /borrow requests
 *
 * @author Bartłomiej Chojnacki
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/borrow")
@Slf4j
@Api("Management Api")
public class BorrowController {
    @Autowired
    BorrowService borrowsService;
    @Autowired
    BorrowRepository borrowsRepository;
    @Autowired
    BookRepository bookRepository;

    /**
     * POST Request to borrow a book
     *
     * @param borrow borrow entity.
     * @return ResponseEntity that informs about the borrowing of the book.
     */
    @ApiOperation(value = "Borrows a book based on the id")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Book borrowed", response = Borrow.class),
                            @ApiResponse(code = 404, message = "Bad Request"),
                            @ApiResponse(code = 500, message = "Internal Bookshelf server error"),
                            @ApiResponse(code = 409, message = "Book is already borrowed")})
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> borrowBook(@RequestBody Borrow borrow) {
        Book book = bookRepository.findById(borrow.getBook().getId()).get();
        borrowsService.borrowBook(borrow);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Book borrowed" + book.getName());
    }

    /**
     * PUT Request to return one book based on the id of borrow and book id.
     *
     * @param borrow borrow entity.
     * @return String that informs about the returning of the book.
     */
    @ApiOperation(value = "Returning a book.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Book returned", response = Book.class),
                            @ApiResponse(code = 400, message = "Bad Request"),
                            @ApiResponse(code = 500, message = "Internal Bookshelf server error"),
                            @ApiResponse(code = 404, message = "Book no found")})
    @PutMapping
    public ResponseEntity<String> returnBorrow(@RequestBody Borrow borrow) {
        Book book = bookRepository.findById(borrow.getBook().getId()).get();
        borrowsService.returnBook(borrow);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Book returned" + book.getName());

    }

    /**
     * DELETE Request to remove one borrow based on the id.
     *
     * @param id id of the borrow
     * @return ResponseEntity that informs about the removal of the borrow
     */
    @DeleteMapping(produces = "text/plain")
    @ApiOperation(value = "Deleting specific borrow")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Borrow deleted"),
                            @ApiResponse(code = 404, message = "Borrow not found"),
                            @ApiResponse(code = 409, message = "Borrow is still active"),
                            @ApiResponse(code = 500, message = "Internal Bookshelf server error")})
    public ResponseEntity<String> deleteBorrow(@RequestBody Integer id) {
        borrowsService.deleteBorrow(id);
        log.info("Deleting borrow id={}", id);
        return new ResponseEntity<>(String.format("Borrow id=%d deleted successfully", id), HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Getting specific user borrow history")
    public ResponseEntity<HashMap<List<Borrow>, String>> getUserBorrowHistory(@RequestParam String firstname,
                                                                              @RequestParam String surname) {
        HashMap<List<Borrow>, String> foundBorrows = borrowsService.getUserBorrowHistory(firstname, surname);
        log.info("Showing borrow history of user={} {}", firstname, surname);
        return new ResponseEntity<>(foundBorrows, HttpStatus.OK);
    }
}