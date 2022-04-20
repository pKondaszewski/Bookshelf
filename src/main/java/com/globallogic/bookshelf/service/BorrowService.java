package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.BorrowRepository;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
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
     * Return a book
     *
     * @param borrowBody
     * @throws com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException exception informing that book was not borrowed
     */
    @Transactional
    public void returnBook(Borrow borrowBody) {

        Integer id = borrowBody.getId();
        Borrow borrow = borrowRepository.findById(id).get();
        Book book = bookRepository.findById(borrow.getId()).get();

        if (!book.isAvailable()) {
            book.setAvailable(true);
            bookRepository.save(book);
            borrow.setReturned(new Date());
            borrowRepository.save(borrow);
        } else {
            throw new BookshelfResourceNotFoundException(String.format("Book with name : %s is not borrowed.", book.getName()));
        }
    }

    /**
     * Delete a specific, finished borrow.
     *
     * @param id id of the specific borrow
     * @throws BookshelfResourceNotFoundException exception informing that borrow doesn't exist
     * @throws BookshelfConflictException exception informing that borrow is still active
     */
    @Transactional
    public void deleteBorrow(Integer id) {
        Optional<Borrow> foundBorrow = borrowRepository.findById(id);
        if (foundBorrow.isEmpty()) {
            throw new BookshelfResourceNotFoundException(String.format("Borrow with id=%d doesn't exist",id));
        } else {
            Borrow borrow = foundBorrow.get();
            if (borrow.getReturned() == null) {
                throw new BookshelfConflictException(String.format("Borrow with id=%d is still active. Can't delete",id));
            } else {
                borrowRepository.deleteById(id);
            }
        }
    }

    public HashMap<List<Borrow>, String> getUserBorrowHistory(String firstname, String surname) {
        HashMap<List<Borrow>, String> returnMap = new HashMap<>();
        StringBuilder returnMessage = new StringBuilder("Active borrow on books : ");
        int numberOfBorrowedBooks = 0;
        List<Borrow> foundBorrows = borrowRepository.findBorrowsByFirstnameAndSurname(firstname, surname);
        for (Borrow borrow : foundBorrows) {
            Book book = borrow.getBook();
            if (!book.isAvailable()) {
                returnMessage.append(book.getName());
                numberOfBorrowedBooks += 1;
            }
        }
        returnMessage.append(String.format(". Liczba aktywnych wypożyczonych ksiazek = %d", numberOfBorrowedBooks));
        returnMap.put(foundBorrows, returnMessage.toString());
        return returnMap;
    }
}