package com.globallogic.bookshelf.controller;

import com.globallogic.bookshelf.exeptions.BookshelfConflictException;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * POST Request to create one category based on the name.
     *
     * @param name name of the category
     * @return ResponseEntity that informs about the creation of the category
     */
    @PostMapping(consumes = "text/plain", produces = "text/plain")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Category created"),
                            @ApiResponse(code = 409, message = "Category with given name already exists"),
                            @ApiResponse(code = 500, message = "Internal Category server error")})
    public ResponseEntity<String> create(@RequestBody String name) {
        try {
            categoryService.create(name);
            log.info("Creating category={}", name);
            return new ResponseEntity<>(String.format("Category %s created successfully", name), HttpStatus.CREATED);
        } catch (BookshelfConflictException b1) {
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
    @DeleteMapping(consumes = "text/plain", produces = "text/plain")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Category deleted"),
                            @ApiResponse(code = 404, message = "Category not found"),
                            @ApiResponse(code = 409, message = "Can't delete starting category"),
                            @ApiResponse(code = 500, message = "Internal Category server error")})
    public ResponseEntity<String> delete(@RequestBody String name) {
        try {
            categoryService.delete(name);
            log.info("Deleting category={}", name);
            return new ResponseEntity<>(String.format("Category %s deleted successfully", name), HttpStatus.OK);
        } catch (BookshelfConflictException b1) {
            return new ResponseEntity<>(String.format("Can't delete %s. It's a starting category", name),
                    HttpStatus.CONFLICT);
        } catch (BookshelfResourceNotFoundException b2) {
            return new ResponseEntity<>(String.format("Category with name %s doesn't exist.", name),
                    HttpStatus.NOT_FOUND);
        }
    }

    /**
     * GET Request to receive a map that shows how many books are attached to certain category.
     *
     * @return ResponseEntity that contains books per certain category ratios HashMap
     */
    @GetMapping(path = "/amountOfBooksPerCategory", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Books per category ratios found"),
                            @ApiResponse(code = 500, message = "Internal Category server error")})
    public ResponseEntity<HashMap<String, Integer>> getAmountOfBooksPerCategory() {
        HashMap<String, Integer> booksPerCategoryMap = categoryService.getAmountOfBooksPerCategory();
        log.info("Books per category ratios = {}", booksPerCategoryMap);
        return new ResponseEntity<>(booksPerCategoryMap, HttpStatus.OK);
    }
}
