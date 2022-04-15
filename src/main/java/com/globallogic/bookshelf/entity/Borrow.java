package com.globallogic.bookshelf.entity;




import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Date;


/**
 * Definition class of the borrow entity.
 *
 * @author Bartlomiej Chojnacki
 */
@Data
@Entity
public class Borrow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    protected Date borrowed;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    protected Date returned;
    protected String firstname;
    protected String surname;
    protected String comment;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne
    @JoinColumn(name = "book_id",referencedColumnName = "id")
    protected Book book;

}