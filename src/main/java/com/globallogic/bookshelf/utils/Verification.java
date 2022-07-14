package com.globallogic.bookshelf.utils;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.entity.Reservation;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.repository.ReservationRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * Special class for verifying specific data provided to a specific method.
 */
@Component
public class Verification {

    static ReservationRepository reservationRepository;

    public Verification(ReservationRepository reservationRepository) {
        Verification.reservationRepository = reservationRepository;
    }

    /**
     * Method that's handle category setup for certain book entity (sets id based on the category object)
     * @param category object of the category
     * @param book object of the book
     *
     */
    public static void ofTheCategory(Category category, Book book) {
        if (category == null) {
            throw new BookshelfResourceNotFoundException("Category not found");
        } else if (category.getName().equals("Default")) {
            book.getCategory().setId(4);
        } else {
            book.setCategory(category);
        }
    }

    /**
     * Method that's handle date setup. Returns date object initialized with current date or saved param value.
     *
     * @param date date to be verified
     * @return date value (current or given as param)
     */
    public static Date ofTheDate(Date date) {
        return date == null ? new Date() : date;
    }

    /**
     * Method that's check reservation status of the book.
     * @param book book to be checked for reservation status
     * @return boolean if book can be reserved, return true.
     */
    public static boolean ofTheReservation(Book book) {
        List<Reservation> allReservationsOfTheBook = reservationRepository.findByBook(book);
        if (allReservationsOfTheBook.isEmpty()) {
            return true;
        } else {
            Reservation reservation = allReservationsOfTheBook.get(allReservationsOfTheBook.size() - 1);
            LocalDate endDateOfReservation = reservation.getEndDateOfReservation();
            LocalTime endTimeOfReservation = reservation.getEndTimeOfReservation();
            LocalDateTime reservationDateTime = LocalDateTime.of(endDateOfReservation, endTimeOfReservation);
            return LocalDateTime.now().isAfter(reservationDateTime);
        }
    }

    /**
     * Method that's check if given date and time are before or after actual date and time
     * @param localDate given date to check
     * @param localTime given time to check
     * @return boolean is given date and time are after actual date and time
     */
    public static boolean ofTheLocalDateTime(LocalDate localDate, LocalTime localTime) {
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        return localDateTime.isAfter(LocalDateTime.now());
    }
}
