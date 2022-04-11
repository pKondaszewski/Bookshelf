package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.controller.BookSO;
import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.repository.BookRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
     * Get the specific book from the repository
     *
     * @param name name of the wanted book
     * @return DTO of the wanted book
     */
    public BookSO get(String name) {
        Book found = bookRepository.findByName(name);
        return modelMapper.map(found, BookSO.class);
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

    /**
     * Get list of all available books
     *
     * @return list of all available books
     *
     */
    public Book getAllBooksAvailable() {
        Book booksAvailable = bookRepository.findByAvailable(true);

        return booksAvailable;
    }

    /**
     * Get list of all books
     *
     * @return list of all available books
     *
     */
    public Book getAllBooks() {
        Book book = (Book) bookRepository.findAll();


        return book;
    }
    /**
     * Delete a book
     *
     * @param id id to specify the book in the repository
     * @return Integer value = 0 informing that deleting process went OK
     * @throws BookshelfConflictException exception informing that book with given id is borrowed and cannot be deleted.
     */
    public Integer delete(Integer id) {
        Book found_book = bookRepository.getById(id);
        if (found_book.isAvailable() == false) {
            throw new BookshelfConflictException(String.format("Can't delete %s. This book is borrowed", found_book.getName()));
        }
        bookRepository.delete(found_book);
        return 0;
    }
}

