package com.globallogic.bookshelf.entity;




import lombok.Data;
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


    protected Date borrowed;
    protected Date returned;
    protected String name;
    protected String surname;

    @OneToOne
    @JoinColumn(name = "book_id",referencedColumnName = "id")
    protected Book book;
}
