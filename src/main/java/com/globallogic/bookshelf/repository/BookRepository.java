package com.globallogic.bookshelf.repository;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    Book findByName(String name);

    Book findBookByAuthorAndName(String author, String name);

    Book findBookById(Integer id);

    List<Book> findAllByCategory(Category category);

    List<Book> findByAvailableIsTrue();

}