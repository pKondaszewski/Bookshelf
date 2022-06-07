package com.globallogic.bookshelf.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

/**
 * Interface for CategoryController
 *
 * @author Przemysław Kondaszewski
 */
public interface CategoryInterface {

    @PostMapping(path = "/categoryCreate")
    @ApiOperation(value = "Create a category with given name")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Category created"),
                            @ApiResponse(code = 409, message = "Category with given name already exists"),
                            @ApiResponse(code = 500, message = "Internal Category server error")})
    ResponseEntity<String> create(@RequestParam(name = "name") String name);

    @DeleteMapping(path = "/categoryDelete",produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Delete a category based by the name")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Category deleted"),
                            @ApiResponse(code = 404, message = "Category not found"),
                            @ApiResponse(code = 409, message = "Can't delete starting category"),
                            @ApiResponse(code = 500, message = "Internal Category server error")})
    ResponseEntity<String> delete(@RequestParam String name);

    @GetMapping(path = "/amountOfBooksPerCategory", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Show amount of books per each category")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Books per category ratios found"),
                            @ApiResponse(code = 500, message = "Internal Category server error")})
    ResponseEntity<HashMap<String, Integer>> getAmountOfBooksPerCategory();
}
