package com.globallogic.shelfuser.service;



import com.globallogic.shelfuser.entity.ShelfUser;
import com.globallogic.shelfuser.exeptions.BookshelfConflictException;
import com.globallogic.shelfuser.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.shelfuser.repository.ShelfUserRepository;
import com.globallogic.shelfuser.utils.Status;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Optional;

@Component
public class ShelfUserService {


    protected ShelfUserRepository shelfUserRepository;

    public ShelfUserService(ShelfUserRepository uRepository) {
        shelfUserRepository = uRepository;
    }



    /**
     * Create a user
     * @param firstName String represents firstName of the user
     * @param lastName String represents lastName of the user
     * @param status of user
     *
     */

    @Transactional
    public void userAdd(String firstName, String lastName, Status status) {
        ShelfUser user = new ShelfUser();
        if (status.name().equals("ACTIVE")) {
            user = new ShelfUser(null, firstName, lastName, status.name());

        }
        if (status.name().equals("INACTIVE")) {
            user = new ShelfUser(null, firstName, lastName, status.name());

        }
        if (status.name().equals("NOT_PRESENT")) {
            user = new ShelfUser(null, firstName, lastName, status.name());

        }
        shelfUserRepository.save(user);
    }

    /**
     * Delete a specific user.
     *
     * @param id id of the specific user
     * @throws BookshelfResourceNotFoundException exception informing that user doesn't exist
     */

    @Transactional
    public void userDelete(Integer id) {
        shelfUserRepository.findById(id)
                .orElseThrow(
                        () -> new BookshelfResourceNotFoundException(String.format("User with id=%s doesn't exist", id))
                );
        shelfUserRepository.deleteById(id);
    }


    /**
     * Change status of user
     * @param firstName String represents firstname of the user
     * @param lastName String represents lastname of the user
     * @param status of user
     *
     */
    public void userChangeStatus(String firstName, String lastName, Status status) {
        ShelfUser user = shelfUserRepository.findByFirstnameAndLastname(firstName, lastName)
                .orElseThrow(
                        () -> new BookshelfResourceNotFoundException(
                                String.format("User %s %s not found", firstName, lastName))
                );
        if (user.getStatus().equals(status.name())) {
            throw new BookshelfConflictException(String.format("Status of user %s %s is already %s",
                    firstName, lastName, status.name()));
        } else {
            user.setStatus(status.name());
            shelfUserRepository.save(user);
        }
    }

    /**
     * Get a status of the specific user based by firstname and lastname
     *
     * @param firstName String represents firstname of the user
     * @param lastName  String represents lastname of the user
     * @return status of user
     */

    public String userStatusGet(String firstName, String lastName) {
        ShelfUser user = shelfUserRepository.findByFirstnameAndLastname(firstName, lastName)
                .orElseThrow(
                        () -> new BookshelfResourceNotFoundException(
                                String.format("User %s %s doesn't exist", firstName, lastName))
                );
        return user.getStatus();
    }
}
