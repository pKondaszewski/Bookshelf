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
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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
     * GET Request to find one borrow based on the id.
     *
     * @param id id of the borrow.
     * @return DTO of the selected borrow.
     */
    @ApiOperation(value = "Get borrow based on the id .")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Borrows found", response = BorrowSO.class),
            @ApiResponse(code = 404, message = "Borrows not found"),
            @ApiResponse(code = 500, message = "Internal Borrows server error")})
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.OK)
    public BorrowSO get(@PathVariable(name = "id") Integer id) {
        BorrowSO borrowSO = borrowsService.get(id);
        log.info("Returning Borrows={}", borrowSO);
        return borrowSO;
    }

    /**
     * POST Request to create one category based on the name.
     *
     * @param borrow borrow entity.
     * @return String that informs about the borrowing of the book.
     */
    @ApiOperation(value = "Borrows a book based on the id")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Book borrowed", response = Borrow.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Bookshelf server error"),
            @ApiResponse(code = 409, message = "Book is already borrowed")})
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public String borrowBook(@RequestBody Borrow borrow) {
        Book book = bookRepository.findById(borrow.getBook().getId()).get();
        borrowsService.borrowBook(borrow);
        log.info("Borrowing book ={}", borrow.getBook().getName());
        return String.format("Book %s borrow successfully", book.getName());
    }

    /**
     * PUT Request to return one book based on the id of borrow and book id.
     *
     * @param borrow borrow entity.
     * @return String that informs about the returning of the book.
     */
    @ApiOperation(value = "Returning a book.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Book returned", response = Book.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Bookshelf server error"),
            @ApiResponse(code = 404, message = "Book no found")})
    @ResponseStatus(code = HttpStatus.OK)
    @PutMapping
    public String returnBorrow(@RequestBody Borrow borrow) {
        Book book = bookRepository.findById(borrow.getBook().getId()).get();
        borrowsService.returnBook(borrow);
        log.info("Deleting category={}", borrow);
        return String.format("Book %s was returned", book.getName());

    }
}
