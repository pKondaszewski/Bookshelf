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
public class CategoryServiceTest {

    @Mock
    private static CategoryRepository categoryRepository;

    @Mock
    private static BookRepository bookRepository;

    @Mock
    private static List<String> startingCategories;

    @InjectMocks
    private static CategoryService categoryService;

    private static Category category;
    private static final String categoryName = "randomName";

    private static List<Book> foundBooksByCategory;
    private static List<Category> allCategories;

    private static Book book;
    private static HashMap<String, Integer> outputMap;

    @BeforeAll
    public static void initVariables() {
        category = new Category(null, categoryName);
        allCategories = new ArrayList<>();
        allCategories.add(category);

        book = new Book();
        foundBooksByCategory = new ArrayList<>();
        foundBooksByCategory.add(book);

        outputMap = new HashMap<>();
        outputMap.put(categoryName, 0);
    }

    @Test
    public void createWithNameSuccessTest() {
        Mockito.doReturn(null).when(categoryRepository).findByName(categoryName);

        categoryService.create(categoryName);

        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    public void createWithNameConflictExceptionTest() {
        Mockito.doReturn(category).when(categoryRepository).findByName(categoryName);

        Exception exception = assertThrows(BookshelfConflictException.class, () ->
                categoryService.create(categoryName)
        );

        String expectedMessage = String.format("Category with name %s already exists.", categoryName);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void deleteWithNameSuccessTest() {
        Mockito.doReturn(category).when(categoryRepository).findByName(categoryName);

        categoryService.delete(categoryName);

        Mockito.verify(categoryRepository).delete(category);
    }

    @Test
    public void deleteWithNameConflictExceptionTest() {
        Mockito.doReturn(true).when(startingCategories).contains(categoryName);

        Exception exception = assertThrows(BookshelfConflictException.class, () ->
                categoryService.delete(categoryName)
        );

        String expectedMessage = String.format("Can't delete %s. It's a starting category", categoryName);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void deleteWithNameResourceNotFoundExceptionTest() {
        Mockito.doReturn(null).when(categoryRepository).findByName(categoryName);

        Exception exception = assertThrows(BookshelfResourceNotFoundException.class, () ->
                categoryService.delete(categoryName)
        );

        String expectedMessage = String.format("Category with name %s doesn't exist.", categoryName);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void deleteWithChangeCategoryToDefaultTest() {
        Mockito.doReturn(category).when(categoryRepository).findByName(categoryName);
        Mockito.doReturn(foundBooksByCategory).when(bookRepository).findAllByCategory(category);

        categoryService.delete(categoryName);

        assertEquals("Default", book.getCategory().getName());
        assertEquals(4, book.getCategory().getId());
    }

    @Test
    public void getAmountOfBooksPerCategorySuccessTest() {
        Mockito.doReturn(allCategories).when(categoryRepository).findAll();
        Mockito.doReturn(new ArrayList<>(1)).when(bookRepository).findAllByCategory(category);

        assertEquals(categoryService.getAmountOfBooksPerCategory(), outputMap);
    }
}
