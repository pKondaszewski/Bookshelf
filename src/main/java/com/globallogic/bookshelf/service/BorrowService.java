package com.globallogic.bookshelf.service;


import com.globallogic.bookshelf.controller.BorrowSO;
import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFound;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.BorrowRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Business logic of the /borrows request
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

    public BorrowService(BorrowRepository repository, ModelMapper mapper) {
        borrowsRepository = repository;
        modelMapper = mapper;
    }

    /**
     * Get the specific borrow from the repository based by id
     *
     * @param id name of the borrow
     * @return DTO of the borrow
     * @throws BookshelfResourceNotFound exception informing that borrow with given id doesn't exist
     */

    public BorrowSO get(Integer id) {
        Borrow found_borrow = borrowsRepository.getById(id);
        if (found_borrow == null) {
            throw new BookshelfResourceNotFound(String.format("Category with name : %s doesn't exist.", id));
        } else {
            return modelMapper.map(found_borrow, BorrowSO.class);
        }
    }

    /**
     * Create a borrow
     *
     * @param borrowBody
     * @return Integer value = 0 informing that creating process went OK
     * @throws BookshelfConflictException exception informing that book is already borrowed
     */

    public Integer borrowBook(Borrow borrowBody) {
        Book book = bookRepository.findById(borrowBody.getBook().getId()).get();
        if (book.isAvailable() == true) {

            book.setAvailable(false);
            bookRepository.save(book);
            borrowsRepository.save(borrowBody);

            return 0;

        } else {
            throw new BookshelfConflictException(String.format("Book with name : %s is already borrowed.", book.getName()));

        }
    }

    /**
     * Return a book
     *
     * @param borrowBody
     * @return Integer value = 0 informing that returning process went OK
     * @throws BookshelfResourceNotFound exception informing that book was not borrowed
     */
    public Integer returnBook(Borrow borrowBody) {

        Integer id = borrowBody.getId();
        Borrow borrow = borrowsRepository.findById(id).get();
        Book book = bookRepository.findById(borrow.getId()).get();

        if (book.isAvailable() == false) {
            book.setAvailable(true);
            bookRepository.save(book);
            Date currentDate = new Date();
            borrow.setReturned(currentDate);
            borrowsRepository.save(borrow);

            return 0;
        } else {
            throw new BookshelfResourceNotFound(String.format("Book with name : %s was not borrowed ", book.getName()));
        }
    }

}




