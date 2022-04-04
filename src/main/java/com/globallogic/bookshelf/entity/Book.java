package com.globallogic.bookshelf.entity;

import lombok.Data;

import javax.persistence.*;


/**
 * Definition class of the book entity.
 *
 * @author Bartlomiej Chojnacki
 */
@Data
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    protected String author;
    protected String name;
    protected String category;
    protected boolean available;


}
