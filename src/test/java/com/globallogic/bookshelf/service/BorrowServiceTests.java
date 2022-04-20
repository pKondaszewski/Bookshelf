package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.controller.BorrowSO;
import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.BorrowRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class BorrowServiceTests {

    private static Borrow borrowTest, borrow2Test;
    private static Book bookTest;
    private static Category categoryTest;

    @Mock
    private static BorrowRepository borrowRepository;

    @Mock
    private static BookRepository bookRepository;

    @InjectMocks
    private static BorrowService service;

    private static final int ID_TEST = 1;

    @BeforeAll
    public static void setModel() {

        service = new BorrowService(borrowRepository, bookRepository);

        bookTest = new Book(1,"Tadeusz","Arek",true, categoryTest);
        borrowTest = new Borrow(3, new Date() ,new Date(),"Arek","Darek","awdawwda",bookTest);
        borrow2Test = new Borrow(3, new Date() ,null,"Arek","Darek","awdawwda",bookTest);

    }

    @Test
    public void testDeleteBorrowSuccess() {
        Mockito.doReturn(Optional.of(borrowTest)).when(borrowRepository).findById(ID_TEST);

        service.deleteBorrow(ID_TEST);

        Mockito.verify(borrowRepository).deleteById(ID_TEST);
    }

    @Test
    public void testDeleteBorrowResourceNotFoundException() {
        Mockito.doReturn(Optional.empty()).when(borrowRepository).findById(ID_TEST);

        Exception exception = assertThrows(BookshelfResourceNotFoundException.class, () ->
                service.deleteBorrow(ID_TEST)
        );

        String expectedMessage = String.format("Borrow with id=%d doesn't exist", ID_TEST);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testDeleteBorrowConflictException() {
        Mockito.doReturn(Optional.of(borrow2Test)).when(borrowRepository).findById(ID_TEST);

        Exception exception = assertThrows(BookshelfConflictException.class, () ->
                service.deleteBorrow(ID_TEST)
        );

        String expectedMessage = String.format("Borrow with id=%d is still active. Can't delete", ID_TEST);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
