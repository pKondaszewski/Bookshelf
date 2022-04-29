package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.BorrowRepository;
import com.globallogic.bookshelf.utils.UserHistory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.sql.Date;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BorrowServiceTests {

    private static Book availableBookTest, notAvailableBookTest, available2BookTest;
    private static Borrow borrow3Test, borrow1Test, borrow2Test;
    private static String firstnameTest, surnameTest, bookAuthorTest, bookNameTest;
    private static List<Borrow> borrowListTest;
    private static UserHistory correctUserHistory;

    @Mock
    private static BorrowRepository borrowRepository;

    @Mock
    private static BookRepository bookRepository;

    @InjectMocks
    private static BorrowService service;

    private static final int ID_TEST = 1;

    @BeforeAll
    public static void setModel() {
        service = new BorrowService(borrowRepository, bookRepository);

        Date testDate1 = Date.valueOf(LocalDate.of(1,1,1));
        Date testDate2 = Date.valueOf(LocalDate.of(2,2,2));

        bookAuthorTest = "BookAuthorTest";
        bookNameTest = "BookNameTest";
        notAvailableBookTest = new Book(1, bookAuthorTest, bookNameTest, false,
                new Category(1, "testCategory"));
        availableBookTest = new Book(2, bookAuthorTest, bookNameTest, true,
                new Category(2, "testCategory"));
        available2BookTest = new Book(2, bookAuthorTest, bookNameTest, true,
                new Category(2, "testCategory"));

        borrow1Test = new Borrow(1, testDate1, testDate2,"Arek","Darek","awdawwda", availableBookTest);
        borrow2Test = new Borrow(2, testDate1,null,"Arek","Darek","awdawwda", notAvailableBookTest);
        borrow3Test = new Borrow(3, null, null,"Arek","Darek","awdawwda", availableBookTest);

        borrowListTest = new ArrayList<>();
        borrowListTest.add(borrow1Test);
        borrowListTest.add(borrow2Test);

        List<Borrow> foundBorrowsTest = new ArrayList<>();
        foundBorrowsTest.add(borrow1Test);

        correctUserHistory = new UserHistory(foundBorrowsTest,
                "BookNameTest; ",
                1);

        firstnameTest = "Jan";
        surnameTest = "Kowalski";
    }

    @Test
    public void testBorrowBookByAuthorAndTitleSuccess() {
        Mockito.doReturn(availableBookTest).when(bookRepository).findBookByAuthorAndName(bookAuthorTest, bookNameTest);

        service.borrowBookByAuthorAndTitle(borrow3Test);

        Mockito.verify(borrowRepository).save(borrow3Test);
    }

    @Test
    public void testBorrowBookByAuthorAndTitleSuccessWithNullReturnDate() {
        Mockito.doReturn(available2BookTest).when(bookRepository).findBookByAuthorAndName(bookAuthorTest, bookNameTest);

        service.borrowBookByAuthorAndTitle(borrow3Test);

        Mockito.verify(borrowRepository).save(borrow3Test);
    }

    @Test
    public void testBorrowBookByAuthorAndTitleResourceNotFoundException() {
        Mockito.doReturn(null).when(bookRepository).findBookByAuthorAndName(bookAuthorTest, bookNameTest);


        Exception exception = assertThrows(BookshelfResourceNotFoundException.class, () ->
                service.borrowBookByAuthorAndTitle(borrow3Test)
        );

        String expectedMessage = String.format(
                "Book with author : %s and name : %s doesn't exist.", bookAuthorTest, bookNameTest
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testBorrowBookByAuthorAndTitleConflictException() {
        Mockito.doReturn(notAvailableBookTest).when(bookRepository).findBookByAuthorAndName(bookAuthorTest, bookNameTest);


        Exception exception = assertThrows(BookshelfConflictException.class, () ->
                service.borrowBookByAuthorAndTitle(borrow3Test)
        );

        String expectedMessage = String.format(
                "Book with author : %s and name : %s is already borrowed.", bookAuthorTest, bookNameTest
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testDeleteBorrowSuccess() {
        Mockito.doReturn(Optional.of(borrow1Test)).when(borrowRepository).findById(ID_TEST);

        service.deleteBorrow(ID_TEST);

        Mockito.verify(borrowRepository).deleteById(ID_TEST);
    }

    @Test
    public void testDeleteBorrowResourceNotFoundException() {
        Mockito.doReturn(Optional.empty()).when(borrowRepository).findById(ID_TEST);

        Exception exception = assertThrows(BookshelfResourceNotFoundException.class, () ->
                service.deleteBorrow(ID_TEST)
        );

        String expectedMessage = String.format("Borrow with id=%d doesn't exist", ID_TEST);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testDeleteBorrowConflictException() {
        Mockito.doReturn(Optional.of(borrow2Test)).when(borrowRepository).findById(ID_TEST);

        Exception exception = assertThrows(BookshelfConflictException.class, () ->
                service.deleteBorrow(ID_TEST)
        );

        String expectedMessage = String.format("Borrow with id=%d is still active. Can't delete", ID_TEST);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testGetUserBorrowHistory() {
        Mockito.doReturn(borrowListTest).when(borrowRepository)
                .findBorrowsByFirstnameAndSurname(firstnameTest, surnameTest);

        UserHistory testedList = service.getUserBorrowHistory(firstnameTest, surnameTest);

        assertThat(testedList).usingRecursiveComparison().isEqualTo(correctUserHistory);
    }

}
