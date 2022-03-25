package com.globallogic.bookshelf.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public class Book {
    String id;
    String author;
    String name;
    String category;
}
