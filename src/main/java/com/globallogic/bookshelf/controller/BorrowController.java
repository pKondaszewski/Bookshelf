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
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
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
     * POST Request to borrow a book by id
     *
     * @param BookId     id of the book
     * @param FirstName  FirstName of the person that is borrowing the book
     * @param LastName    lastname of the person that is borrowing the book
     * @param BorrowDate date of the borrow
     * @param Comment    additional comment for the borrow
     * @return ResponseEntity that informs about the borrowing of the book.
     */
    @ApiOperation(value = "Borrows a book based on the id")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Book borrowed"),
                            @ApiResponse(code = 404, message = "Bad Request"),
                            @ApiResponse(code = 500, message = "Internal Bookshelf server error"),
                            @ApiResponse(code = 409, message = "Book is already borrowed")})
    @PostMapping(path = "/byId", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> borrowBookById(@RequestParam Integer BookId,
                                                 @RequestParam String FirstName,
                                                 @RequestParam String LastName,
                                                 @RequestParam(required = false)
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") Date BorrowDate,
                                                 @RequestParam(required = false) String Comment) {

        try {
            borrowsService.borrowBookById(BookId, FirstName, LastName, BorrowDate, Comment);
            log.info("Borrow creation with Id : {} ", BookId);
            return new ResponseEntity<>(
                    String.format("You borrow book with id: %s ",BookId),
                    HttpStatus.OK);
        } catch (BookshelfResourceNotFoundException exception) {
            return new ResponseEntity<>(
                    String.format("Book with id : %s doesn't exist.",BookId),
                    HttpStatus.NOT_FOUND);
        } catch (BookshelfConflictException exception) {
            return new ResponseEntity<>(
                    String.format("Book with id : %s is borrowed.",BookId),
                    HttpStatus.CONFLICT);
        }
    }

    /**
     * POST Request to borrow a book based on book's author and title
     *
     * @param BookAuthor     author of the book
     * @param BookTitle      title of the book
     * @param FirstName      firstname of the person that is borrowing the book
     * @param LastName       lastname of the person that is borrowing the book
     * @param BorrowDate     date of the borrow
     * @param Comment        additional comment for the borrow
     * @return @return ResponseEntity that informs about the borrowing of the book.
     */
    @PostMapping(path = "/byAuthorAndTitle")
    @ApiOperation(value = "Borrows a book based on author and title")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Book borrowed"),
                            @ApiResponse(code = 404, message = "Bad Request"),
                            @ApiResponse(code = 500, message = "Internal Bookshelf server error"),
                            @ApiResponse(code = 409, message = "Book is already borrowed")})
    public ResponseEntity<String> borrowBookByAuthorAndTitle(@RequestParam String BookAuthor,
                                                             @RequestParam String BookTitle,
                                                             @RequestParam String FirstName,
                                                             @RequestParam String LastName,
                                                             @RequestParam(required = false)
                                                             @DateTimeFormat(pattern = "yyyy-MM-dd") Date BorrowDate,
                                                             @RequestParam(required = false) String Comment) {
        try {
            borrowsService.borrowBookByAuthorAndTitle(BookAuthor, BookTitle, FirstName, LastName, BorrowDate, Comment);
            log.info("User : {} {} borrows book with author : {} and title : {}", FirstName, LastName, BookAuthor, BookTitle);
            return new ResponseEntity<>(
                    String.format("User : %s %s borrows book with author : %s and title : %s",
                            FirstName, LastName, BookAuthor, BookTitle),
                    HttpStatus.OK);
        } catch (BookshelfResourceNotFoundException exception) {
            return new ResponseEntity<>(
                    String.format("Book with author : %s and title : %s doesn't exist.", FirstName, LastName),
                    HttpStatus.NOT_FOUND);
        } catch (BookshelfConflictException exception) {
            return new ResponseEntity<>(
                    String.format("Book with author : %s and title : %s is already borrowed.", FirstName, LastName),
                    HttpStatus.CONFLICT);
        }
    }

    /**
     * PUT Request to return one book based on the id of borrow and book id.
     *
     * @param borrow_id id of borrow.
     * @return String that informs about the returning of the book.
     */
    @ApiOperation(value = "Returning a book.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Book returned", response = Book.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Bookshelf server error"),
            @ApiResponse(code = 404, message = "Book no found")})
    @PutMapping(path = "/bookReturn")
    public ResponseEntity<String> returnBorrow(@RequestParam Integer borrow_id) {
        try {
            borrowsService.returnBook(borrow_id);
            return new ResponseEntity<>(String.format("Borrow with id= %s ended", borrow_id),
                    HttpStatus.OK);
        } catch (BookshelfResourceNotFoundException exception) {
            return new ResponseEntity<>(
                    String.format("Borrow with id= %s not found", borrow_id),
                    HttpStatus.NOT_FOUND);
        } catch (BookshelfConflictException exception) {
            return new ResponseEntity<>(
                    String.format("Borrow with id : %s is ended.", borrow_id),
                    HttpStatus.CONFLICT);
        }
    }

    /**
     * DELETE Request to remove one borrow based on the id.
     *
     * @param id id of the borrow
     * @return ResponseEntity that informs about the removal of the borrow
     */
    @DeleteMapping(path = "/borrowDelete", produces = MediaType.TEXT_PLAIN_VALUE)
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
            return new ResponseEntity<>(String.format("Borrow with id=%d is still active. Can't delete", id),
                    HttpStatus.CONFLICT);
        }
    }

    /**
     * GET Request to get borrow history and additional information based by the specific user
     *
     * @param firstname String firstname of the user
     * @param lastname   String lastname of the user
     * @return Response entity with list containing finished borrows, active holding books and amount of them.
     */
    @GetMapping(path = "/userHistory", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Getting specific user borrow history")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Borrow history returned"),
            @ApiResponse(code = 500, message = "Internal Bookshelf server error")})
    public ResponseEntity<UserHistory> getUserBorrowHistory(@RequestParam String firstname,
                                                            @RequestParam String lastname) {
        UserHistory userHistory = borrowsService.getUserBorrowHistory(firstname, lastname);
        log.info("Showing borrow history of user={} {} : {} {} {}",
                firstname,
                lastname,
                userHistory.getReturnedBooks(),
                userHistory.getCurrentlyBorrowedBooks(),
                userHistory.getNumberOfCurrentlyBorrowedBooks());
        return new ResponseEntity<>(userHistory, HttpStatus.OK);

    }

}