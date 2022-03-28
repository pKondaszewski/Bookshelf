package com.globallogic.bookshelf.repository;

import com.globallogic.bookshelf.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Integer> {

    Book findByName(String name);

}