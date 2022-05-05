package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.BorrowRepository;
import com.globallogic.bookshelf.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;


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


    public BookShelfService(BookRepository bkRepository, BorrowRepository bwRepository,
                            CategoryRepository cRepository) {
        bookRepository = bkRepository;
        borrowRepository = bwRepository;
        categoryRepository = cRepository;
    }

    /**
     * Create a book with specified parameters
     *
     * @param book body of the book
     */
    public void create(Book book) {
        Category category = categoryRepository.findByName(book.getCategory().getName());
        if (category == null) {
            throw new BookshelfResourceNotFoundException("Category not found");
        } else if (category.getName().equals("Default")) {
            book.setCategory(categoryRepository.getById(4));
        } else {
            book.setCategory(categoryRepository.getById(category.getId()));
        }
        bookRepository.save(book);
    }

    public void delete(Integer id) {
        Optional<Book> foundBook = bookRepository.findById(id);
        if (foundBook.isEmpty()) {
            throw new BookshelfResourceNotFoundException(String.format("Book with id=%d doesn't exist", id));
        } else {
            Book book = foundBook.get();
            List<Borrow> borrowList = borrowRepository.findBorrowsByBook(book);
            if (book.isAvailable()) {
                for (Borrow borrow : borrowList) {
                    borrowRepository.delete(borrow);
                }
                bookRepository.deleteById(id);
            } else {
                throw new BookshelfConflictException(String.format("Book with id=%d is still borrowed. Can't delete", id));
            }
        }
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
            String bookName = book.getTitle();
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
        List<Book> allBooks = bookRepository.findAll();
        for (Book book : allBooks) {
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
    public HashMap<Book, String> getListOfBorrowedBooksSort() {
        HashMap<Book, String> booksAvailability = new HashMap<>();
        List<Book> allBooks = bookRepository.findAll();
        for (Book book : allBooks) {
            if (!book.isAvailable()) {
                List<Borrow> borrowList = borrowRepository.findBorrowsByBook(book);
                if (CollectionUtils.isNotEmpty(borrowList)) {
                    Borrow borrow = borrowList.get(borrowList.size() - 1);
                    Date dateOfTheBorrow = borrow.getBorrowed();
                    String ownerOfTheBorrow = "Name : " + borrow.getFirstname() + " " + borrow.getSurname();
                    String infoAboutTheBorrow = ownerOfTheBorrow + " : Date of borrowing book " + dateOfTheBorrow;
                    booksAvailability.put(book, infoAboutTheBorrow);
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
    public HashMap<Book, List<String>> getBooksHistory(String title) {
        HashMap<Book, List<String>> bookHistory = new HashMap<>();
        Book book = bookRepository.findByTitle(title);
        List<Borrow> booksBorrow = borrowRepository.findBorrowsByBook(book);

        List<String> bookList = new ArrayList<>();

        for (Borrow borrow : booksBorrow) {
            String name = "Name: " + borrow.getFirstname() + " " + borrow.getSurname();
            String comment;
            String borrowDate = "Date of borrowing book: " + borrow.getBorrowed().toString();
            String returnDate;

            bookList.add(name);
            bookList.add(borrowDate);
            if (borrow.getReturned() == null) {
                returnDate = "Book not returned";
            } else {
                returnDate = "Date of return book: " + borrow.getReturned().toString();

            }
            bookList.add(returnDate);


            if (borrow.getComment() == null) {
                comment = "No comment";
            } else {
                comment = "Comment: " + borrow.getComment();
            }
            bookList.add(comment);
        }
        String available;
        if (book.isAvailable()) {
            available = "Book is available";

        } else {
            available = "Book is not available";
        }
        bookList.add(available);
        bookHistory.put(book, bookList);


        return bookHistory;
    }
}
