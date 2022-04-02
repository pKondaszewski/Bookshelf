package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.controller.BookSO;
import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Business logic of the /bookshelf request
 *
 * @author Bartlomiej Chojnacki
 * @author Przemyslaw Kondaszeski
 */
@Component
public class BookShelfService {

    @Autowired
    protected BookRepository bookRepository;
    @Autowired
    protected CategoryRepository categoryRepository;
    @Autowired
    protected ModelMapper modelMapper;


    public BookShelfService(BookRepository repository,ModelMapper model){
        bookRepository = repository;
        modelMapper = model;
    }

    /**
     * Get the specific book from the repository
     *
     * @param name name of the wanted book
     * @return DTO of the wanted book
     */
    public BookSO get(String name){
        Book found = bookRepository.findByName(name);
        return modelMapper.map(found,BookSO.class);
    }

    /**
     * Create a book with specified parameters
     *
     * @param so DTO body to specify the book parameters in repository
     * @return DTO of the created book
     */
    public BookSO create(BookSO so){
        Book book = modelMapper.map(so,Book.class);
        Category category = categoryRepository.findByName(book.getCategory().getName());
        if(category == null) {
            throw new BookshelfResourceNotFoundException("Category not found");
        } else if(category.getName().equals("Default")) {
            book.setCategory(categoryRepository.getById(4));
        } else {
            book.setCategory(categoryRepository.getById(category.getId()));
        }
        return modelMapper.map(bookRepository.save(book),BookSO.class);
    }

}
