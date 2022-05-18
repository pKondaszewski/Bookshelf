package com.globallogic.bookshelf.controller;


import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.service.BookShelfService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ContextConfiguration(classes = {BookShelfController.class})
@ExtendWith(SpringExtension.class)
class BookShelfControllerTest {
    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private BookShelfController bookShelfController;

    @MockBean
    private BookShelfService bookShelfService;


    private static Book testBook, testBook2;
    private static String bookTitle;
    private static HashMap<Book, String> allBooksAvailableHashMap;
    private static HashMap<String, String> allBooksHashMap;


    @BeforeAll
    public static void setModel() {
        String Available = "Available";
        bookTitle = "test";

        testBook = new Book(1, "Author", bookTitle, true, new Category(1, "test"));
        testBook2 = new Book(1, "a", bookTitle, true, new Category(1, "test"));

        allBooksAvailableHashMap = new HashMap<>();
        allBooksAvailableHashMap.put(testBook, Available);
        allBooksAvailableHashMap.put(testBook2, "Not Available");

        allBooksHashMap = new HashMap<>();
        allBooksHashMap.put(testBook.getAuthor(), testBook.getTitle());


    }


   @Test
    void createTest() throws Exception{
       MockHttpServletRequestBuilder paramRequest = MockMvcRequestBuilders.post("/bookshelf/bookCreate")
               .param("author",testBook.getAuthor());
       MockHttpServletRequestBuilder requestBuilder = paramRequest.param("availability", String.valueOf(true))
               .param("title",bookTitle);
       ResultActions resultActions = MockMvcBuilders.standaloneSetup(this.bookShelfController)
               .build()
               .perform(requestBuilder);
       resultActions.andExpect(MockMvcResultMatchers.status().isCreated())
               .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
               .andExpect(MockMvcResultMatchers.content().string("Book test created"));
   }

}
