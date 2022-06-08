package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.client.ShelfUserFeignClient;
import com.globallogic.bookshelf.controller.ShelfUserController;
import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.exeptions.ReservationConflictException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.BorrowRepository;
import com.globallogic.bookshelf.repository.ReservationRepository;
import com.globallogic.bookshelf.utils.StringRepresentation;
import com.globallogic.bookshelf.utils.UserHistory;
import com.globallogic.bookshelf.utils.Verification;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {BorrowService.class})
@ExtendWith(MockitoExtension.class)
public class BorrowServiceTest {


    private static Book availableBook, availableBook3, notAvailableBook, notAvailableBook2;
    private static Borrow borrow1, borrow2, borrow3, borrow6, borrow7;
    private static String firstname, lastname, author, title;
    private static List<Borrow> borrowList;
    private static UserHistory correctUserHistory;
    private static ResponseEntity<?> responseEntity;

    @Mock
    private static BookRepository bookRepository;

    @Mock
    private static BorrowRepository borrowRepository;

    @Mock
    private static ShelfUserController shelfUserController;
    @Mock
    private static ShelfUserFeignClient shelfUserFeignClient;

    private static ReservationRepository reservationRepository;

    @InjectMocks
    private static BorrowService borrowService;

    private static final int id = 1;
    private static Date date1;


    @BeforeAll
    public static void initVariables() {
        date1 = Date.valueOf(LocalDate.of(1, 1, 1));
        Date date2 = Date.valueOf(LocalDate.of(2, 2, 2));

        String bookAuthor = "bookAuthor";
        String bookName = "bookName";

        notAvailableBook = new Book(1, bookAuthor, bookName, false,
                new Category(1, "category"));
        notAvailableBook2 = new Book(1, bookAuthor, bookName, false,
                new Category(2, "category"));
        availableBook = new Book(2, bookAuthor, bookName, true,
                new Category(2, "category"));
        availableBook3 = new Book(2, bookAuthor, bookName, true,
                new Category(2, "category"));

        firstname = "Jan";
        lastname = "Kowalski";
        author = "Author";
        title = "Title";
        String comment = "Comment";

        borrow1 = new Borrow(1, date1, date2, firstname, lastname, comment, availableBook);
        borrow2 = new Borrow(2, date1, null, firstname, lastname, comment, notAvailableBook);
        borrow3 = new Borrow(null, null, null, firstname, lastname, null, availableBook3);
        borrow6 = new Borrow(4, date1, null, firstname, lastname, comment, notAvailableBook2);
        borrow7 = new Borrow(null, null, null, firstname, lastname, null, availableBook);

        borrowList = new ArrayList<>();
        borrowList.add(borrow1);
        borrowList.add(borrow2);

        List<String> completedBorrows = new ArrayList<>();
        completedBorrows.add(StringRepresentation.ofTheBorrow(borrow1));

        List<String> uncompletedBorrows = new ArrayList<>();
        uncompletedBorrows.add(StringRepresentation.ofTheBorrow(borrow2));

        correctUserHistory = new UserHistory(completedBorrows, uncompletedBorrows, 1);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.TEXT_PLAIN);

        responseEntity = new ResponseEntity<>(
                "ACTIVE",
                header,
                HttpStatus.OK
        );
    }

    @Test
    public void borrowBookByIdSuccessTest() {
        when(shelfUserFeignClient.getUserStatus((String) any(), (String) any()))
                .thenReturn((ResponseEntity<String>) responseEntity);
        Mockito.doReturn(Optional.of(availableBook)).when(bookRepository).findById(id);
        try (MockedStatic<Verification> mockedStatic = mockStatic(Verification.class)) {
            mockedStatic.when(() -> Verification.ofTheReservation(availableBook)).thenReturn(true);
            borrowService.borrowBookById(id, firstname, lastname, null, null);
        }
        Mockito.verify(borrowRepository).save(borrow7);
    }


    @Test
    public void borrowBookByIdResourceNotFoundTest() {
        when(shelfUserFeignClient.getUserStatus((String) any(), (String) any()))
                .thenReturn((ResponseEntity<String>) responseEntity);
        Mockito.doReturn(Optional.empty()).when(bookRepository).findById(id);

        Exception exception = assertThrows(BookshelfResourceNotFoundException.class, () ->
                borrowService.borrowBookById(id, firstname, lastname, null, null)
        );

        String expectedMessage = String.format("Book with id: %s doesn't exist.", id);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void borrowBookByIdConflictExceptionTest() {
        when(shelfUserFeignClient.getUserStatus((String) any(), (String) any()))
                .thenReturn((ResponseEntity<String>) responseEntity);
        Mockito.doReturn(Optional.of(notAvailableBook)).when(bookRepository).findById(notAvailableBook.getId());

        Exception exception = assertThrows(BookshelfConflictException.class, () ->
                borrowService.borrowBookById(notAvailableBook.getId(), borrow1.getFirstname(),
                        borrow1.getLastname(), borrow1.getBorrowed(), borrow1.getComment())
        );

        String expectedMessage = String.format("Book with id: %s is already borrowed.", borrow1.getId());
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void returnBookTest() {
        Mockito.doReturn(Optional.of(borrow6)).when(borrowRepository).findById(borrow6.getId());
        Mockito.doReturn(Optional.of(notAvailableBook2)).when(bookRepository).findById(id);
        borrowService.returnBook(4);
        Mockito.verify(borrowRepository).save(borrow6);
    }

    @Test
    public void returnBookResourceNotFoundExceptionTest() {
        Exception exception = assertThrows(BookshelfResourceNotFoundException.class, () ->
                borrowService.returnBook(id)
        );

        String expectedMessage = String.format("Borrow with id= %s not found", id);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void borrowBookByAuthorAndTitleSuccessTest() {
        when(shelfUserFeignClient.getUserStatus((String) any(), (String) any()))
                .thenReturn((ResponseEntity<String>) responseEntity);
        Mockito.doReturn(availableBook3).when(bookRepository).findByAuthorAndTitle(author, title);
        try (MockedStatic<Verification> mockedStatic = mockStatic(Verification.class)) {
            mockedStatic.when(() -> Verification.ofTheReservation(availableBook3)).thenReturn(true);
            borrowService.borrowBookByAuthorAndTitle(author, title, firstname, lastname, null, null);
        }
        Mockito.verify(borrowRepository).save(borrow3);
    }



    @Test
    public void borrowBookByAuthorAndTitleResourceNotFoundExceptionTest() {
        when(shelfUserFeignClient.getUserStatus((String) any(), (String) any()))
                .thenReturn((ResponseEntity<String>) responseEntity);
        Mockito.doReturn(null).when(bookRepository).findByAuthorAndTitle(author, title);


        Exception exception = assertThrows(BookshelfResourceNotFoundException.class, () ->
                borrowService.borrowBookByAuthorAndTitle(author, title, firstname, lastname, date1, null)
        );

        String expectedMessage = String.format(
                "Book with author: %s and title: %s doesn't exist.", author, title
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

//    @Test
//    public void borrowBookByAuthorAndTitleConflictExceptionTest() {
//        when(shelfUserFeignClient.getUserStatus((String) any(), (String) any()))
//                .thenReturn((ResponseEntity<String>) responseEntity);
//        Mockito.doReturn(notAvailableBook).when(bookRepository).findByAuthorAndTitle(author, title);
//
//
//        Exception exception = assertThrows(BookshelfConflictException.class, () ->
//                borrowService.borrowBookByAuthorAndTitle(author, title, firstname, lastname, date1, null)
//        );
//
//        String expectedMessage = String.format(
//                "Book with author: %s and title: %s is already borrowed.", author, title
//        );
//        String actualMessage = exception.getMessage();
//        assertTrue(actualMessage.contains(expectedMessage));
//    }

    @Test
    public void deleteBorrowSuccessTest() {
        Mockito.doReturn(Optional.of(borrow1)).when(borrowRepository).findById(id);

        borrowService.deleteBorrow(id);

        Mockito.verify(borrowRepository).deleteById(id);
    }

    @Test
    public void deleteBorrowResourceNotFoundExceptionTest() {
        Mockito.doReturn(Optional.empty()).when(borrowRepository).findById(id);

        Exception exception = assertThrows(BookshelfResourceNotFoundException.class, () ->
                borrowService.deleteBorrow(id)
        );

        String expectedMessage = String.format("Borrow with id=%d doesn't exist", id);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void deleteBorrowConflictExceptionTest() {
        Mockito.doReturn(Optional.of(borrow2)).when(borrowRepository).findById(id);

        Exception exception = assertThrows(BookshelfConflictException.class, () ->
                borrowService.deleteBorrow(id)
        );

        String expectedMessage = String.format("Borrow with id=%d is still active. Can't delete", id);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void getUserBorrowHistoryTest() {
        Mockito.doReturn(borrowList).when(borrowRepository)
                .findAllByFirstnameAndLastname(firstname, lastname);

        UserHistory testedList = borrowService.getUserBorrowHistory(firstname, lastname);

        assertThat(testedList).usingRecursiveComparison().isEqualTo(correctUserHistory);
    }
}