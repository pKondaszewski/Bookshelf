package com.globallogic.bookshelf.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Definition class of the Category entity.
 *
 * @author Przemyslaw Kondaszewski
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Category {
    @Id
    @SequenceGenerator(name="seq", sequenceName="seq", initialValue = 5, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq")
    private Integer id;
    private String name;
}