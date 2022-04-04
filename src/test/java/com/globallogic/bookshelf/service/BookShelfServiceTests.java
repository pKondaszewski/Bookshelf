package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.controller.BookSO;
import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.repository.BookRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
public class BookShelfServiceTests {

    private Book bookTest;
    private BookSO bookSOTest;

    protected static ModelMapper model = new ModelMapper();


    @Mock
    BookRepository repository;

    @InjectMocks
    BookShelfService service;

    @BeforeAll
    public static void setModel() {
        model.getConfiguration().setPropertyCondition(Conditions.isNotNull());
    }

    @BeforeEach
    public void setBook(){
        service = new BookShelfService(repository,model);
        bookTest = new Book();
        bookTest.setName("TestName");
        bookTest.setCategory(new Category(1, "testCategoryName"));
        bookTest.setAuthor("TestAuthor");
        bookTest.setId(2);

        bookSOTest = new BookSO();
        bookSOTest.setName("TestName");
        bookSOTest.setCategory(new Category(1, "testCategoryName"));
        bookSOTest.setAuthor("TestAuthor");
        bookSOTest.setId(2);
    }

}