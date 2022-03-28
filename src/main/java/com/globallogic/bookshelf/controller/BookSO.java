package com.globallogic.bookshelf.controller;

import lombok.Data;

/**
 * DTO (Data Transfer Object) class of the Book entity
 *
 * @author Bartlomiej Chojnacki
 */
@Data
public class BookSO {
    private Integer id;
    private String author;
    private String name;
    private String category;
}
