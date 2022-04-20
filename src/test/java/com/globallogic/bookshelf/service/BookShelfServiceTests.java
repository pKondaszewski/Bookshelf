package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import com.globallogic.bookshelf.entity.Category;
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
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookShelfServiceTests {

    private static Book bookAvailableTest, bookNotAvailableTest;
    private static Borrow borrowTest;
    private static HashMap<Book, String> booksAvailabilityTest;
    private static final int ID_TEST = 1;

    protected static ModelMapper model = new ModelMapper();


    @Mock
    private static BookRepository bookRepository;

    @Mock
    private static BorrowRepository borrowRepository;

    @Mock
    private static CategoryRepository categoryRepository;

    @InjectMocks
    private static BookShelfService service;

    private static List<Book> allBooksTest;

    @BeforeAll
    public static void setBook() {
        model.getConfiguration().setPropertyCondition(Conditions.isNotNull());

        service = new BookShelfService(bookRepository, borrowRepository, categoryRepository, model);
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

        booksAvailabilityTest = new HashMap<>();
        booksAvailabilityTest.put(bookAvailableTest, "available");
        booksAvailabilityTest.put(bookNotAvailableTest,
                String.format("%s %s : %s",
                        borrowTest.getFirstname(),
                        borrowTest.getSurname(),
                        borrowTest.getBorrowed()));
    }

    @Test
    public void testGetBooksAvailability() {
        ArrayList<Borrow> bookBorrows = new ArrayList<>();
        bookBorrows.add(borrowTest);

        Mockito.doReturn(allBooksTest).when(bookRepository).findAll();
        Mockito.doReturn(bookBorrows).when(borrowRepository).findBorrowsByBook(bookNotAvailableTest);

        HashMap<Book, String> booksAvailabilityReturn = service.getBooksAvailability();

        assertEquals(booksAvailabilityTest, booksAvailabilityReturn);
    }
}
