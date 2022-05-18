package com.globallogic.bookshelf.utils;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class VerificationTest {

    private static Book book;
    private static Category category, category1;
    @Mock
    private static CategoryRepository categoryRepository;

    @BeforeAll
    public static void initVariables() {
        category = null;
        category1 = new Category(1, "categoryName");
        book = new Book();
    }

    @Test
    public void ofTheCategoryResourceNotFoundExceptionTest() {
        Exception exception = assertThrows(BookshelfResourceNotFoundException.class, () ->
                Verification.ofTheCategory(category, book, categoryRepository)
        );

        String expectedMessage = "Category not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void ofTheCategorySuccessWithCustomCategoryTest() {
        Mockito.when(categoryRepository.getById(1)).thenReturn(category1);

        Verification.ofTheCategory(category1, book, categoryRepository);

        assertEquals(category1, book.getCategory());
    }
}
