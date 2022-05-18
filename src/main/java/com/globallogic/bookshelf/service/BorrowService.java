package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.BorrowRepository;
import com.globallogic.bookshelf.utils.StringRepresentation;
import com.globallogic.bookshelf.utils.UserHistory;
import com.globallogic.bookshelf.utils.Verification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Business logic of the /borrow request
 *
 * @author Bartłomiej Chojnacki
 */

@Slf4j
@Component
public class BorrowService {

    protected BorrowRepository borrowRepository;
    protected BookRepository bookRepository;

    public BorrowService(BorrowRepository bwRepository, BookRepository bkRepository) {
        borrowRepository = bwRepository;
        bookRepository = bkRepository;
    }


    /**
     * Create a borrow with book id, personal information (firstname, lastname) and
     * additional information (date of the borrow, comment)
     *
     * @param id         author of the book
     * @param firstname  firstname of the person that is borrowing the book
     * @param lastname   lastname of the person that is borrowing the book
     * @param borrowDate date of the borrow
     * @param comment    additional comment for the borrow
     * @throws BookshelfResourceNotFoundException exception informing that book with given author and title doesn't exist
     * @throws BookshelfConflictException         exception informing that book with given author and title is already borrowed
     */
    @Transactional
    public void borrowBookById(Integer id, String firstname,
                               String lastname, Date borrowDate, String comment) {

       Optional<Book> bookOptional = bookRepository.findById(id);

        if (bookOptional.isEmpty()) {
            throw new BookshelfResourceNotFoundException(
                    String.format("Book with id : %s doesn't exist.", id)
            );
        } else {
            Book book = bookOptional.get();
            if (book.isAvailable()) {
                book.setAvailable(false);
                bookRepository.save(book);
                borrowDate = Verification.ofTheDate(borrowDate);
                Borrow borrow = new Borrow(null, borrowDate, null, firstname, lastname, comment, book);
                borrowRepository.save(borrow);
            } else {
                throw new BookshelfConflictException(
                        String.format("Book with id : %s is already borrowed.", id)
                );
            }
        }
    }


    /**
     * Create a borrow with book information (author, title), personal information (firstname, lastname) and
     * additional information (date of the borrow, comment)
     *
     * @param author     author of the book
     * @param title      title of the book
     * @param firstname  firstname of the person that is borrowing the book
     * @param lastname   lastname of the person that is borrowing the book
     * @param borrowDate date of the borrow
     * @param comment    additional comment for the borrow
     * @throws BookshelfResourceNotFoundException exception informing that book with given author and title doesn't exist
     * @throws BookshelfConflictException         exception informing that book with given author and title is already borrowed
     */
    @Transactional
    public void borrowBookByAuthorAndTitle(String author, String title, String firstname,
                                           String lastname, Date borrowDate, String comment) {
        Book book = bookRepository.findByAuthorAndTitle(author, title);
        if (book == null) {
            throw new BookshelfResourceNotFoundException(
                    String.format("Book with author : %s and title : %s doesn't exist.", author, title)
            );
        } else {
            if (book.isAvailable()) {
                book.setAvailable(false);
                bookRepository.save(book);
                borrowDate = Verification.ofTheDate(borrowDate);
                Borrow borrow = new Borrow(null, borrowDate, null, firstname, lastname, comment, book);
                borrowRepository.save(borrow);
            } else {
                throw new BookshelfConflictException(
                        String.format("Book with author : %s and title : %s is already borrowed.", author, title)
                );
            }
        }
    }


    /**
     * Return a book by borrow id.
     *
     * @param id of borrow
     * @throws com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException exception informing that book was not borrowed
     */
    @Transactional
    public void returnBook(Integer id) {
        Optional<Borrow> borrow = borrowRepository.findById(id);
        if (borrow.isEmpty()) {
            throw new BookshelfResourceNotFoundException(
                    String.format("Borrow with id= %s not found", id)
            );
        } else {
            Book book = bookRepository.findById(borrow.get().getBook().getId()).get();
            if (!book.isAvailable()) {
                book.setAvailable(true);
                bookRepository.save(book);
                borrow.get().setReturned(new Date());
                borrowRepository.save(borrow.get());
            } else {
                throw new BookshelfConflictException(String.format("Borrow with id : %s is ended.", borrow.get().getId()));
            }
        }
    }


    /**
     * Delete a specific, finished borrow.
     *
     * @param id id of the specific borrow
     * @throws BookshelfResourceNotFoundException exception informing that borrow doesn't exist
     * @throws BookshelfConflictException         exception informing that borrow is still active
     */
    @Transactional
    public void deleteBorrow(Integer id) {
        Optional<Borrow> foundBorrow = borrowRepository.findById(id);
        if (foundBorrow.isEmpty()) {
            throw new BookshelfResourceNotFoundException(String.format("Borrow with id=%d doesn't exist", id));
        } else {
            Borrow borrow = foundBorrow.get();
            if (borrow.getReturned() == null) {
                throw new BookshelfConflictException(String.format("Borrow with id=%d is still active. Can't delete", id));
            } else {
                borrowRepository.deleteById(id);
            }
        }
    }

    /**
     * Get a borrow history and actual borrowed books info of the specific user based by firstname and lastname
     *
     * @param firstname String represents firstname of the user
     * @param lastname  String represents lastname of the user
     * @return List with every finished borrow of the user and active borrowed books with the number of them.
     */
    public UserHistory getUserBorrowHistory(String firstname, String lastname) {
        List<String> completedBorrows = new ArrayList<>();
        List<String> uncompletedBorrows = new ArrayList<>();
        int numberOfBorrowedBooks = 0;
        List<Borrow> borrowsOfTheUser = borrowRepository.findAllByFirstnameAndLastname(firstname, lastname);
        for (Borrow borrow : borrowsOfTheUser) {
            Book book = borrow.getBook();
            if (!book.isAvailable() && borrow.getReturned() == null) {
                String borrowAsString = StringRepresentation.ofTheBorrow(borrow);
                uncompletedBorrows.add(borrowAsString);
                numberOfBorrowedBooks += 1;
            } else if (book.isAvailable() && borrow.getReturned() != null) {
                String borrowUserFriendlyLook = StringRepresentation.ofTheBorrow(borrow);
                completedBorrows.add(borrowUserFriendlyLook);
            } else {
                log.error("There is something wrong with book : {} and borrow {}", book, borrow);
            }
        }
        return new UserHistory(completedBorrows, uncompletedBorrows, numberOfBorrowedBooks);
    }
}