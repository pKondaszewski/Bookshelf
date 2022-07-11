package com.globallogic.bookshelf.controller;

import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.service.CategoryService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;


/**
 * Client-server communication class that's processes /category requests
 *
 * @author Przemyslaw Kondaszewski
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/category")
@Slf4j
@Api("Management Api")
public class CategoryController implements CategoryInterface{

    @Autowired
    private CategoryService categoryService;

    /**
     * POST Request to create one category based on the name.
     *
     * @param name name of the category
     * @return ResponseEntity that informs about the creation of the category
     */
    @Override
    public ResponseEntity<String> create(String name) {
        try {
            categoryService.create(name);
            log.info("Creating category={}", name);
            return new ResponseEntity<>(String.format("Category %s created successfully", name), HttpStatus.CREATED);
        } catch (BookshelfConflictException exception) {
            return new ResponseEntity<>(String.format("Category with name %s already exists.", name),
                    HttpStatus.CONFLICT);
        }
    }

    /**
     * DELETE Request to remove one category based on the name.
     *
     * @param name name of the category
     * @return ResponseEntity that informs about the removal of the category
     */
    @Override
    public ResponseEntity<String> delete(String name) {
        try {
            categoryService.delete(name);
            log.info("Deleting category={}", name);
            return new ResponseEntity<>(String.format("Category %s deleted successfully", name), HttpStatus.OK);
        } catch (BookshelfConflictException exception) {
            return new ResponseEntity<>(String.format("Can't delete %s. It's a starting category", name),
                    HttpStatus.CONFLICT);
        } catch (BookshelfResourceNotFoundException exception) {
            return new ResponseEntity<>(String.format("Category with name %s doesn't exist.", name),
                    HttpStatus.NOT_FOUND);
        }
    }

    /**
     * GET Request to receive a map that shows how many books are attached to certain category.
     *
     * @return ResponseEntity that contains books per certain category ratios HashMap
     */
    @Override
    public ResponseEntity<HashMap<String, Integer>> getAmountOfBooksPerCategory() {
        HashMap<String, Integer> booksPerCategoryMap = categoryService.getAmountOfBooksPerCategory();
        log.info("Books per category ratios = {}", booksPerCategoryMap);
        return new ResponseEntity<>(booksPerCategoryMap, HttpStatus.OK);
    }
}
