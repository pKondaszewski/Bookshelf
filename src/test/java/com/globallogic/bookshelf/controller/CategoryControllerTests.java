package com.globallogic.bookshelf.controller;

import com.globallogic.bookshelf.controller.CategoryController;
import com.globallogic.bookshelf.repository.CategoryRepository;
import com.globallogic.bookshelf.service.CategoryService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@WebMvcTest(controllers = CategoryController.class)
@AutoConfigureMockMvc
public class CategoryControllerTests {

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private MockMvc mockMvc;

    private static String name, mapCategoryTestName1, mapCategoryTestName2;
    private static HashMap<String, Integer> booksPerCategoryMap;

    @BeforeAll
    public static void setModel() {
        name = "testName";

        mapCategoryTestName1 = "TestCategory1";
        mapCategoryTestName2 = "TestCategory2";
        booksPerCategoryMap = new HashMap<>();
        booksPerCategoryMap.put(mapCategoryTestName1, 5);
        booksPerCategoryMap.put(mapCategoryTestName2, 10);
    }

    @Test
    public void testPostRequestSuccess() throws Exception {
        mockMvc
                .perform(post("/category").content(name).contentType(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(String.format("Category %s created successfully", name)));
    }

    @Test
    public void testDeleteRequestSuccess() throws Exception {
        mockMvc
                .perform(delete("/category").content(name).contentType(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Category %s deleted successfully", name)));
    }

    @Test
    public void testGetAllRequestSuccess() throws Exception {
        Mockito.doReturn(booksPerCategoryMap).when(categoryService).getAmountOfBooksPerCategory();

        mockMvc
                .perform(get("/category/amountOfBooksPerCategory"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath(
                        String.format("$.%s",mapCategoryTestName1),
                        Matchers.is(booksPerCategoryMap.get(mapCategoryTestName1))))
                .andExpect(jsonPath(
                        String.format("$.%s",mapCategoryTestName2),
                        Matchers.is(booksPerCategoryMap.get(mapCategoryTestName2))));
    }
}
