package com.globallogic.bookshelf.controller;

import com.globallogic.bookshelf.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) class of the Book entity
 *
 * @author Bartlomiej Chojnacki
 */
@AllArgsConstructor
@Data
public class BookSO {
    private Integer id;
    private String author;
    private String name;
    private boolean available;
    private Category category;

}
