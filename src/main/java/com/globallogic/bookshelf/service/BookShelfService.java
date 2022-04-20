package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.controller.BookSO;
import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.BorrowRepository;
import com.globallogic.bookshelf.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Business logic of the /bookshelf request
 *
 * @author Bartlomiej Chojnacki
 * @author Przemyslaw Kondaszeski
 */
@Slf4j
@Component
public class BookShelfService {

    protected BookRepository bookRepository;
    protected BorrowRepository borrowRepository;
    protected CategoryRepository categoryRepository;
    protected ModelMapper modelMapper;


    public BookShelfService(BookRepository bkRepository, BorrowRepository bwRepository,
                            CategoryRepository cRepository, ModelMapper model) {
        bookRepository = bkRepository;
        borrowRepository = bwRepository;
        categoryRepository = cRepository;
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
        Category category = categoryRepository.findByName(book.getCategory().getName());
        if (category == null) {
            throw new BookshelfResourceNotFoundException("Category not found");
        } else if (category.getName().equals("Default")) {
            book.setCategory(categoryRepository.getById(4));
        } else {
            book.setCategory(categoryRepository.getById(category.getId()));
        }
        return modelMapper.map(bookRepository.save(book), BookSO.class);
    }

    public void delete(Integer id) {
        Book found_book = bookRepository.getById(id);
        if (!found_book.isAvailable()) {
            throw new BookshelfConflictException(String.format("Can't delete %s. This book is borrowed", found_book.getName()));
        }
        bookRepository.delete(found_book);
    }

    /**
     * Get information about every book available
     *
     * @return Hashmap with book and information about the book available (name and available book)
     */

    public HashMap<String, String> getAllBooks() {
        HashMap<String, String> bookMap = new HashMap<>();
        List<Book> bookList = bookRepository.findAll();
        for (Book book : bookList) {
            String bookName = book.getName();
            String bookAuthor = book.getAuthor();
            bookMap.put(bookAuthor, bookName);
        }
        return bookMap;
    }

    /**
     * Get information about every book available
     *
     * @return Hashmap with book and information about the book available (name and available book)
     */

    public HashMap<Book, String> getAllBooksAvailable() {
        HashMap<Book, String> bookMap = new HashMap<>();
        List<Book> bookList = bookRepository.findAll();
        for (Book book : bookList) {
            if (book.isAvailable()) {
                bookMap.put(book, "Available");
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
                List<Borrow> bookBorrows = borrowRepository.findBorrowsByBook(book);
                Borrow activeBorrow = bookBorrows.get(bookBorrows.size() - 1);
                Date dateOfTheBorrow = activeBorrow.getBorrowed();
                String ownerOfTheBorrow = activeBorrow.getFirstname() + " " + activeBorrow.getSurname();
                String infoAboutTheBorrow = ownerOfTheBorrow + " : " + dateOfTheBorrow;
                booksAvailability.put(book, infoAboutTheBorrow);
            }
        }
        return booksAvailability;
    }

    /**
     * Get information about every book availability
     *
     * @return Hashmap with book and information about the book availability (owner and date of the borrow)
     */
    public HashMap<Book, String> getListOfBorrowedBooksWithNewestBorrow() {
        HashMap<Book, String> booksAvailability = new HashMap<>();
        List<Book> allBooks = bookRepository.findAll();
        for (Book book : allBooks) {
            if (!book.isAvailable()) {
                List<Borrow> borrowList = borrowRepository.findBorrowsByBook(book);
                if(!borrowList.isEmpty()) {
                    if (borrowList != null && !borrowList.isEmpty()) {
                        Borrow borrow = borrowList.get(borrowList.size()-1);
                        Date dateOfTheBorrow = borrow.getBorrowed();
                        String ownerOfTheBorrow = borrow.getFirstname() + " " + borrow.getSurname();
                        String infoAboutTheBorrow = ownerOfTheBorrow + " : " + dateOfTheBorrow;
                        booksAvailability.put(book, infoAboutTheBorrow);
                    }
                }
            }
        }
        return booksAvailability;
    }


    /**
     * Get information about every book history
     *
     * @return Hashmap with book and information about the book borrow history
     */

    public HashMap<Book, List<String>> getBooksHistory() {
        HashMap<Book, List<String>> bookHistory = new HashMap<>();
        List<Book> allBooks = bookRepository.findAll();
        for (Book book : allBooks) {

            List<Borrow> booksBorrow = borrowRepository.findBorrowsByBook(book);
            String available;
            List<String> bookList = new ArrayList<>();

            for (Borrow borrow : booksBorrow) {
                String name = borrow.getFirstname() + " " + borrow.getSurname();
                String comment;
                String borrowDate = borrow.getBorrowed().toString();
                String returnDate;

                bookList.add(name);
                bookList.add(borrowDate);
                if (borrow.getReturned() == null) {
                    returnDate = "No returned";
                } else {
                    returnDate = borrow.getReturned().toString();

                }
                bookList.add(returnDate);


                if (borrow.getComment() == null) {
                    comment = "No comment";
                } else {
                    comment = borrow.getComment();

                }
                bookList.add(comment);
            }
            if (book.isAvailable()) {
                available = "available";

            } else {
                available = "No available";
            }
            bookList.add(available);
            bookHistory.put(book, bookList);
        }


        return bookHistory;
    }
}

