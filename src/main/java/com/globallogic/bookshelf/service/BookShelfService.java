package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.controller.BookSO;
import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.BorrowRepository;
import com.globallogic.bookshelf.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
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
    protected BookRepository bookRepository;
    @Autowired
    protected CategoryRepository categoryRepository;
    protected BorrowRepository borrowRepository;
    @Autowired
    protected ModelMapper modelMapper;


    public BookShelfService(BookRepository bkRepository, BorrowRepository bwRepository, ModelMapper model) {
        bookRepository = bkRepository;
        borrowRepository = bwRepository;
        modelMapper = model;
    }

    /**
     * Create a book with specified parameters
     *
     * @param so DTO body to specify the book parameters in repository
     * @return DTO of the created book
     */
    public BookSO create(BookSO so){
        Book book = modelMapper.map(so,Book.class);
        Category category = categoryRepository.findByName(book.getCategory().getName());
        if(category == null) {
            throw new BookshelfResourceNotFoundException("Category not found");
        } else if(category.getName().equals("Default")) {
            book.setCategory(categoryRepository.getById(4));
        } else {
            book.setCategory(categoryRepository.getById(category.getId()));
        }
        return modelMapper.map(bookRepository.save(book),BookSO.class);
    }

    public void delete(Integer id) {
        Book found_book = bookRepository.getById(id);
        if (!found_book.isAvailable()) {
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

    /**
     * Get information about every book availability
     *
     * @return Hashmap with book and information about the book availability (owner and date of the borrow)
     */
    public HashMap<Book, String> getBooksAvailability() {
        HashMap<Book, String> booksAvailability = new HashMap<>();
        List<Book> allBooks = bookRepository.findAll();
        for (Book book : allBooks) {
            if (book.isAvailable()) {
                booksAvailability.put(book, "available");
            } else {
                Borrow booksBorrow = borrowRepository.findBorrowByBook(book);
                Date dateOfTheBorrow = booksBorrow.getBorrowed();
                String ownerOfTheBorrow = booksBorrow.getFirstname() + " " + booksBorrow.getSurname();
                String infoAboutTheBorrow = ownerOfTheBorrow + " : " + dateOfTheBorrow;
                booksAvailability.put(book, infoAboutTheBorrow);
            }
        }
        return booksAvailability;
    }
}

