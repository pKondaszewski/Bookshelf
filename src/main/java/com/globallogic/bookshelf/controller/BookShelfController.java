package com.globallogic.bookshelf.controller;


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
        Book book = new Book();
        book.setAuthor("Adam");
        book.setCategory("Sport");
        book.setId(id);
        book.setName("Skoki");

        log.info("Returning books={}",book);

        return book;
    }

}
