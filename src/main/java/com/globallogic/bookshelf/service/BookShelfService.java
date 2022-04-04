package com.globallogic.bookshelf.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.globallogic.bookshelf.controller.BookSO;
import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.sun.source.tree.LambdaExpressionTree;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;


/**
 * Business logic of the /bookshelf request
 *
 * @author Bartlomiej Chojnacki
 * @author Przemyslaw Kondaszeski
 */
@Component
public class BookShelfService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    protected ModelMapper modelMapper;


    public BookShelfService(BookRepository repository, ModelMapper model) {
        bookRepository = repository;
        modelMapper = model;
    }

    /**
     * Create a book with specified parameters
     *
     * @param so DTO body to specify the book parameters in repository
     * @return DTO of the created book
     */
    public BookSO create(BookSO so) {
        Book book = modelMapper.map(so, Book.class);
        return modelMapper.map(bookRepository.save(book), BookSO.class);
    }


    public void delete(Integer id) {
        Book found_book = bookRepository.getById(id);
        if (found_book.isAvailable() == false) {
            throw new BookshelfConflictException(String.format("Can't delete %s. This book is borrowed", found_book.getName()));
        }
        bookRepository.delete(found_book);
    }


    public HashMap<String, String> getAllBooks() {
        HashMap<String, String> bookMap = new HashMap<>();
        List<Book> bookList = bookRepository.findAll();

        for (Book book : bookList) {

                String bookName = book.getName();
                String bookAuthor= book.getAuthor();
                bookMap.put(bookAuthor, bookName);


        }
        return bookMap;
    }

    public HashMap<String, Boolean> getAllBooksAvailable() {
        HashMap<String, Boolean> bookMap = new HashMap<>();
        List<Book> bookList = bookRepository.findAll();

        for (Book book : bookList) {
            if (book.isAvailable()) {
                String bookName = book.getName();
                Boolean aBoolean = book.isAvailable();
                bookMap.put(bookName, aBoolean);
            }

        }
        return bookMap;
    }
}

