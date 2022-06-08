package com.globallogic.bookshelf.utils;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class VerificationTest {

    @Mock
    private static ReservationRepository reservationRepository;

    private static Book book;
    private static Category category1, category2;
    private LocalDateTime defaultLocalDateTime = LocalDateTime.of(1900, 1, 1, 12, 0);

    @BeforeAll
    public static void initVariables() {
        category1 = new Category(4, "Default");
        category2 = new Category(1, "CategoryName");
        book = new Book(1, "a", "s", true, category1);
    }

    @Test
    public void ofTheCategoryResourceNotFoundExceptionTest() {
        Exception exception = assertThrows(BookshelfResourceNotFoundException.class, () ->
                Verification.ofTheCategory(null, book)
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
    public void ofTheDateNotNullTest() {
        LocalDateTime atStartOfDayResult = LocalDate.of(1970, 1, 1).atStartOfDay();
        Verification.ofTheDate(Date.from(atStartOfDayResult.atZone(ZoneId.of("UTC")).toInstant()));
    }

    @Test
    public void nullOfTheDateTest() {
        Verification.ofTheDate(null);
    }

    @Test
    public void ofTheLocalDateTimeTest() {
        boolean verificationResult = Verification.ofTheLocalDateTime(
                LocalDate.of(2000, 1, 1),
                LocalTime.of(12, 12)
        );
        assertFalse(verificationResult);
    }
}
