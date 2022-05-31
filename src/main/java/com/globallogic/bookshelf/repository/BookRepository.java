package com.globallogic.bookshelf.repository;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Category;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    Book findByTitle(String title);
    Book findByAuthorAndTitle(String author, String title);
    List<Book> findAllByCategory(Category category);
    List<Book> findAllByAvailable(boolean available);
}