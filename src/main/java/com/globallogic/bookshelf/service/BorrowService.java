package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.BorrowRepository;
import com.globallogic.bookshelf.utils.DateVerification;
import com.globallogic.bookshelf.utils.StringRepresentationOfTheBorrow;
import com.globallogic.bookshelf.utils.UserHistory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Business logic of the /borrow request
 *
 * @author Bartłomiej Chojnacki
 */

@Component
public class BorrowService {

    protected BorrowRepository borrowRepository;
    protected BookRepository bookRepository;

    public BorrowService(BorrowRepository bwRepository, BookRepository bkRepository) {
        borrowRepository = bwRepository;
        bookRepository = bkRepository;
    }


    /**
     * Create a borrow with book id, personal information (firstname, surname) and
     * additional information (date of the borrow, comment)
     *
     * @param id         author of the book
     * @param firstname  firstname of the person that is borrowing the book
     * @param surname    surname of the person that is borrowing the book
     * @param borrowDate date of the borrow
     * @param comment    additional comment for the borrow
     * @throws BookshelfResourceNotFoundException exception informing that book with given author and title doesn't exist
     * @throws BookshelfConflictException         exception informing that book with given author and title is already borrowed
     */
    @Transactional
    public void borrowBookById(Integer id, String firstname,
                               String surname, Date borrowDate, String comment) {
        Optional<Book> book = bookRepository.findById(id);

        if (book.isEmpty()) {
            throw new BookshelfResourceNotFoundException(
                    String.format("Book with id : %s doesn't exist.", id)
            );
        } else {
            if (book.get().isAvailable()) {
                book.get().setAvailable(false);
                bookRepository.save(book.get());
                borrowDate = DateVerification.checkNullDate(borrowDate);
                Borrow borrow = new Borrow(null, borrowDate, null, firstname, surname, comment, book.get());
                borrowRepository.save(borrow);
            } else {
                throw new BookshelfConflictException(
                        String.format("Book with id : %s is already borrowed.", id)
                );
            }

        }
    }


    /**
     * Create a borrow with book information (author, title), personal information (firstname, surname) and
     * additional information (date of the borrow, comment)
     *
     * @param author     author of the book
     * @param title      title of the book
     * @param firstname  firstname of the person that is borrowing the book
     * @param surname    surname of the person that is borrowing the book
     * @param borrowDate date of the borrow
     * @param comment    additional comment for the borrow
     * @throws BookshelfResourceNotFoundException exception informing that book with given author and title doesn't exist
     * @throws BookshelfConflictException         exception informing that book with given author and title is already borrowed
     */
    @Transactional
    public void borrowBookByAuthorAndTitle(String author, String title, String firstname,
                                           String surname, Date borrowDate, String comment) {
        Book book = bookRepository.findByAuthorAndTitle(author, title);
        if (book == null) {
            throw new BookshelfResourceNotFoundException(
                    String.format("Book with author : %s and title : %s doesn't exist.", author, title)
            );
        } else {
            if (book.isAvailable()) {
                book.setAvailable(false);
                bookRepository.save(book);
                borrowDate = DateVerification.checkNullDate(borrowDate);
                Borrow borrow = new Borrow(null, borrowDate, null, firstname, surname, comment, book);
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
     * Get a borrow history and actual borrowed books info of the specific user based by firstname and surname
     *
     * @param firstname String represents firstname of the user
     * @param surname   String represents surname of the user
     * @return List with every finished borrow of the user and active borrowed books with the number of them.
     */
    public UserHistory getUserBorrowHistory(String firstname, String surname) {
        List<String> completedBorrows = new ArrayList<>();
        List<String> uncompletedBorrows = new ArrayList<>();
        int numberOfBorrowedBooks = 0;
        StringRepresentationOfTheBorrow userFriendlyLook = new StringRepresentationOfTheBorrow();
        List<Borrow> borrowsOfTheUser = borrowRepository.findAllByFirstnameAndSurname(firstname, surname);
        for (Borrow borrow : borrowsOfTheUser) {
            Book book = borrow.getBook();
            if (!book.isAvailable() && borrow.getReturned() == null) {
                String borrowUserFriendlyLook = userFriendlyLook.stringRepresentationOfUncompletedBorrow(borrow);
                uncompletedBorrows.add(borrowUserFriendlyLook);
                numberOfBorrowedBooks += 1;
            }
        }
        borrowsOfTheUser.removeIf(
                borrow -> (borrow.getReturned() == null)
        );
        for (Borrow borrow : borrowsOfTheUser) {
            String borrowUserFriendlyLook = userFriendlyLook.stringRepresentationOfCompletedBorrow(borrow);
            completedBorrows.add(borrowUserFriendlyLook);
        }
        return new UserHistory(completedBorrows, uncompletedBorrows, numberOfBorrowedBooks);
    }
}