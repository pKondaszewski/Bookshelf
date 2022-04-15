package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.BorrowRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;

/**
 * Business logic of the /borrow request
 *
 * @author Bartłomiej Chojnacki
 */

@Component

public class BorrowService {

    @Autowired
    private BorrowRepository borrowsRepository;
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    protected ModelMapper modelMapper;



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
            borrowsRepository.save(borrowBody);
        } else {
            throw new BookshelfConflictException(String.format("Book with name : %s is already borrowed.", book.getName()));
        }
    }


//    public String borrowBookName(String bookAuthor,String bookName){
//        Book book = bookRepository.findByName(bookName);
//        Borrow borrow = new Borrow();
//
//        if (book.isAvailable()) {
//            book.setAvailable(false);
//            bookRepository.save(book);
//            borrow.
//
//            return String.format("");
//
//        }else {
//            throw new BookshelfConflictException(String.format("Book with name : %s is already borrowed.", book.getName()));
//        }
//
//
//    }

    /**
     * Return a book
     *
     * @param borrowBody
     * @throws com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException exception informing that book was not borrowed
     */
    @Transactional
    public void returnBook(Borrow borrowBody) {

        Integer id = borrowBody.getId();
        Borrow borrow = borrowsRepository.findById(id).get();
        Book book = bookRepository.findById(borrow.getId()).get();

        if (!book.isAvailable()) {
            book.setAvailable(true);
            bookRepository.save(book);
            borrow.setReturned(new Date());
            borrowsRepository.save(borrow);
        } else {
            throw new BookshelfResourceNotFoundException(String.format("Book with name : %s is not borrowed.", book.getName()));
        }
    }
}