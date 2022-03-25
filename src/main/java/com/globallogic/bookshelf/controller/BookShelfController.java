package com.globallogic.bookshelf.controller;


import com.globallogic.bookshelf.entity.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/bookshelf")
@Slf4j

public class BookShelfController {

    @GetMapping(path = "/{id}")
    public Book get(@PathVariable(name = "id")String id){
        Book book = new Book(id,"adam","dwd","dwad");
        log.info("Returning books={}",book);

        return book;
    }

}
