package com.globallogic.bookshelf.controller;


import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.exeptions.LocalDateTimeException;
import com.globallogic.bookshelf.exeptions.ReservationConflictException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.service.BookShelfService;
import com.globallogic.bookshelf.service.ReservationService;
import com.globallogic.bookshelf.utils.CustomObjects.CustomBorrow;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
public class BookShelfController implements BookShelfInterface {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookShelfService bookShelfService;
    @Autowired
    private ReservationService reservationService;

    /**
     * POST Request to create a book
     *
     * @param author of book
     * @param title of book
     * @param categoryName name of category
     * @param availability book availability
     * @return ResponseEntity that informs about the creation of the book
     */
    @Override
    public ResponseEntity<String> create(String author, String title, boolean availability, String categoryName) {
        try {
            bookShelfService.create(title, author, availability, categoryName);
            log.info("Book with title: {} and author: {} created successfully", title, author);
            return new ResponseEntity<>(String.format("Book %s created", title), HttpStatus.CREATED);
        } catch (BookshelfResourceNotFoundException exception){
            return new ResponseEntity<>(String.format("Category %s not found", categoryName), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * DELETE Request to remove one book based on the id.
     *
     * @param id id of the book
     * @return ResponseEntity that informs about the removal of the book
     */
    @Override
    public ResponseEntity<String> delete(Integer id) {
        try {
            bookShelfService.delete(id);
            log.info("Book with id = {} deleted successfully", id);
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
    @Override
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
    @Override
    public ResponseEntity<HashMap<String, String>> getAllBooks() {
        HashMap<String, String> allBooksWithAvailabilityHashMap = bookShelfService.getAllBooks();
        log.info("All books = {}", allBooksWithAvailabilityHashMap);
        return new ResponseEntity<>(allBooksWithAvailabilityHashMap, HttpStatus.OK);
    }

    /**
     * GET Request to receive a map that shows every book availability.
     *
     * @return ResponseEntity that contains every book, and it's availability (actual owner of the book and borrow date) HashMap
     */
    @Override
    public ResponseEntity<HashMap<Book, String>> getBooksAvailability() {
        HashMap<Book, String> booksAvailability = bookShelfService.getBooksAvailability();
        log.info("Books availability={}", booksAvailability);
        return new ResponseEntity<>(booksAvailability, HttpStatus.OK);
    }

    /**
     * GET Request to receive a map that shows history of all book.
     *
     * @param name name of the book
     * @return ResponseEntity that contains history of every book, and it's availability.
     */
    @Override
    public ResponseEntity<HashMap<Book, List<String>>> getBookHistory(String name) {
        HashMap<Book, List<String>> bookHistoryHashMap = null;
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
    @Override
    public ResponseEntity<List<CustomBorrow>> getNewestActiveBorrowSort(String sort) {
        List<CustomBorrow> bookHistoryHashMap = bookShelfService.getListOfBorrowedBooksSort(sort);
        log.info("Sorted books History={}", bookHistoryHashMap);
        return new ResponseEntity<>(bookHistoryHashMap, HttpStatus.OK);
    }

    /**
     * GET Request to receive a Hashmap that shows last borrow of book.
     *
     * @return ResponseEntity that contains book and information about who borrow book at the moment.
     */
    @Override
    public ResponseEntity<List<String>> getNewestActiveBorrow() {
        List<String> bookHistoryHashMap = bookShelfService.getListOfBorrowedBooks();
        log.info("Books History={}", bookHistoryHashMap);
        return new ResponseEntity<>(bookHistoryHashMap,HttpStatus.OK);
    }

    /**
     * PUT Request to set a reservation on a specific book.
     *
     * @param bookId id of the book
     * @param date end date of the reservation
     * @param time end time of the reservation
     * @param firstname firstname of the reservation owner
     * @param lastname lastname of the reservation owner
     * @param comment additional comment for the reservation
     * @return String, brief information about the reservation
     */
    @Override
    public ResponseEntity<String> reservation(Integer bookId, LocalDate date, LocalTime time,
                                              String firstname, String lastname, String comment) {
        try {
            reservationService.reservation(bookId, date, time, firstname, lastname, comment);
            log.info("Reservation created successfully. Book with id = {} is reserved by {} {}. " +
                            "Date of the reservation: {}. Time of the reservation: {}",
                    bookId, firstname, lastname, date, time);
            return new ResponseEntity<>(
                    String.format("Reservation created successfully. Book with id: %d is reserved by %s %s. " +
                                    "Date of the reservation: %s. Time of the reservation: %s",
                            bookId, firstname, lastname, date, time), HttpStatus.OK);
        } catch (BookshelfResourceNotFoundException exception) {
            return new ResponseEntity<>(String.format("Book with id = %d not found.", bookId), HttpStatus.NOT_FOUND);
        } catch (ReservationConflictException exception) {
            return new ResponseEntity<>(String.format("Book with id = %d is already reserved.", bookId), HttpStatus.CONFLICT);
        } catch (LocalDateTimeException exception) {
            return new ResponseEntity<>(
                    String.format("Given date: %s and time: %s of the reservation is before actual date: %s and time: %s",
                            date, time, LocalDateTime.now().toLocalDate(),
                            LocalDateTime.now().toLocalTime().truncatedTo(ChronoUnit.MINUTES)), HttpStatus.CONFLICT);
        } catch (BookshelfConflictException exception) {
            return new ResponseEntity<>(String.format("Book with id = %d is not available.", bookId), HttpStatus.CONFLICT);
        }
    }
}