package com.globallogic.bookshelf.controller;


import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.service.BookShelfService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class BookShelfControllerTests {

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private BookShelfService bookShelfService;

    @InjectMocks
    private static BookShelfController bookShelfController;

    private static MockMvc mockMvc;

    private static Book book1;
    private static String bookName;
    private static HashMap<Book, String> allBooksAvailableHashMap;
    private static HashMap<String, String> allBooksHashMap;

    @BeforeEach
    public void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookShelfController).build();
    }

    @BeforeAll
    public static void setModel() {
        String available = "Available";
        bookName = "bookname";

        book1 = new Book(1, "Author", bookName, true, new Category(1, "categoryName"));
        Book book2 = new Book(1, "a", bookName, true, new Category(1, "categoryName"));

        allBooksAvailableHashMap = new HashMap<>();
        allBooksAvailableHashMap.put(book1, available);
        allBooksAvailableHashMap.put(book2, "Not Available");

        allBooksHashMap = new HashMap<>();
        allBooksHashMap.put(book1.getAuthor(), book1.getTitle());


    }


    @Test
    public void createBookTest() throws Exception {
        Gson gson = new Gson();
        String json = gson.toJson(book1);

        mockMvc
                .perform(post("/bookshelf").content(json).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(String.format("Book %s created successfully", book1.getTitle())));
    }

    @Test
    public void testDeleteBookRequestSuccess() throws Exception {
        Mockito.doReturn(book1).when(bookRepository).findByTitle(bookName);
        mockMvc
                .perform(delete("/bookshelf/{id}", book1.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Book with id=%d delete", book1.getId())));
    }

    @Test
    public void testGetListOfBooksAvailableSuccess() throws Exception {
        Mockito.doReturn(allBooksAvailableHashMap).when(bookShelfService).getAllBooksAvailable();
        Gson gson = new Gson();
        String json = gson.toJson(allBooksAvailableHashMap);

        mockMvc
                .perform(get("/bookshelf/booksPerCategoryMap"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(json));
    }

    @Test
    public void testGetListOfBooksSuccess() throws Exception {
        Mockito.doReturn(allBooksHashMap).when(bookShelfService).getAllBooks();
//
//        Gson gson = new Gson();
//        String json = gson.toJson(allBooksHashMap);

        mockMvc
                .perform(get("/bookshelf/listOfBooks"))
                .andDo(print())
                .andExpect(status().isOk());

//        RestAssuredMockMvc.standaloneSetup(new BookShelfController());
//        RestAssuredMockMvc.given()
//                .when()
//                .get("/bookshelf/listOfBooks")
//                .then()
//                .statusCode(200);
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(content().json(json));
    }

    @Test
    public void testGetListOfBooksAvailabilitySuccess() throws Exception {
        Mockito.doReturn(allBooksHashMap).when(bookShelfService).getBooksAvailability();

        Gson gson = new Gson();
        String json = gson.toJson(allBooksHashMap);
        mockMvc
                .perform(get("/bookshelf/booksAvailability"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

}