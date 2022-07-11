package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Category;
import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.CategoryRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Business logic of the /category request
 *
 * @author Przemyslaw Kondaszeski
 */
@Component
public class CategoryService {

    protected CategoryRepository categoryRepository;
    protected BookRepository bookRepository;
    protected List<String> startingCategories;

    public CategoryService(CategoryRepository cRepository, BookRepository bRepository) {
        categoryRepository = cRepository;
        bookRepository = bRepository;
        startingCategories = Arrays.asList("Programming", "Management", "Testing", "Default");
    }

    /**
     * Create a category
     *
     * @param name name to specify the category in the repository
     * @throws BookshelfConflictException exception informing that category with given name already exists
     */
    public void create(String name) {
        Category foundCategory = categoryRepository.findByName(name);
        if (foundCategory == null) {
            Category category = new Category(null, name);
            categoryRepository.save(category);
        } else {
            throw new BookshelfConflictException(String.format("Category with name %s already exists.", name));
        }
    }

    /**
     * Delete a category
     *
     * @param name name to specify the category in the repository
     * @throws BookshelfResourceNotFoundException exception informing that category with given name doesn't exist
     */
    public void delete(String name) {
        Category foundCategory = categoryRepository.findByName(name);
        if (startingCategories.contains(name)) {
            throw new BookshelfConflictException(String.format("Can't delete %s. It's a starting category", name));
        } else if (foundCategory == null) {
            throw new BookshelfResourceNotFoundException(String.format("Category with name %s doesn't exist.", name));
        } else {
            List<Book> foundBooksByCategory = bookRepository.findAllByCategory(foundCategory);
            if (!foundBooksByCategory.isEmpty()) {
                for (Book book : foundBooksByCategory) {
                    book.setCategory(new Category(4, "Default"));
                }
            }
            categoryRepository.delete(foundCategory);
        }
    }

    /**
     * Get amount of books per every category ratios
     *
     * @return Hashmap containing all categories and amount of books attached to them
     */
    public HashMap<String, Integer> getAmountOfBooksPerCategory() {
        HashMap<String, Integer> booksPerCategoriesMap = new HashMap<>();
        List<Category> allCategories = categoryRepository.findAll();
        for (Category category : allCategories) {
            String categoryName = category.getName();
            Integer amountOfBooks = bookRepository.findAllByCategory(category).size();
            booksPerCategoriesMap.put(categoryName, amountOfBooks);
        }
        return booksPerCategoriesMap;
    }


}
