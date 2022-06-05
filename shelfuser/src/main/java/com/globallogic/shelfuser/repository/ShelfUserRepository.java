package com.globallogic.shelfuser.repository;



import com.globallogic.shelfuser.entity.ShelfUser;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ShelfUserRepository extends JpaRepository<ShelfUser, Integer> {


    ShelfUser findByFirstnameAndLastname (String firstName,String lastName);


}