package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTests {

    private static Category categoryTest;

    @Mock
    private static CategoryRepository categoryRepository;

    @Mock
    private static BookRepository bookRepository;

    @Mock
    private static List<String> startingCategories;

    @InjectMocks
    private static CategoryService service;

    private static final String categoryTestName = "testName";

    private static List<Book> foundBooksByCategoryTest;
    private static Book bookTest;

    private static List<Category> allCategoriesTest;
    private static HashMap<String, Integer> mapTest;

    @BeforeAll
    public static void setModel() {
        startingCategories = new ArrayList<>();

        service = new CategoryService(categoryRepository, bookRepository);

        categoryTest = new Category(null, categoryTestName);

        foundBooksByCategoryTest = new ArrayList<>();
        bookTest = new Book();
        foundBooksByCategoryTest.add(bookTest);

        allCategoriesTest = new ArrayList<>();
        allCategoriesTest.add(categoryTest);

        mapTest = new HashMap<>();
        mapTest.put(categoryTestName, 0);
    }

    @Test
    public void testCreateWithNameSuccess() {
        Mockito.doReturn(null).when(categoryRepository).findByName(categoryTestName);

        service.create(categoryTestName);

        Mockito.verify(categoryRepository).save(categoryTest);
    }

    @Test
    public void testCreateWithNameConflictException() {
        Mockito.doReturn(categoryTest).when(categoryRepository).findByName(categoryTestName);

        Exception exception = assertThrows(BookshelfConflictException.class, () ->
                service.create(categoryTestName)
        );

        String expectedMessage = String.format("Category with name %s already exists.", categoryTestName);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testDeleteWithNameSuccess() {
        Mockito.doReturn(categoryTest).when(categoryRepository).findByName(categoryTestName);

        service.delete(categoryTestName);

        Mockito.verify(categoryRepository).delete(categoryTest);
    }

    @Test
    public void testDeleteWithNameConflictException() {
        Mockito.doReturn(true).when(startingCategories).contains(categoryTestName);

        Exception exception = assertThrows(BookshelfConflictException.class, () ->
                service.delete(categoryTestName)
        );

        String expectedMessage = String.format("Can't delete %s. It's a starting category", categoryTestName);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testDeleteWithNameResourceNotFoundException() {
        Mockito.doReturn(null).when(categoryRepository).findByName(categoryTestName);

        Exception exception = assertThrows(BookshelfResourceNotFoundException.class, () ->
                service.delete(categoryTestName)
        );

        String expectedMessage = String.format("Category with name %s doesn't exist.", categoryTestName);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testDeleteWithChangeCategoryToDefault() {
        Mockito.doReturn(categoryTest).when(categoryRepository).findByName(categoryTestName);
        Mockito.doReturn(foundBooksByCategoryTest).when(bookRepository).findAllByCategory(categoryTest);

        service.delete(categoryTestName);

        assertEquals("Default", bookTest.getCategory().getName());
        assertEquals(4, bookTest.getCategory().getId());
    }

    @Test
    public void testGetAmountOfBooksPerCategorySuccess() {
        Mockito.doReturn(allCategoriesTest).when(categoryRepository).findAll();
        Mockito.doReturn(new ArrayList<>(1)).when(bookRepository).findAllByCategory(categoryTest);

        assertEquals(service.getAmountOfBooksPerCategory(), mapTest);
    }

}
