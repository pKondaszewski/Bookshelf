package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.BorrowRepository;
import com.globallogic.bookshelf.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookShelfServiceTests {

    private static Book bookNotAvailableTest;
    private static Borrow borrowTest;
    private static Book bookAvailableTest, getBookNotAvailableNoCommentNotReturnedTest;
    private static Borrow borrowTestNoCommentNoReturn;
    private static HashMap<Book, String> booksWithNewestBorrow;
    private static HashMap<Book, String> booksAvailabilityTest;
    private static HashMap<Book, String> booksAllAvailabilityTest;
    private static HashMap<String, String> books;
    private static final int ID_TEST = 1;
    private static String name = "test";
    private static Category testCategory;

    @Mock
    private static BookRepository bookRepository;

    @Mock
    private static BorrowRepository borrowRepository;

    @Mock
    private static CategoryRepository categoryRepository;

    @InjectMocks
    private static BookShelfService bookShelfService;


    private static List<Book> allBooksTest;

    @BeforeAll
    public static void setBook() {

        bookShelfService = new BookShelfService(bookRepository, borrowRepository, categoryRepository);
        bookAvailableTest = new Book(
                1,
                "Adam Mickiewicz",
                "Dziady",
                true,
                new Category(1, "testCategoryName"));
        bookNotAvailableTest = new Book(
                2,
                "Adam Mickiewicz",
                "Dziady",
                false,
                new Category(2, "testCategoryName"));

        allBooksTest = new ArrayList<>();
        allBooksTest.add(bookAvailableTest);
        allBooksTest.add(bookNotAvailableTest);


        Date date = new Date();
        borrowTest = new Borrow(
                1,
                date,
                date,
                "Andrzej",
                "Kowalski",
                "random comment",
                bookNotAvailableTest);
        borrowTestNoCommentNoReturn = new Borrow(
                1,
                date,
                null,
                "Andrzej",
                "Kowalski",
                null,
                bookAvailableTest);

        books = new HashMap<>();
        books.put(bookAvailableTest.getAuthor(), bookAvailableTest.getName());
        books.put(bookNotAvailableTest.getAuthor(), bookNotAvailableTest.getName());

        booksWithNewestBorrow = new HashMap<>();
        booksWithNewestBorrow.put(bookNotAvailableTest,
                String.format("Name : %s %s : Date of borrowing book %s",
                        borrowTest.getFirstname(),
                        borrowTest.getSurname(),
                        borrowTest.getBorrowed()));
        booksAllAvailabilityTest = new HashMap<>();
        booksAllAvailabilityTest.put(bookAvailableTest, "Available");
        booksAvailabilityTest = new HashMap<>();
        booksAvailabilityTest.put(bookAvailableTest, "available");
        booksAvailabilityTest.put(bookNotAvailableTest,
                String.format("%s %s : %s",
                        borrowTest.getFirstname(),
                        borrowTest.getSurname(),
                        borrowTest.getBorrowed()));

        testCategory = new Category(null, name);
    }

    @Test
    public void testDeleteBookSuccess() {
        Mockito.doReturn(Optional.of(bookAvailableTest)).when(bookRepository).findById(ID_TEST);
        Mockito.doReturn(List.of(borrowTest)).when(borrowRepository).findBorrowsByBook(bookAvailableTest);

        bookShelfService.delete(ID_TEST);
        Mockito.verify(borrowRepository).delete(borrowTest);
        Mockito.verify(bookRepository).deleteById(ID_TEST);
    }

    @Test
    public void testDeleteBookResourceNotFoundException() {
        Mockito.doReturn(Optional.empty()).when(bookRepository).findById(ID_TEST);

        Exception exception = assertThrows(BookshelfResourceNotFoundException.class, () ->
                bookShelfService.delete(ID_TEST)
        );

        String expectedMessage = String.format("Book with id=%d doesn't exist", ID_TEST);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    public void testDeleteBookConflictException() {
        Mockito.doReturn(Optional.of(bookNotAvailableTest)).when(bookRepository).findById(ID_TEST);

        Exception exception = assertThrows(BookshelfConflictException.class, () ->
                bookShelfService.delete(ID_TEST)
        );

        String expectedMessage = String.format("Book with id=%d is still borrowed. Can't delete", ID_TEST);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testGetAllBooks() {


        Mockito.doReturn(allBooksTest).when(bookRepository).findAll();

        HashMap<String, String> booksAvailabilityReturn = bookShelfService.getAllBooks();

        assertEquals(books, booksAvailabilityReturn);
    }

    @Test
    public void testGetAllBooksAvailable() {
        ArrayList<Book> bookArrayList = new ArrayList<>();
        bookArrayList.add(bookAvailableTest);

        Mockito.doReturn(bookArrayList).when(bookRepository).findAll();
        HashMap<Book, String> booksAvailabilityReturn = bookShelfService.getAllBooksAvailable();

        assertEquals(booksAllAvailabilityTest, booksAvailabilityReturn);

    }

    @Test
    public void testGetListOfBorrowedBooksSort() {

        ArrayList<Borrow> bookBorrows = new ArrayList<>();
        bookBorrows.add(borrowTest);

        Mockito.doReturn(allBooksTest).when(bookRepository).findAll();
        Mockito.doReturn(bookBorrows).when(borrowRepository).findBorrowsByBook(bookNotAvailableTest);

        HashMap<Book, String> booksAvailabilityReturn = bookShelfService.getListOfBorrowedBooksSort();

        assertEquals(booksAvailabilityReturn, booksWithNewestBorrow);
    }

    @Test
    public void testGetBooksAvailability() {
        ArrayList<Borrow> bookBorrows = new ArrayList<>();
        bookBorrows.add(borrowTest);

        Mockito.doReturn(allBooksTest).when(bookRepository).findAll();
        Mockito.doReturn(bookBorrows).when(borrowRepository).findBorrowsByBook(bookNotAvailableTest);

        HashMap<Book, String> booksAvailabilityReturn = bookShelfService.getBooksAvailability();

        assertEquals(booksAvailabilityTest, booksAvailabilityReturn);
    }

    @Test
    public void testGetBookHistory() {
        ArrayList<Borrow> bookBorrows = new ArrayList<>();
        bookBorrows.add(borrowTest);
        List<String> borrowInfo = new ArrayList<>();

        borrowInfo.add("Name: " + borrowTest.getFirstname() + " " + borrowTest.getSurname());
        borrowInfo.add("Date of borrowing book: " + borrowTest.getBorrowed().toString());
        borrowInfo.add("Date of return book: " + borrowTest.getReturned().toString());
        borrowInfo.add("Comment: " + borrowTest.getComment());
        borrowInfo.add("Book is available");

        HashMap<Book, List<String>> bookListHashMap = new HashMap<>();
        bookListHashMap.put(bookAvailableTest, borrowInfo);
        Mockito.doReturn(bookAvailableTest).when(bookRepository).findByName(bookAvailableTest.getName());
        Mockito.doReturn(bookBorrows).when(borrowRepository).findBorrowsByBook(bookAvailableTest);


        HashMap<Book, List<String>> bookHistory = bookShelfService.getBooksHistory(bookAvailableTest.getName());

        assertEquals(bookListHashMap, bookHistory);

    }

    @Test
    public void testGetBookHistoryWithNoCommentAndNotReturn() {
        ArrayList<Borrow> bookBorrows = new ArrayList<>();
        bookBorrows.add(borrowTestNoCommentNoReturn);
        List<String> borrowInfo = new ArrayList<>();
        borrowInfo.add("Name: " + borrowTestNoCommentNoReturn.getFirstname() + " " + borrowTestNoCommentNoReturn.getSurname());
        borrowInfo.add("Date of borrowing book: " + borrowTestNoCommentNoReturn.getBorrowed().toString());
        borrowInfo.add("Book not returned");
        borrowInfo.add("No comment");
        borrowInfo.add("Book is not available");

        HashMap<Book, List<String>> bookListHashMap = new HashMap<>();
        bookListHashMap.put(bookNotAvailableTest, borrowInfo);
        Mockito.doReturn(bookNotAvailableTest).when(bookRepository).findByName(bookNotAvailableTest.getName());
        Mockito.doReturn(bookBorrows).when(borrowRepository).findBorrowsByBook(bookNotAvailableTest);


        HashMap<Book, List<String>> bookHistory = bookShelfService.getBooksHistory(bookNotAvailableTest.getName());

        assertEquals(bookListHashMap, bookHistory);

    }

    @Test
    public void testCreateSuccess(){
        Mockito.doReturn(testCategory).when(categoryRepository).findByName(bookAvailableTest.getCategory().getName());

        bookShelfService.create(bookAvailableTest);

        Mockito.verify(bookRepository).save(bookAvailableTest);
    }

    @Test
    public void testCreateBookshelfResourceNotFoundException() {

        Exception exception = assertThrows(BookshelfResourceNotFoundException.class, () ->
                bookShelfService.create(bookNotAvailableTest)
        );

        String expectedMessage = "Category not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}

