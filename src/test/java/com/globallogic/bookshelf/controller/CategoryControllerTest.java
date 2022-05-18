package com.globallogic.bookshelf.controller;

import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class CategoryControllerTest {

    private static MockHttpServletRequestBuilder deleteRequestBuilder,deleteResult;
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
    public static void initVariables() {
        name = "name";

        category1 = "category1";
        category2 = "category2";
        booksPerCategoryMap = new HashMap<>();
        booksPerCategoryMap.put(category1, 5);
        booksPerCategoryMap.put(category2, 10);
        deleteResult = MockMvcRequestBuilders.delete("/category/categoryDelete");
        deleteRequestBuilder = deleteResult.param("name",name);
    }

    @Test
    public void createRequestSuccessTest() throws Exception {
        Mockito.doReturn(null).when(categoryRepository).findByName(name);

        mockMvc
                .perform(post("/category/categoryCreate").param(name, name))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(String.format("Category %s created successfully", name)));
    }
    @Test
    public void createConflictTest() throws Exception {
        doThrow(new BookshelfConflictException(
                String.format("Category with name %s already exists.", name))).when(categoryService)
                .create(name);

        mockMvc
                .perform(post("/category/categoryCreate").param(name, name))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().string(String.format("Category with name %s already exists.", name)));
    }


    @Test
    public void deleteRequestSuccessTest() throws Exception {
        mockMvc
                .perform(delete("/category/categoryDelete").param(name,name))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Category %s deleted successfully", name)));
    }

    @Test
    public void deleteRequestStartingCategoryTest()throws Exception{
        doThrow(new BookshelfConflictException(String.format("Can't delete %s. It's a starting category"
                , name))).when(categoryService)
                .delete(name);

        mockMvc
                .perform(delete("/category/categoryDelete").param(name,name))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().string(String.format("Can't delete %s. It's a starting category", name)));
    }

    @Test
    public void deleteRequestCategoryNotExistTest()throws Exception{
        doThrow(new BookshelfResourceNotFoundException(String.format("Category with name %s doesn't exist.", name)))
                .when(categoryService).delete(name);

        mockMvc
                .perform(delete("/category/categoryDelete").param(name,name))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(String.format("Category with name %s doesn't exist.", name)));
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
