package com.globallogic.bookshelf.controller;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.service.BookShelfService;
import com.globallogic.bookshelf.utils.UserHistory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
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

    @InjectMocks
    private BookShelfController bookShelfController;


    private static MockMvc mockMvc;
    private static Book book, book2;
    private static String bookTitle;
    private static HashMap<Book, String> allBooksAvailableHashMap;
    private static HashMap<String, String> allBooksHashMap;
    private static String categoryName;

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
                .andExpect(content().string(String.format("Book with id=%d delete", book.getId())));
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
    void bookDeleteBookIsBorrowedTest() throws Exception {


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
    void getBookHistoryNullPointerExceptionTest() throws Exception {
        when(bookShelfService.getBooksHistory((String) org.mockito.Mockito.any()))
                .thenThrow(new NullPointerException());
        mockMvc
                .perform(get("/bookshelf/bookHistory/{name}", "Name"))
                .andExpect(status().isNotFound());
    }

}
