package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.BorrowRepository;
import com.globallogic.bookshelf.utils.StringRepresentation;
import com.globallogic.bookshelf.utils.UserHistory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ContextConfiguration(classes = {BorrowService.class})
@ExtendWith(MockitoExtension.class)
public class BorrowServiceTest {

    private static Borrow borrow7;
    private static Book availableBook3;
    @Autowired
    private BorrowService borrowService;

    private static Book availableBook, availableBook2, notAvailableBook, notAvailableBook2;
    private static Borrow borrow3, borrow1, borrow2, borrow4, borrow5, borrow6;
    private static String firstname, surname, author, title;
    private static List<Borrow> borrowList;
    private static Book availableBookTest, notAvailableBookTest, available2BookTest, available3BookTest, notAvailableBookTest2;
    private static UserHistory correctUserHistory;


    @Mock
    private static BookRepository bookRepository;

    @Mock
    private static BorrowRepository borrowRepository;

    @InjectMocks
    private static BorrowService service;

    private static final int id = 1;
    private static Date date1;


    @BeforeAll
    public static void initVariables() {
        service = new BorrowService(borrowRepository, bookRepository);

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
        availableBook2 = new Book(4, bookAuthor, bookName, true,
                new Category(2, "category"));
        availableBook3 = new Book(2, bookAuthor, bookName, true,
                new Category(2, "category"));

        firstname = "Jan";
        surname = "Kowalski";
        author = "Author";
        title = "Title";
        String comment = "Comment";

        borrow1 = new Borrow(1, date1, date2, firstname, surname, comment, availableBook);
        borrow2 = new Borrow(2, date1, null, firstname, surname, comment, notAvailableBook);
        borrow3 = new Borrow(null, date1, null, firstname, surname, null, availableBook3);
        borrow4 = new Borrow(4, null, null, firstname, surname, comment, availableBook2);
        borrow5 = new Borrow(4, null, null, firstname, surname, comment, notAvailableBook);
        borrow6 = new Borrow(4, date1, null, firstname, surname, comment, notAvailableBook2);
        borrow7 = new Borrow(null, date1, null, firstname, surname, null, availableBook);

        borrowList = new ArrayList<>();
        borrowList.add(borrow1);
        borrowList.add(borrow2);

        List<String> completedBorrows = new ArrayList<>();
        completedBorrows.add(StringRepresentation.ofTheBorrow(borrow1));

        List<String> uncompletedBorrows = new ArrayList<>();
        uncompletedBorrows.add(StringRepresentation.ofTheBorrow(borrow2));

        correctUserHistory = new UserHistory(completedBorrows, uncompletedBorrows, 1);
    }

    @Test
    public void borrowByIdSuccessTest() {
        Mockito.doReturn(Optional.of(availableBook)).when(bookRepository).findById(id);

        service.borrowBookById(id,firstname,surname,date1,null);

        Mockito.verify(borrowRepository).save(borrow7);

    }

    @Test
    public void borrowBookByIdResourceBookshelfConflictExceptionTest() {
        Mockito.doReturn(Optional.of(notAvailableBook)).when(bookRepository).findById(notAvailableBook.getId());


        Exception exception = assertThrows(BookshelfConflictException.class, () ->
                service.borrowBookById(notAvailableBook.getId(),borrow1.getFirstname()
                        ,borrow1.getLastname(),borrow1.getBorrowed(),borrow1.getComment())
        );

        String expectedMessage = String.format("Book with id: %s is already borrowed.", borrow1.getId());
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void returnBookTest() {
        Mockito.doReturn(Optional.of(borrow6)).when(borrowRepository).findById(borrow6.getId());
        Mockito.doReturn(Optional.of(notAvailableBook2)).when(bookRepository).findById(id);
        service.returnBook(4);
        Mockito.verify(borrowRepository).save(borrow6);
    }

    @Test
    public void returnBookResourceNotFoundExceptionTest() {

        Exception exception = assertThrows(BookshelfResourceNotFoundException.class, () ->
                service.returnBook(id)
        );

        String expectedMessage =  String.format("Borrow with id= %s not found", id);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }



    @Test
    public void borrowBookByAuthorAndTitleSuccessTest() {
        Mockito.doReturn(availableBook3).when(bookRepository).findByAuthorAndTitle(author, title);

        service.borrowBookByAuthorAndTitle(author, title, firstname, surname, date1, null);

        Mockito.verify(borrowRepository).save(borrow3);
    }

    @Test
    public void borrowBookByAuthorAndTitleResourceNotFoundExceptionTest() {
        Mockito.doReturn(null).when(bookRepository).findByAuthorAndTitle(author, title);


        Exception exception = assertThrows(BookshelfResourceNotFoundException.class, () ->
                service.borrowBookByAuthorAndTitle(author, title, firstname, surname, date1, null)
        );

        String expectedMessage = String.format(
                "Book with author: %s and title: %s doesn't exist.", author, title
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void borrowBookByAuthorAndTitleConflictExceptionTest() {
        Mockito.doReturn(notAvailableBook).when(bookRepository).findByAuthorAndTitle(author, title);


        Exception exception = assertThrows(BookshelfConflictException.class, () ->
                service.borrowBookByAuthorAndTitle(author, title, firstname, surname, date1, null)
        );

        String expectedMessage = String.format(
                "Book with author: %s and title: %s is already borrowed.", author, title
        );
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void deleteBorrowSuccessTest() {
        Mockito.doReturn(Optional.of(borrow1)).when(borrowRepository).findById(id);

        service.deleteBorrow(id);

        Mockito.verify(borrowRepository).deleteById(id);
    }

    @Test
    public void deleteBorrowResourceNotFoundExceptionTest() {
        Mockito.doReturn(Optional.empty()).when(borrowRepository).findById(id);

        Exception exception = assertThrows(BookshelfResourceNotFoundException.class, () ->
                service.deleteBorrow(id)
        );

        String expectedMessage = String.format("Borrow with id=%d doesn't exist", id);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void deleteBorrowConflictExceptionTest() {
        Mockito.doReturn(Optional.of(borrow2)).when(borrowRepository).findById(id);

        Exception exception = assertThrows(BookshelfConflictException.class, () ->
                service.deleteBorrow(id)
        );

        String expectedMessage = String.format("Borrow with id=%d is still active. Can't delete", id);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void getUserBorrowHistoryTest() {
        Mockito.doReturn(borrowList).when(borrowRepository)
                .findAllByFirstnameAndLastname(firstname, surname);

        UserHistory testedList = service.getUserBorrowHistory(firstname, surname);

        assertThat(testedList).usingRecursiveComparison().isEqualTo(correctUserHistory);
    }
}