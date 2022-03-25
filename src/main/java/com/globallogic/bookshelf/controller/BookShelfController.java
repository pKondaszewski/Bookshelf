package com.globallogic.bookshelf.controller;


import com.globallogic.bookshelf.entity.Book;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/bookshelf")
@Slf4j
public class BookShelfController {

    @ApiOperation(value = "Returns a specified book entity")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Book found", response = Book.class),
                            @ApiResponse(code = 500, message = "Internal Bookshelf server error"),
                            @ApiResponse(code = 404, message = "Book not found")})
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Book getBook(@ApiParam(
            name =  "id",
            type = "Integer",
            value = "Identifier of the book",
            example = "1",
            required = true)
                    @PathVariable(name = "id") Integer id) {
        Book book = new Book(id,"adam","dwd","dwad");
        log.info("Returning books={}", book);
        return book;
    }

}
