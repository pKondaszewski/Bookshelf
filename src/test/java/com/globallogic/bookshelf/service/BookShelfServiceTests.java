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

    private static Book bookAvailable, bookNotAvailable;
    private static Borrow borrow;
    private static Borrow borrowTestNoCommentNoReturn;
    private static HashMap<Book, String> booksWithNewestBorrow;
    private static HashMap<Book, String> booksAvailability;
    private static HashMap<Book, String> booksAllAvailability;
    private static HashMap<String, String> books;
    private static final int ID_TEST = 1;
    private static final String name = "Default";
    private static Category Category;

    @Mock
    private static BookRepository bookRepository;

    @Mock
    private static BorrowRepository borrowRepository;

    @Mock
    private static CategoryRepository categoryRepository;

    @InjectMocks
    private static BookShelfService bookShelfService;


    private static List<Book> allBooks;
    private static ArrayList<Object> booksWithNewestBorrowSort;

    @BeforeAll
    public static void setBook() {

        bookShelfService = new BookShelfService(bookRepository, borrowRepository, categoryRepository);
        bookAvailable = new Book(
                1,
                "Adam Mickiewicz",
                "Dziady",
                true,
                new Category(1, "testCategoryName"));
        bookNotAvailable = new Book(
                2,
                "Adam Mickiewicz",
                "Dziady",
                false,
                new Category(2, "testCategoryName"));

        allBooks = new ArrayList<>();
        allBooks.add(bookAvailable);
        allBooks.add(bookNotAvailable);


        Date date = new Date();
        borrow = new Borrow(
                1,
                date,
                date,
                "Andrzej",
                "Kowalski",
                "random comment",
                bookNotAvailable);
        borrowTestNoCommentNoReturn = new Borrow(
                1,
                date,
                null,
                "Andrzej",
                "Kowalski",
                null,
                bookAvailable);

        books = new HashMap<>();
        books.put(bookAvailable.getAuthor(), bookAvailable.getTitle());
        books.put(bookNotAvailable.getAuthor(), bookNotAvailable.getTitle());

        booksWithNewestBorrowSort = new ArrayList<>();
        booksWithNewestBorrowSort.add(borrowTestNoCommentNoReturn);


        booksWithNewestBorrow = new HashMap<>();
        booksWithNewestBorrow.put(bookNotAvailable,
                String.format("Name : %s %s : Date of borrowing book %s",
                        borrow.getFirstname(),
                        borrow.getSurname(),
                        borrow.getBorrowed()));
        booksAllAvailability = new HashMap<>();
        booksAllAvailability.put(bookAvailable, "Available");
        booksAvailability = new HashMap<>();
        booksAvailability.put(bookAvailable, "available");
        booksAvailability.put(bookNotAvailable,
                String.format("%s %s : %s",
                        borrow.getFirstname(),
                        borrow.getSurname(),
                        borrow.getBorrowed()));


       Category = new Category(1, name);
    }

    @Test
    public void deleteBookSuccessTest() {
        Mockito.doReturn(Optional.of(bookAvailable)).when(bookRepository).findById(ID_TEST);
        Mockito.doReturn(List.of(borrow)).when(borrowRepository).findAllByBook(bookAvailable);

        bookShelfService.delete(ID_TEST);
        Mockito.verify(borrowRepository).delete(borrow);
        Mockito.verify(bookRepository).deleteById(ID_TEST);
    }

    @Test
    public void deleteBookResourceNotFoundExceptionTest() {
        Mockito.doReturn(Optional.empty()).when(bookRepository).findById(ID_TEST);

        Exception exception = assertThrows(BookshelfResourceNotFoundException.class, () ->
                bookShelfService.delete(ID_TEST)
        );

        String expectedMessage = String.format("Book with id=%d doesn't exist", ID_TEST);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void deleteBookConflictExceptionTest() {
        Mockito.doReturn(Optional.of(bookNotAvailable)).when(bookRepository).findById(ID_TEST);

        Exception exception = assertThrows(BookshelfConflictException.class, () ->
                bookShelfService.delete(ID_TEST)
        );

        String expectedMessage = String.format("Book with id=%d is still borrowed. Can't delete", ID_TEST);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getAllBooksTest() {
        Mockito.doReturn(allBooks).when(bookRepository).findAll();

        HashMap<String, String> booksAvailabilityReturn = bookShelfService.getAllBooks();

        assertEquals(books, booksAvailabilityReturn);
    }

    @Test
    public void getAllBooksAvailableTest() {
        ArrayList<Book> bookArrayList = new ArrayList<>();
        bookArrayList.add(bookAvailable);

        Mockito.doReturn(bookArrayList).when(bookRepository).findAll();
        HashMap<Book, String> booksAvailabilityReturn = bookShelfService.getAllBooksAvailable();

        assertEquals(booksAllAvailability, booksAvailabilityReturn);

    }

    @Test
    public void getListOfBorrowedBooksSortTest() {

        ArrayList<Borrow> bookBorrows = new ArrayList<>();
        bookBorrows.add(borrowTestNoCommentNoReturn);

        Mockito.doReturn(allBooks).when(bookRepository).findAll();
        Mockito.doReturn(bookBorrows).when(borrowRepository).findAllByBook(bookNotAvailable);

        List<Borrow> booksAvailabilityReturn = bookShelfService.getListOfBorrowedBooksSort();

        assertEquals(booksAvailabilityReturn, booksWithNewestBorrowSort);
    }

    @Test
    public void getListOfBorrowedBooksTest() {

        ArrayList<Borrow> bookBorrows = new ArrayList<>();
        bookBorrows.add(borrow);

        Mockito.doReturn(allBooks).when(bookRepository).findAll();
        Mockito.doReturn(bookBorrows).when(borrowRepository).findAllByBook(bookNotAvailable);

        HashMap<Book, String> booksAvailabilityReturn = bookShelfService.getListOfBorrowedBooks();

        assertEquals(booksAvailabilityReturn, booksWithNewestBorrow);
    }

    @Test
    public void getBooksAvailabilityTest() {
        ArrayList<Borrow> bookBorrows = new ArrayList<>();
        bookBorrows.add(borrow);

        Mockito.doReturn(allBooks).when(bookRepository).findAll();
        Mockito.doReturn(bookBorrows).when(borrowRepository).findAllByBook(bookNotAvailable);

        HashMap<Book, String> booksAvailabilityReturn = bookShelfService.getBooksAvailability();

        assertEquals(booksAvailability, booksAvailabilityReturn);
    }

    @Test
    public void getBookHistoryTest() {
        ArrayList<Borrow> bookBorrows = new ArrayList<>();
        bookBorrows.add(borrow);
        List<String> borrowInfo = new ArrayList<>();

        borrowInfo.add("Name: " + borrow.getFirstname() + " " + borrow.getSurname());
        borrowInfo.add("Date of borrowing book: " + borrow.getBorrowed().toString());
        borrowInfo.add("Date of return book: " + borrow.getReturned().toString());
        borrowInfo.add("Comment: " + borrow.getComment());
        borrowInfo.add("Book is available");

        HashMap<Book, List<String>> bookListHashMap = new HashMap<>();
        bookListHashMap.put(bookAvailable,borrowInfo);
        Mockito.doReturn(bookAvailable).when(bookRepository).findByTitle(bookAvailable.getTitle());
        Mockito.doReturn(bookBorrows).when(borrowRepository).findAllByBook(bookAvailable);

        HashMap<Book, List<String>> bookHistory = bookShelfService.getBooksHistory(bookAvailable.getTitle());

        assertEquals(bookListHashMap, bookHistory);
    }

    @Test
    public void getBookHistoryWithNoCommentAndNotReturnTest() {
        ArrayList<Borrow> bookBorrows = new ArrayList<>();
        bookBorrows.add(borrowTestNoCommentNoReturn);
        List<String> borrowInfo = new ArrayList<>();
        borrowInfo.add("Name: " + borrowTestNoCommentNoReturn.getFirstname() + " " + borrowTestNoCommentNoReturn.getSurname());
        borrowInfo.add("Date of borrowing book: " + borrowTestNoCommentNoReturn.getBorrowed().toString());
        borrowInfo.add("Book not returned");
        borrowInfo.add("No comment");
        borrowInfo.add("Book is not available");

        HashMap<Book, List<String>> bookListHashMap = new HashMap<>();
        bookListHashMap.put(bookNotAvailable, borrowInfo);
        Mockito.doReturn(bookNotAvailable).when(bookRepository).findByTitle(bookNotAvailable.getTitle());
        Mockito.doReturn(bookBorrows).when(borrowRepository).findAllByBook(bookNotAvailable);

        HashMap<Book, List<String>> bookHistory = bookShelfService.getBooksHistory(bookNotAvailable.getTitle());

        assertEquals(bookListHashMap, bookHistory);

    }

//    @Test
//    public void createSuccessTest(){
//        Mockito.doReturn(Category).when(categoryRepository).findByName(bookAvailable.getCategory().getName());
//
//        bookShelfService.create(bookAvailable);
//
//        Mockito.verify(bookRepository).save(bookAvailable);
//    }
//
//    @Test
//    public void createBookshelfResourceNotFoundExceptionTest() {
//        Exception exception = assertThrows(BookshelfResourceNotFoundException.class, () ->
//                bookShelfService.create(bookNotAvailable)
//        );
//
//        String expectedMessage = "Category not found";
//        String actualMessage = exception.getMessage();
//        assertTrue(actualMessage.contains(expectedMessage));
//    }
}

