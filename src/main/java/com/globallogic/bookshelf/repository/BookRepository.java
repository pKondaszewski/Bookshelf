package com.globallogic.bookshelf.repository;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Integer> {

    Book findByTitle(String title);
    Optional<Book> findByAuthorAndTitle(String author, String title);
    List<Book> findAllByCategory(Category category);
    List<Book> findAllByAvailable(boolean available);
}