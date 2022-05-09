package com.globallogic.bookshelf.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;


/**
 * Definition class of the borrow entity.
 *
 * @author Bartlomiej Chojnacki
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Borrow {
    @Id
    @SequenceGenerator(name="borrowSeq", sequenceName="borrowSeq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="borrowSeq")
    protected Integer id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    protected Date borrowed;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    protected Date returned;
    protected String firstname;
    protected String surname;
    protected String comment;

    @OneToOne()
    @JoinColumn(name = "book_id",referencedColumnName = "id")
    protected Book book;

}