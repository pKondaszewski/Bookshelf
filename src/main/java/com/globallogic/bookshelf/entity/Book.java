package com.globallogic.bookshelf.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


/**
 * Definition class of the Book entity.
 *
 * @author Bartlomiej Chojnacki
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    protected String author;
    protected String name;
    protected boolean available;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    protected Category category;
}
