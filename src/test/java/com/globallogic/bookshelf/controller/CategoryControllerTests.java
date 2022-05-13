package com.globallogic.bookshelf.controller;

import com.globallogic.bookshelf.repository.CategoryRepository;
import com.globallogic.bookshelf.service.CategoryService;
import org.hamcrest.Matchers;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
//@WebMvcTest(controllers = CategoryController.class)
//@AutoConfigureMockMvc
public class CategoryControllerTests {

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private static MockMvc mockMvc;

    private static String name, category1, category2;
    private static HashMap<String, Integer> booksPerCategoryMap;

    @BeforeEach
    public void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }

    @BeforeAll
    public static void setModel() {
        name = "name";

        category1 = "category1";
        category2 = "category2";
        booksPerCategoryMap = new HashMap<>();
        booksPerCategoryMap.put(category1, 5);
        booksPerCategoryMap.put(category2, 10);
    }

    @Test
    public void postRequestSuccessTest() throws Exception {
        mockMvc
                .perform(post("/categoryCreate").content(name).contentType(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(String.format("Category %s created successfully", name)));
    }


    @Test
    public void deleteRequestSuccessTest() throws Exception {
        mockMvc
                .perform(delete("/categoryDelete").content(name).contentType(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Category %s deleted successfully", name)));
    }

    @Test
    public void getAllRequestSuccessTest() throws Exception {
        Mockito.doReturn(booksPerCategoryMap).when(categoryService).getAmountOfBooksPerCategory();

        mockMvc
                .perform(get("/category/amountOfBooksPerCategory"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath(
                        String.format("$.%s", category1),
                        Matchers.is(booksPerCategoryMap.get(category1))))
                .andExpect(jsonPath(
                        String.format("$.%s", category2),
                        Matchers.is(booksPerCategoryMap.get(category2))));
    }
}
