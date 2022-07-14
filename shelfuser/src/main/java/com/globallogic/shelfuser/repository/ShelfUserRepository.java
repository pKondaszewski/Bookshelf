package com.globallogic.shelfuser.repository;



import com.globallogic.shelfuser.entity.ShelfUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ShelfUserRepository extends JpaRepository<ShelfUser, Integer> {

    Optional<ShelfUser> findByFirstnameAndLastname (String firstName, String lastName);

}