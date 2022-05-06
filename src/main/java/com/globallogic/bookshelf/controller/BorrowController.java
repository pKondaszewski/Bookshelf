package com.globallogic.bookshelf.controller;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.BorrowRepository;
import com.globallogic.bookshelf.service.BorrowService;
import com.globallogic.bookshelf.utils.UserHistory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private BorrowService borrowsService;
    @Autowired
    private BorrowRepository borrowsRepository;
    @Autowired
    private BookRepository bookRepository;

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
    @PostMapping(path = "/byId",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> borrowBook(@RequestBody Borrow borrow) {
        Book book = bookRepository.findById(borrow.getBook().getId()).get();
        try {

            borrowsService.borrowBook(borrow);
            log.info("Borrow creation = {}", borrow);
            return new ResponseEntity<>(
                    String.format("Borrow id=%s created successfully", borrow.getId()),
                    HttpStatus.CREATED);
        } catch (BookshelfResourceNotFoundException b1) {
            return new ResponseEntity<>(
                    String.format("Book with author : %s and name : %s doesn't exist.",
                            borrow.getBook().getAuthor(),
                            borrow.getBook().getTitle()),
                    HttpStatus.NOT_FOUND);
        } catch (BookshelfConflictException b2) {
            return new ResponseEntity<>(
                    String.format("Book with author : %s and name : %s is already borrowed.",
                            book.getAuthor(),
                            book.getTitle()),
                    HttpStatus.CONFLICT);
        }
    }

    /**
     * POST Request to borrow a book based on book's author and title
     *
     * @param author author of the book
     * @param title title of the book
     * @param firstname firstname of the person that is borrowing the book
     * @param surname surname of the person that is borrowing the book
     * @param borrowDate date of the borrow
     * @param comment additional comment for the borrow
     * @return @return ResponseEntity that informs about the borrowing of the book.
     */
    @PostMapping(path = "/byAuthorAndTitle")
    @ApiOperation(value = "Borrows a book based on author and title")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Book borrowed"),
                            @ApiResponse(code = 404, message = "Bad Request"),
                            @ApiResponse(code = 500, message = "Internal Bookshelf server error"),
                            @ApiResponse(code = 409, message = "Book is already borrowed")})
    public ResponseEntity<String> borrowBookByAuthorAndTitle(@RequestParam String author,
                                                             @RequestParam String title,
                                                             @RequestParam String firstname,
                                                             @RequestParam String surname,
                                                             @RequestParam(required = false)
                                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") Date borrowDate,
                                                             @RequestParam(required = false) String comment) {
        try {
            borrowsService.borrowBookByAuthorAndTitle(author, title, firstname, surname, borrowDate, comment);
            log.info("Borrow creation with author : {} and title : {}", author, title);
            return new ResponseEntity<>(
                    String.format("Borrow with author : %s and title : %s created successfully", firstname, surname),
                    HttpStatus.CREATED);
        } catch (BookshelfResourceNotFoundException exception) {
            return new ResponseEntity<>(
                    String.format("Book with author : %s and title : %s doesn't exist.", firstname, surname),
                    HttpStatus.NOT_FOUND);
        } catch (BookshelfConflictException exception) {
            return new ResponseEntity<>(
                    String.format("Book with author : %s and title : %s is already borrowed.", firstname, surname),
                    HttpStatus.CONFLICT);
        }
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
        try {
            borrowsService.returnBook(borrow);
            log.info("Book returned = {}", borrow);
            return new ResponseEntity<>(
                    String.format("Borrow id=%s returned successfully", borrow.getId()),
                    HttpStatus.CREATED);
        } catch (BookshelfResourceNotFoundException b1) {
            return new ResponseEntity<>(
                    String.format("Book with author : %s and name : %s doesn't exist.",
                            borrow.getBook().getAuthor(),
                            borrow.getBook().getTitle()),
                    HttpStatus.NOT_FOUND);
        } catch (BookshelfConflictException b2) {
            return new ResponseEntity<>(
                    String.format("Book with author : %s and name : %s is not borrowed.",
                            borrow.getBook().getAuthor(),
                            borrow.getBook().getTitle()),
                    HttpStatus.CONFLICT);
        }
    }

    /**
     * DELETE Request to remove one borrow based on the id.
     *
     * @param id id of the borrow
     * @return ResponseEntity that informs about the removal of the borrow
     */
    @DeleteMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Deleting specific borrow by id")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Borrow deleted"),
                            @ApiResponse(code = 404, message = "Borrow not found"),
                            @ApiResponse(code = 409, message = "Borrow is still active"),
                            @ApiResponse(code = 500, message = "Internal Bookshelf server error")})
    public ResponseEntity<String> deleteBorrow(@RequestParam Integer id) {
        try {
            borrowsService.deleteBorrow(id);
            log.info("Deleting borrow id={}", id);
            return new ResponseEntity<>(String.format("Borrow id=%d deleted successfully", id), HttpStatus.OK);
        } catch (BookshelfResourceNotFoundException exception) {
            return new ResponseEntity<>(String.format("Borrow with id=%d doesn't exist", id), HttpStatus.NOT_FOUND);
        } catch (BookshelfConflictException exception) {
            return new ResponseEntity<>(String.format("Borrow with id=%d is still active. Can't delete",id),
                    HttpStatus.CONFLICT);
        }
    }

    /**
     * GET Request to get borrow history and additional information based by the specific user
     *
     * @param firstname String firstname of the user
     * @param surname String surname of the user
     * @return Response entity with list containing finished borrows, active holding books and amount of them.
     */
    @GetMapping(path = "/userHistory", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Getting specific user borrow history")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Borrow history returned"),
                            @ApiResponse(code = 500, message = "Internal Bookshelf server error")})
    public ResponseEntity<UserHistory> getUserBorrowHistory(@RequestParam String firstname,
                                                            @RequestParam String surname) {
        UserHistory userHistory = borrowsService.getUserBorrowHistory(firstname, surname);
        log.info("Showing borrow history of user={} {} : {} {} {}",
                firstname,
                surname,
                userHistory.getReturnedBooks(),
                userHistory.getCurrentlyBorrowedBooks(),
                userHistory.getNumberOfCurrentlyBorrowedBooks());
        return new ResponseEntity<>(userHistory, HttpStatus.OK);

    }

}