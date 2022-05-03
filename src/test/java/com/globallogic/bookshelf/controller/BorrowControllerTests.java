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
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.time.LocalDate;
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

    @Autowired
    private MockMvc mockMvc;


    private static String name, mapCategoryTestName1, mapCategoryTestName2;
    private static HashMap<String, Integer> booksPerCategoryMap;


    @BeforeAll
    public static void setModel() {
        name = "1";

        mapCategoryTestName1 = "TestCategory1";
        mapCategoryTestName2 = "TestCategory2";
        booksPerCategoryMap = new HashMap<>();
        booksPerCategoryMap.put(mapCategoryTestName1, 5);
        booksPerCategoryMap.put(mapCategoryTestName2, 10);
    }



}
