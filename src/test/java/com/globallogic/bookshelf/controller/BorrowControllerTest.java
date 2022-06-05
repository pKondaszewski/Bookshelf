package com.globallogic.bookshelf.controller;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.BorrowRepository;
import com.globallogic.bookshelf.service.BorrowService;
import com.globallogic.bookshelf.utils.UserHistory;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ContextConfiguration(classes = {BorrowController.class})
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class BorrowControllerTest {


    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private BorrowRepository borrowRepository;

    @MockBean
    private static BorrowService borrowService;

    @MockBean
    private static ShelfUserController shelfUserController;

    @InjectMocks
    private BorrowController borrowController;

    private static MockMvc mockMvc;
    private static String BookId, borrowId;
    private static String FirstName, LastName, BookAuthor, BookTitle;
    private static Date date = new Date();


    @BeforeEach
    public void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(borrowController).build();
    }

    @BeforeAll
    public static void initVariables() {
        borrowId = "1";
        BookId = "1";
        FirstName = "Adam";
        LastName = "Testowy";
        BookAuthor = "Test";
        BookTitle = "Test";

    }

    @Test
    void borrowBookByIdTest() throws Exception {
        mockMvc
                .perform(post("/borrow/byId").param("BookId", BookId)
                        .param("FirstName", FirstName).param("LastName", LastName))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("%s %s borrow book with id: %s ", BookId, FirstName, LastName)));
    }

    @Test
    void BorrowBookByIdBookNotExistTest() throws Exception {
        doThrow(new BookshelfResourceNotFoundException(String.format("Book with id: %s doesn't exist.",
                String.valueOf(1)))).when(borrowService)
                .borrowBookById(any(), any(), any(), any(), any());

        mockMvc
                .perform(post("/borrow/byId").param("BookId", BookId)
                        .param("FirstName", FirstName).param("LastName", LastName))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book with id: 1 doesn't exist."));
    }

    @Test
    void borrowBookByIdBookIsBorrowedTest() throws Exception {
        doThrow(new BookshelfConflictException(String.format("Book with id: %s is borrowed.",
                String.valueOf(1)))).when(borrowService)
                .borrowBookById(any(), any(), any(), any(), any());

        mockMvc
                .perform(post("/borrow/byId").param("BookId", BookId)
                        .param("FirstName", FirstName).param("LastName", LastName))
                .andExpect(status().isConflict())
                .andExpect(content().string("Book with id: 1 is borrowed."));
    }


    @Test
    void borrowBookByAuthorAndTitleTest() throws Exception {
        mockMvc
                .perform(post("/borrow/byAuthorAndTitle").param("BookAuthor", BookAuthor).param("BookTitle", BookTitle)
                        .param("FirstName", FirstName).param("LastName", LastName))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("User: %s %s borrows book with author: %s and title: %s",
                        FirstName, LastName, BookAuthor, BookTitle)));
    }

    @Test
    void borrowBookByAuthorAndTitleBookNotExistTest() throws Exception {
        doThrow(new BookshelfResourceNotFoundException(String.format("Book with author: %s and title: %s doesn't exist."
                , FirstName, LastName)))
                .when(borrowService)
                .borrowBookByAuthorAndTitle(any(), any(), any(), any(), any(), any());

        mockMvc
                .perform(post("/borrow/byAuthorAndTitle").param("BookAuthor", BookAuthor).param("BookTitle", BookTitle)
                        .param("FirstName", FirstName).param("LastName", LastName))
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format(
                        "Book with author: %s and title: %s doesn't exist.", FirstName, LastName)));
    }

    @Test
    void borrowBookByAuthorAndTitleIsBorrowTest() throws Exception {
        doThrow(new BookshelfConflictException(String.format("Book with author: %s and title: %s is already borrowed."
                , FirstName, LastName)))
                .when(borrowService)
                .borrowBookByAuthorAndTitle(any(), any(), any(), any(), any(), any());

        mockMvc
                .perform(post("/borrow/byAuthorAndTitle").param("BookAuthor", BookAuthor).param("BookTitle", BookTitle)
                        .param("FirstName", FirstName).param("LastName", LastName))
                .andExpect(status().isConflict())
                .andExpect(content().string(String.format("Book with author: %s and title: %s is already borrowed.",
                        FirstName, LastName)));
    }


    @Test
    public void returnBookSuccessTest() throws Exception {

        mockMvc
                .perform(put("/borrow/bookReturn").param("borrowId", borrowId))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Borrow with id= %s ended", borrowId)));

    }

    @Test
    public void returnBookBorrowNotFoundTest() throws Exception {

        doThrow(new BookshelfResourceNotFoundException(String.format("Borrow with id= %s not found", borrowId)))
                .when(borrowService)
                .returnBook(any());

        mockMvc
                .perform(put("/borrow/bookReturn").param("borrowId", borrowId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format("Borrow with id= %s not found", borrowId)));

    }

    @Test
    public void returnBookBorrowIsEndedTest() throws Exception {

        doThrow(new BookshelfConflictException(String.format("Borrow with id: %s is ended.", borrowId)))
                .when(borrowService)
                .returnBook(any());

        mockMvc
                .perform(put("/borrow/bookReturn").param("borrowId", borrowId))
                .andExpect(status().isConflict())
                .andExpect(content().string(String.format("Borrow with id: %s is ended.", borrowId)));

    }

    @Test
    public void deleteBorrowSuccessTest() throws Exception {

        mockMvc
                .perform(delete("/borrow/borrowDelete").param("borrowId", borrowId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Borrow id= %s deleted successfully", borrowId)));
    }

    @Test
    public void deleteBorrowBorrowNotExistTest() throws Exception {

        doThrow(new BookshelfResourceNotFoundException(String.format("Borrow with id= %s doesn't exist", borrowId)))
                .when(borrowService)
                .deleteBorrow(any());

        mockMvc
                .perform(delete("/borrow/borrowDelete").param("borrowId", borrowId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format("Borrow with id= %s doesn't exist", borrowId)));
    }

    @Test
    public void deleteBorrowBorrowIsActiveTest() throws Exception {

        doThrow(new BookshelfConflictException(String.format("Borrow with id= %s is still active. Can't delete", borrowId)))
                .when(borrowService)
                .deleteBorrow(any());

        mockMvc
                .perform(delete("/borrow/borrowDelete").param("borrowId", borrowId))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().string(String.format("Borrow with id= %s is still active. Can't delete", borrowId)));
    }


    @Test
    void GetUserBorrowHistorySuccessTest() throws Exception {
        ArrayList<String> returnedBooks = new ArrayList<>();
        ArrayList<String> currentlyBorrowedBooks = new ArrayList<>();

        returnedBooks.add(String.valueOf(new Book(1,"d","d",true,new Category(1,"test"))));
        currentlyBorrowedBooks.add(String.valueOf(new Book(1,"d","d",false,new Category(1,"test"))));

        when(borrowService.getUserBorrowHistory(any(),any()))
                .thenReturn(new UserHistory(returnedBooks, currentlyBorrowedBooks, 10));

       mockMvc
                .perform(get("/borrow/userHistory").param("firstName", "Arek").param("lastName", "Kot"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content()
                        .string("{\"returnedBooks\":" +
                                "[\"Book(id=1, author=d, title=d, available=true, category=Category(id=1, name=test))\"]," +
                                "\"currentlyBorrowedBooks\":[\"Book(id=1, author=d, title=d, available=false, category=Category(id=1, name=test))\"],\"numberOfCurrentlyBorrowedBooks\":10}"));
    }




}
