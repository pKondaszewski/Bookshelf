package com.globallogic.bookshelf.utils;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class VerificationTest {

    private static Book book;
    private static Category category, category1, category2;

    @BeforeAll
    public static void initVariables() {
        category = null;
        category1 = new Category(4, "Default");
        category2 = new Category(1, "CategoryName");
        book = new Book(1, "a", "s", true, category1);
    }

    @Test
    public void ofTheCategoryResourceNotFoundExceptionTest() {
        Exception exception = assertThrows(BookshelfResourceNotFoundException.class, () ->
                Verification.ofTheCategory(category, book)
        );

        String expectedMessage = "Category not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void ofTheCategorySuccessWithDefaultCategoryTest() {
        Verification.ofTheCategory(category1, book);

        assertEquals(category1, book.getCategory());
    }

    @Test
    public void ofTheCategorySuccessWithCustomCategoryTest() {
        Verification.ofTheCategory(category2, book);

        assertEquals(category2, book.getCategory());
    }

    @Test
    void notNullOfTheDateTest() {

        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        Verification.ofTheDate(Date.from(atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant()));
    }

    @Test

    void nullOfTheDateTest() {


        Verification.ofTheDate(null);
    }
}
