package com.globallogic.bookshelf.utils;


import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class CustomBorrow {

 private Integer id;
 private Date borrowed;
 private String firstname;
 private String lastname;
 private String comment;

 private Book book;


}
