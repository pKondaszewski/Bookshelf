package com.globallogic.bookshelf.service;


import com.globallogic.bookshelf.controller.BorrowSO;
import com.globallogic.bookshelf.entity.Borrow;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFound;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.BorrowRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public BorrowService(BorrowRepository repository, ModelMapper mapper){
        borrowsRepository = repository;
        modelMapper = mapper;
    }

    public BorrowSO get(Integer id) {
        Borrow found_borrow = borrowsRepository.getById(id);
        if (found_borrow == null) {
            throw new BookshelfResourceNotFound("Borrow not found.");
        } else {
            return modelMapper.map(found_borrow,BorrowSO.class);
        }
    }

    public BorrowSO create(BorrowSO so) {
        Borrow borrow = modelMapper.map(so, Borrow.class);
        return modelMapper.map(borrowsRepository.save(borrow), BorrowSO.class);
    }



}
