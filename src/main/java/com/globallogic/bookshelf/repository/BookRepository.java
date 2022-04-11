package com.globallogic.bookshelf.repository;

import com.globallogic.bookshelf.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Integer> {

    Book findByName(String name);

    @Query("select b from Book b where b.available = ?1")
    Book findByAvailable(boolean available);

}