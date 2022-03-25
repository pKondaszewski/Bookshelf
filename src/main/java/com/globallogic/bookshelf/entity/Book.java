package com.globallogic.bookshelf.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Book {
    Integer id;
    String author;
    String name;
    String category;
}
