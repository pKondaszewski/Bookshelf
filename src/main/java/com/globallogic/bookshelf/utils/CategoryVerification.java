package com.globallogic.bookshelf.utils;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.repository.CategoryRepository;


import java.util.Date;

public class CategoryVerification {
   public static CategoryRepository categoryRepository;

    public static Category checkCategory(Category category, Book book) {
            if (category == null) {
                throw new BookshelfResourceNotFoundException("Category not found");
            }else if (category.getName().equals("Default")) {
                book.setCategory(categoryRepository.getById(4));
            } else {
                book.setCategory(categoryRepository.getById(category.getId()));
            }
            return category;
        }
    }

