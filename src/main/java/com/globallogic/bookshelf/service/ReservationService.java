package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Reservation;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.exeptions.LocalDateTimeException;
import com.globallogic.bookshelf.exeptions.ReservationConflictException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.ReservationRepository;
import com.globallogic.bookshelf.utils.Verification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Reservation service class for reserving books
 * @author Przemyslaw Kondaszewski
 */
@Component
public class ReservationService {

    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;

    public ReservationService(BookRepository bookRepository, ReservationRepository reservationRepository) {
        this.bookRepository = bookRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Creates a reservation for available book. During the time of reservation, book can't be borrowed.
     * @param bookId id of the reserved book
     * @param date end date of the reservation
     * @param time end time of the reservation
     * @param firstname firstname of the user, that's reserve the book
     * @param lastname lastname of the user, that's reserve the book
     * @param comment additional comment to the reservation
     */
    public void reservation(Integer bookId, LocalDate date, LocalTime time,
                            String firstname, String lastname, String comment) {
        Optional<Book> foundBook = bookRepository.findById(bookId);
        if (foundBook.isEmpty()) {
            throw new BookshelfResourceNotFoundException(String.format("Book with id = %d doesn't exist", bookId));
        } else {
            Book book = foundBook.get();
            if (!book.isAvailable()) {
                throw new BookshelfConflictException(String.format("Book with id = %d is borrowed.", bookId));
            } else {
                if (Verification.ofTheReservation(book)) {
                    if (Verification.ofTheLocalDateTime(date, time)) {
                        Reservation reservation = new Reservation(
                                null, firstname, lastname, LocalDate.now(), LocalTime.now(), date, time, comment, book);
                        reservationRepository.save(reservation);
                    } else {
                        throw new LocalDateTimeException(
                                String.format("Given date: %s and time: %s of the reservation is before actual date: %s and time: %s",
                                        date, time,
                                        LocalDateTime.now().toLocalDate(),
                                        LocalDateTime.now().toLocalTime().truncatedTo(ChronoUnit.MINUTES)));
                    }
                } else {
                    throw new ReservationConflictException(String.format("Book with id = %d is already reserved.", bookId));
                }
            }
        }
    }
}
