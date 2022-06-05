package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.entity.Reservation;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.exeptions.LocalDateTimeException;
import com.globallogic.bookshelf.exeptions.ReservationConflictException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.ReservationRepository;
import com.globallogic.bookshelf.utils.Verification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@ContextConfiguration(classes = {ReservationService.class})
@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    private static Integer id;
    private static LocalDate localDate;
    private static LocalTime localTime;
    private static String firstname, lastname, comment;
    private static Book book, unavailableBook;
    private static Reservation reservation;

    @Mock
    private BookRepository bookRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @InjectMocks
    private ReservationService reservationService;

    @BeforeAll
    public static void initVariables() {
        id = 1;
        localDate = LocalDate.of(1970,1,1);
        localTime = LocalTime.of(12, 12);
        firstname = "Jan";
        lastname = "Kowalski";
        comment = "Comment";
        Category category = new Category(1,"category");
        book = new Book(1, "author", "title", true, category);
            unavailableBook = new Book(1, "author", "title", false, category);
    }

    @Test
    public void reservationSuccessTest() {
        Mockito.doReturn(Optional.of(book)).when(bookRepository).findById(id);
        try (MockedStatic<Verification> mockedStatic = mockStatic(Verification.class)) {
            mockedStatic.when(() -> Verification.ofTheReservation(book)).thenReturn(true);
            mockedStatic.when(() -> Verification.ofTheLocalDateTime(any(), any())).thenReturn(true);
            reservationService.reservation(id, localDate, localTime, firstname, lastname, comment);
        }
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    public void reservationBookshelfConflictExceptionTest() {
        Mockito.doReturn(Optional.of(unavailableBook)).when(bookRepository).findById(id);
        Exception exception = assertThrows(BookshelfConflictException.class, () ->
                reservationService.reservation(id, localDate, localTime, firstname, lastname, comment)
        );

        String expectedMessage = String.format("Book with id = %d is borrowed.", id);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void reservationResourceNotFoundExceptionTest() {
        Mockito.doReturn(Optional.empty()).when(bookRepository).findById(id);
        Exception exception = assertThrows(BookshelfResourceNotFoundException.class, () ->
                reservationService.reservation(id, localDate, localTime, firstname, lastname, comment)
        );

        String expectedMessage = String.format("Book with id = %d doesn't exist", id);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void reservationLocalDateTimeExceptionTest() {
        Mockito.doReturn(Optional.of(book)).when(bookRepository).findById(id);
        try (MockedStatic<Verification> mockedStatic = mockStatic(Verification.class)) {
            mockedStatic.when(() -> Verification.ofTheReservation(book)).thenReturn(true);
            mockedStatic.when(() -> Verification.ofTheLocalDateTime(any(), any())).thenReturn(false);
            assertThrows(LocalDateTimeException.class, () ->
                    reservationService.reservation(id, localDate, localTime, firstname, lastname, comment)
            );
        }
    }

    @Test
    public void reservationReservationConflictExceptionTest() {
        Mockito.doReturn(Optional.of(book)).when(bookRepository).findById(id);
        try (MockedStatic<Verification> mockedStatic = mockStatic(Verification.class)) {
            mockedStatic.when(() -> Verification.ofTheReservation(book)).thenReturn(false);

            Exception exception = assertThrows(ReservationConflictException.class, () ->
                    reservationService.reservation(id, localDate, localTime, firstname, lastname, comment)
            );

            String expectedMessage = String.format("Book with id = %d is already reserved.", id);
            String actualMessage = exception.getMessage();
            assertTrue(actualMessage.contains(expectedMessage));
        }
    }
}
