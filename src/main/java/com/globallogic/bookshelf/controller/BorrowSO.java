package com.globallogic.bookshelf.controller;


import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.Date;

/**
 * DTO (Data Transfer Object) class of the Borrows entity
 *
 * @author Bartłomiej Chojnacki
 */

@Data
public class BorrowSO {
    private Integer id;
    private Date borrowedDate;
    private Date returnedDate;
    private String username;
    private String surname;
    private String comment;


    private Book book;

}