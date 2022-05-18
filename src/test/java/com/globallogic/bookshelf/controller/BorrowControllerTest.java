package com.globallogic.bookshelf.controller;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.BorrowRepository;
import com.globallogic.bookshelf.service.BorrowService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
//@WebMvcTest(controllers = BorrowController.class)
//@AutoConfigureMockMvc
public class BorrowControllerTest {

    @MockBean
    private BorrowRepository borrowRepository;

    @MockBean
    private BorrowService borrowService;

    @MockBean
    private BookRepository bookRepository;

    @InjectMocks
    private BorrowController borrowController;

    private static MockMvc mockMvc;

    private static Borrow borrow1, borrow2;
    private static Book book1, book2;
    private static int id;

    @BeforeEach
    public void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(borrowController).build();
    }

    @BeforeAll
    public static void initVariables() {
        book1 = new Book(1, "Author", "bookName", true, new Category(1, "test"));
        book2 = new Book(1, "a", "bookName", true, new Category(1, "test"));

       borrow1 = new Borrow(1,new Date(),null,"TestName","TestSurname","Test comment", book1);
       borrow2 = new Borrow(1,new Date(),new Date(),"TestName","TestSurname","Test comment", book2);
    }

    @Test
    public void borrowByIdSuccessTest() throws Exception {
        mockMvc
                .perform(post("/byAuthorAndTitle").param("author", "autor").param("title", "title"))
                .andDo(print())
                .andExpect(status().isCreated());
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
