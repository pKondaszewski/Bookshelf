package com.globallogic.shelfuser.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Definition class of the ShelfUser entity.
 *
 * @author Bartlomiej Chojnacki
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class ShelfUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    protected String firstname;
    protected String lastname;
    protected String status;
    
}
