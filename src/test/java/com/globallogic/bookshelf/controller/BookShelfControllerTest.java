package com.globallogic.bookshelf.controller;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.exeptions.LocalDateTimeException;
import com.globallogic.bookshelf.exeptions.ReservationConflictException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.service.BookShelfService;
import com.globallogic.bookshelf.service.ReservationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {BookShelfController.class})
@ExtendWith({SpringExtension.class, MockitoExtension.class})
class BookShelfControllerTest {

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private static BookShelfService bookShelfService;

    @MockBean
    private static ReservationService reservationService;

    @InjectMocks
    private BookShelfController bookShelfController;


    private static MockMvc mockMvc;
    private static Book book, book2;
    private static String bookTitle, firstname, lastname, comment;
    private static HashMap<Book, String> allBooksAvailableHashMap;
    private static HashMap<String, String> allBooksHashMap;
    private static String categoryName;
    private static Integer bookId;
     static LocalDate date;
    private static LocalTime time;

    @BeforeEach
    public void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookShelfController).build();
    }

    @BeforeAll
    public static void setModel() {
        String Available = "Available";
        bookTitle = "bookTitle";
        categoryName = "aaa";
        book = new Book(1, "Author2", bookTitle, true, new Category(1, "test"));
        book2 = new Book(1, "Author1", bookTitle, true, new Category(1, "test"));

        allBooksAvailableHashMap = new HashMap<>();
        allBooksAvailableHashMap.put(book, Available);

        allBooksHashMap = new HashMap<>();
        allBooksHashMap.put(book.getAuthor(), book.getTitle());
        allBooksHashMap.put(book2.getAuthor(), book2.getTitle());

        HashMap<String, String> test = new HashMap<>();

        bookId = 1;
        firstname = "Jan";
        lastname = "Kowalski";
        date = LocalDate.of(1970, 1, 1);
        time = LocalTime.of(12, 12);

    }


    @Test
    void createSuccessTest() throws Exception {

        mockMvc
                .perform(post("/bookshelf/bookCreate")
                        .param("author", book.getAuthor()).param("title", bookTitle)
                        .param("availability", String.valueOf(book.isAvailable())))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(String.format("Book %s created", bookTitle)));
    }

    @Test
    void bookDeleteSuccessTest() throws Exception {


        mockMvc
                .perform(delete("/bookshelf/{id}", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Book with id=%d deleted successfully", book.getId())));
    }

    @Test
    void bookDeleteBookNotExistTest() throws Exception {


        doThrow(new BookshelfResourceNotFoundException(String.format("Book with id=%d doesn't exist", book.getId()))).when(bookShelfService)
                .delete(book.getId());

        mockMvc
                .perform(delete("/bookshelf/{id}", "1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format("Book with id=%d doesn't exist", book.getId())));
    }

    @Test
    void deleteBookIsBorrowedTest() throws Exception {


        doThrow(new BookshelfConflictException(
                String.format("Book with id=%d is still borrowed. Can't delete", book.getId()))).when(bookShelfService)
                .delete(book.getId());

        mockMvc
                .perform(delete("/bookshelf/{id}", "1"))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().string(String.format("Book with id=%d is still borrowed. Can't delete", book.getId())));
    }

    @Test
    void getAllBooksAvailableSuccessTest() throws Exception {
        when(bookShelfService.getAllBooksAvailable()).thenReturn(allBooksAvailableHashMap);

        mockMvc
                .perform(get("/bookshelf/getAllBooksAvailable"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{\"" +
                        "Book(id=1, author=Author2, title=bookTitle, available=true, " +
                        "category=Category(id=1, name=test))\":\"Available\"}"));
    }


    @Test
    public void getAllBooksSuccessTest() throws Exception {


        when(bookShelfService.getAllBooks()).thenReturn(allBooksHashMap);

        mockMvc
                .perform(get("/bookshelf/listOfBooks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string("{\"Author2\":\"bookTitle\",\"Author1\":\"bookTitle\"}"));
    }

    @Test
    public void getAllBooksSuccess2Test() throws Exception {

        when(bookShelfService.getAllBooks()).thenReturn(new HashMap<>());

        mockMvc
                .perform(get("/bookshelf/listOfBooks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string("{}"));
    }


    @Test
    void getBookHistoryTest() throws Exception {
        when(bookShelfService.getBooksHistory(any())).thenReturn(new HashMap<>());

        mockMvc
                .perform(get("/bookshelf/bookHistory/{name}", "Name"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string("{}"));
    }


    @Test
    void getBookHistoryResourceNotFoundExceptionTest() throws Exception {
        when(bookShelfService.getBooksHistory((String) org.mockito.Mockito.any()))
                .thenThrow(new NullPointerException());
        mockMvc
                .perform(get("/bookshelf/bookHistory/{name}", "Name"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void putReservationSuccessTest() throws Exception {
        mockMvc
                .perform(put("/bookshelf/reservation")
                        .param("bookId", String.valueOf(bookId))
                        .param("date", String.valueOf(date))
                        .param("time", String.valueOf(time))
                        .param("firstname", firstname)
                        .param("lastname", lastname)
                        .param("comment", comment))
                .andExpect(status().isOk())
                .andExpect(content().string( String.format("Reservation created successfully. " +
                                "Book with id: %d is reserved by %s %s. " +
                                "Date of the reservation: %s. Time of the reservation: %s",
                        bookId, firstname, lastname, date, time)));
    }

    @Test
    public void putReservationBookshelfResourceNotFoundExceptionTest() throws Exception {
        doThrow(new BookshelfResourceNotFoundException(String.format("Book with id = %d not found.", bookId)))
                .when(reservationService).reservation(bookId, date, time, firstname, lastname, comment);


        mockMvc
                .perform(put("/bookshelf/reservation")
                        .param("bookId", String.valueOf(bookId))
                        .param("date", String.valueOf(date))
                        .param("time", String.valueOf(time))
                        .param("firstname", firstname)
                        .param("lastname", lastname)
                        .param("comment", comment))
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format("Book with id = %d not found.", bookId)));
    }

    @Test
    public void putReservationReservationConflictExceptionTest() throws Exception {
        String exceptionMessage = String.format("Book with id = %d is already reserved.", bookId);
        doThrow(new ReservationConflictException(exceptionMessage))
                .when(reservationService).reservation(bookId, date, time, firstname, lastname, comment);


        mockMvc
                .perform(put("/bookshelf/reservation")
                        .param("bookId", String.valueOf(bookId))
                        .param("date", String.valueOf(date))
                        .param("time", String.valueOf(time))
                        .param("firstname", firstname)
                        .param("lastname", lastname)
                        .param("comment", comment))
                .andExpect(status().isConflict())
                .andExpect(content().string(exceptionMessage));
    }

    @Test
    public void putReservationLocalDateTimeExceptionTest() throws Exception {
        doThrow(new LocalDateTimeException(String.format("Given date: %s and time: %s of the reservation is before actual date: %s and time: %s",
                date, time,
                LocalDateTime.now().toLocalDate(),
                LocalDateTime.now().toLocalTime().truncatedTo(ChronoUnit.MINUTES))))
                .when(reservationService).reservation(bookId, date, time, firstname, lastname, comment);


        mockMvc
                .perform(put("/bookshelf/reservation")
                        .param("bookId", String.valueOf(bookId))
                        .param("date", String.valueOf(date))
                        .param("time", String.valueOf(time))
                        .param("firstname", firstname)
                        .param("lastname", lastname)
                        .param("comment", comment))
                .andExpect(status().isConflict());
    }

    @Test
    public void putReservationBookshelfConflictExceptionTest() throws Exception {
        String exceptionMessage = String.format("Book with id = %d is not available.", bookId);
        doThrow(new BookshelfConflictException(exceptionMessage))
                .when(reservationService).reservation(bookId, date, time, firstname, lastname, comment);


        mockMvc
                .perform(put("/bookshelf/reservation")
                        .param("bookId", String.valueOf(bookId))
                        .param("date", String.valueOf(date))
                        .param("time", String.valueOf(time))
                        .param("firstname", firstname)
                        .param("lastname", lastname)
                        .param("comment", comment))
                .andExpect(status().isConflict())
                .andExpect(content().string(exceptionMessage));
    }
}
