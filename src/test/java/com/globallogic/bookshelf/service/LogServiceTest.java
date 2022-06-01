package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Log;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.LogRepository;
import com.globallogic.bookshelf.utils.StringRepresentation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {LogService.class})
@ExtendWith(MockitoExtension.class)
public class LogServiceTest {
    @Mock
    private static BookRepository bookRepository;

    @Mock
    private static LogRepository logRepository;

    @InjectMocks
    private static LogService logService;

    private static ArrayList<Book> allAvailableBooks;
    private static String createLogSuccessOutput;
    private static Instant instant;

    @BeforeAll
    public static void initVariables() {
        String title1 = "title1";
        String title2 = "title2";

        Book book1 = new Book(1, "author1", title1, true, null);
        Book book2 = new Book(2, "author2", title2, true, null);
        allAvailableBooks = new ArrayList<>();
        allAvailableBooks.add(book1);
        allAvailableBooks.add(book2);

        ArrayList<String> titlesOfAvailableBooks = new ArrayList<>();
        titlesOfAvailableBooks.add(title1);
        titlesOfAvailableBooks.add(title2);

        instant = Instant.now();
        String timeInUTC = instant.atZone(ZoneOffset.UTC).toString();

        createLogSuccessOutput = StringRepresentation.ofTheLog(timeInUTC,
                allAvailableBooks.size(), 0, titlesOfAvailableBooks);
    }

    @Test
    public void createLogSuccessTest() {
        MockedStatic<Instant> mockedStatic = mockStatic(Instant.class);
        mockedStatic.when(Instant::now).thenReturn(instant);
        Mockito.doReturn(allAvailableBooks).when(bookRepository).findAllByAvailable(true);
        Mockito.doReturn(new ArrayList<>()).when(bookRepository).findAllByAvailable(false);
        String result = logService.createLog();

        assertEquals(result, createLogSuccessOutput);
    }
}
