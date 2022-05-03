package com.globallogic.bookshelf.controller;


import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.service.BookShelfService;
import com.google.gson.Gson;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.print.attribute.standard.Media;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@WebMvcTest(controllers = BookShelfController.class)
@AutoConfigureMockMvc
public class BookShelfControllerTests {

    private static String mapCategoryTestName1, Ave;
    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private BookShelfService bookShelfService;

    @InjectMocks
    private BookShelfController bookShelfController;

    @Autowired
    private MockMvc mockMvc;


    private static Book testBook, testBook2;
    private static String bookName;
    private static HashMap<Book, String> allBooksAvailableHashMap;
    private static HashMap<String, String> allBooksHashMap;


    @BeforeAll
    public static void setModel() {
        Ave = "Available";
        bookName = "test";

        testBook = new Book(1, "Author", bookName, true, new Category(1, "test"));
        testBook2 = new Book(1, "a", bookName, true, new Category(1, "test"));

        allBooksAvailableHashMap = new HashMap<>();
        allBooksAvailableHashMap.put(testBook, Ave);
        allBooksAvailableHashMap.put(testBook2, "Not Available");

        allBooksHashMap = new HashMap<>();
        allBooksHashMap.put(testBook.getAuthor(), testBook.getName());


    }


    @Test
    public void testCreateBookPost() throws Exception {
        Gson gson = new Gson();
        String json = gson.toJson(testBook);

        mockMvc
                .perform(post("/bookshelf").content(json).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(String.format("Book %s created successfully", testBook.getName())));
    }

    @Test
    public void testDeleteBookRequestSuccess() throws Exception {
        Mockito.doReturn(testBook).when(bookRepository).findByName(bookName);
        mockMvc
                .perform(delete("/bookshelf/{id}", testBook.getId().intValue()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Book with id=%d delete", testBook.getId())));
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

        Gson gson = new Gson();
        String json = gson.toJson(allBooksHashMap);
        mockMvc
                .perform(get("/bookshelf/listOfBooks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(json));

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