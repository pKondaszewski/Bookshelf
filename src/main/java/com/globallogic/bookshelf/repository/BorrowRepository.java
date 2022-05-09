package com.globallogic.bookshelf.repository;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, Integer> {

    List<Borrow> findAllByBook(Book book);
    List<Borrow> findAllByFirstnameAndSurname(String firstname, String surname);
    Borrow findByBook(Book book);

}
