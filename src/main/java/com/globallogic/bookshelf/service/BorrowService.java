package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.BorrowRepository;
import com.globallogic.bookshelf.utils.UserHistory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
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
     * Create a borrow
     *
     * @param borrowBody
     * @return String.format informing that book was borrowed
     * @throws BookshelfConflictException exception informing that book is already borrowed
     */
    @Transactional
    public void borrowBook(Borrow borrowBody) {
        Book book = bookRepository.findById(borrowBody.getBook().getId()).get();
        if (book.isAvailable()) {
            book.setAvailable(false);
            bookRepository.save(book);
            if (borrowBody.getBorrowed() == null) {

                borrowBody.setBorrowed(new Date());
            }
            borrowRepository.save(borrowBody);
        } else {
            throw new BookshelfConflictException(String.format("Book with name : %s is already borrowed.", book.getName()));
        }
    }

    /**
     * Create a borrow by author and book title
     *
     * @param borrowBody body of the book to borrow
     * @throws BookshelfConflictException exception informing that book is already borrowed
     */
    @Transactional
    public void borrowBookByAuthorAndTitle(Borrow borrowBody) {
        String bookAuthor = borrowBody.getBook().getAuthor();
        String bookName = borrowBody.getBook().getName();
        Book book = bookRepository.findBookByAuthorAndName(bookAuthor, bookName);
        if (book == null) {
            throw new BookshelfResourceNotFoundException(
                    String.format("Book with author : %s and name : %s doesn't exist.", bookAuthor, bookName)
            );
        } else {
            if (book.isAvailable()) {
                book.setAvailable(false);
                bookRepository.save(book);
                if (borrowBody.getBorrowed() == null) {
                    borrowBody.setBorrowed(new Date());
                }
                borrowRepository.save(borrowBody);
            } else {
                throw new BookshelfConflictException(
                        String.format("Book with author : %s and name : %s is already borrowed.", bookAuthor, bookName)
                );
            }
        }
    }


    /**
     * Return a book
     *
     * @param borrowBody
     * @throws com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException exception informing that book was not borrowed
     */
    @Transactional
    public void returnBook(Borrow borrowBody) {
        Borrow borrow = borrowRepository.findById(borrowBody.getId()).get();
        Book book = bookRepository.findById(borrow.getBook().getId()).get();
        if (!book.isAvailable()) {
            book.setAvailable(true);
            bookRepository.save(book);
            borrow.setReturned(new Date());
            borrowRepository.save(borrow);
        } else {
            throw new NoSuchElementException(String.format("Book with name : %s is not borrowed.", book.getName()));
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
        StringBuilder activeBooksInfo = new StringBuilder();
        int numberOfBorrowedBooks = 0;
        List<Borrow> foundBorrows = borrowRepository.findBorrowsByFirstnameAndSurname(firstname, surname);
        for (Borrow borrow : foundBorrows) {
            Book book = borrow.getBook();
            if (!book.isAvailable() && borrow.getReturned() == null) {
                activeBooksInfo.append(book.getName()).append("; ");
                numberOfBorrowedBooks += 1;
            }
        }
        foundBorrows.removeIf(
                borrow -> (borrow.getReturned() == null)
        );
        return new UserHistory(foundBorrows, activeBooksInfo.toString(), numberOfBorrowedBooks);
    }
}