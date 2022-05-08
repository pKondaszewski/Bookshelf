package com.globallogic.bookshelf.controller;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.BorrowRepository;
import com.globallogic.bookshelf.repository.CategoryRepository;
import com.globallogic.bookshelf.service.BorrowService;
import com.globallogic.bookshelf.service.BorrowServiceTests;
import com.globallogic.bookshelf.service.CategoryService;
import com.google.gson.Gson;
import org.hamcrest.Matcher;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@WebMvcTest(controllers = BorrowController.class)
@AutoConfigureMockMvc
public class BorrowControllerTests {

    private static int id;
    @MockBean
    private BorrowRepository borrowRepository;

    @MockBean
    private BorrowService borrowService;

    @MockBean
    private BookRepository bookRepository;

    @InjectMocks
    private BorrowController borrowController;

    @Autowired
    private MockMvc mockMvc;


    private static Borrow testBorrow1,testBorrow2;
    private static Book testBook1,testBook2;


    @BeforeAll
    public static void setModel() {
        testBook1 = new Book(1, "Author", "bookName", true, new Category(1, "test"));
        testBook2 = new Book(1, "a", "bookName", true, new Category(1, "test"));


       testBorrow1 = new Borrow(1,new Date(),null,"TestName","TestSurname","Test comment",testBook1);
       testBorrow2 = new Borrow(1,new Date(),new Date(),"TestName","TestSurname","Test comment",testBook2);
    }

//    @Test
//    public void testBorrowByIdSuccess() throws Exception {
//        Gson gson = new Gson();
//        String json = gson.toJson(testBorrow1);
//        mockMvc
//                .perform(post("/borrow/byId").contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(String.valueOf((json))))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }

}
