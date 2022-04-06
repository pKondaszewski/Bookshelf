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
 * Client-server communication class that's processes /category requests
 *
 * @author Bartłomiej Chojnacki
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/borrows")
@Slf4j
@Api("Management Api")
public class BorrowController {
    @Autowired
    BorrowService borrowsService;
    @Autowired
    BorrowRepository borrowsRepository;
    @Autowired
    BookRepository bookRepository;

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Borrows found", response = BorrowSO.class),
            @ApiResponse(code = 404, message = "Borrows not found"),
            @ApiResponse(code = 500, message = "Internal Borrows server error")})
    @ResponseStatus(code = HttpStatus.OK)
    public BorrowSO get(@PathVariable(name = "id") Integer id) {
        BorrowSO borrowsSO = borrowsService.get(id);
        log.info("Returning Borrows={}", borrowsSO);
        return borrowsSO;
    }

    /**
     * POST Request to create a borrow
     * @param borrow DTO of the book entity
     * @return DTO of the created book
     */
    @ApiOperation(value = "Creates a book entity.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Book entry created", response = Book.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Bookshelf server error"),
            @ApiResponse(code = 404, message = "Book no found")})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public String addBorrow(@RequestBody Borrow borrow) {
        Book book = bookRepository.findById(borrow.getBook().getId()).get();
        if (book.isAvailable() == false){
            return "Your requested book \"" + book.getName() + "\" is out of stock!";
        }

        book.setAvailable(false);
        bookRepository.save(book);

        borrowsRepository.save(borrow);
        return borrow.getSurname() + " has borrowed one copy of \"" + book.getName() + "\"!";
    }

    /**
     * PUT Request to create a borrow
     * @param borrowBody DTO of the borrow entity
     * @return DTO of the
     */
    @ApiOperation(value = "Returning a book.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Book returned", response = Book.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Bookshelf server error"),
            @ApiResponse(code = 404, message = "Book no found")})
    @PutMapping
    public String returnBorrow(@RequestBody Borrow borrowBody){
        Integer id = borrowBody.getId();
        Borrow borrow = borrowsRepository.findById(id).get();
        Book book = bookRepository.findById(borrow.getId()).get();

        book.setAvailable(true);
        bookRepository.save(book);
        Date currentDate = new Date();
        borrow.setReturned(currentDate);
        borrowsRepository.save(borrow);
        return borrow.getSurname() + " return book " + book.getName();
    }
}
