package com.globallogic.shelfuser.controller;



import com.globallogic.shelfuser.exeptions.BookshelfConflictException;
import com.globallogic.shelfuser.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.shelfuser.repository.ShelfUserRepository;
import com.globallogic.shelfuser.service.ShelfUserService;
import com.globallogic.shelfuser.utils.Status;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Client-server communication class that's processes /shelfUser requests
 *
 * @author Bartłomiej Chojnacki
 * @since 1.0
 */

@RestController
@RequestMapping(value = "/shelfUser")
@Slf4j
@Api("Management Api")
public class ShelfUserController implements ShelfUserInterface {


    @Autowired
    private ShelfUserRepository shelfUserRepository;
    @Autowired
    private ShelfUserService shelfUserService;


    /**
     * POST Request to create a User
     *
     * @param firstName
     * @param lastName
     * @param status
     * @return ResponseEntity that contains information about created user.
     */
    @Override
    public ResponseEntity<String> userAdd(@RequestParam String firstName
            , @RequestParam String lastName
            , @RequestParam Status status) {
        shelfUserService.userAdd(firstName, lastName, status);
        return new ResponseEntity<>(String.format("User %s %s created", firstName, lastName), HttpStatus.CREATED);
    }


    /**
     * DELETE Request to remove one user based on the id.
     *
     * @param id id of the book
     * @return ResponseEntity that informs about the removal of the user
     */
    @Override
    public ResponseEntity<String> userDelete(@PathVariable(name = "id") Integer id) {
        try {
            shelfUserService.userDelete(id);
            return new ResponseEntity<>(String.format("User with id=%d delete", id), HttpStatus.OK);
        } catch (BookshelfResourceNotFoundException exception) {
            return new ResponseEntity<>(String.format("User with id=%d doesn't exist", id), HttpStatus.NOT_FOUND);
        }
    }


    /**
     * Put Request to change user status
     *
     * @param status    ACTIVE,INACTIVE,NOT_PRESENT
     * @param firstName of user
     * @param lastName  of user
     * @return ResponseEntity that informs about change of user status
     */
    @Override
    public ResponseEntity<String> userChangeStatus(@RequestParam String firstName, @RequestParam String lastName, @RequestParam Status status) {
        try {
            shelfUserService.userChangeStatus(firstName, lastName, status);
            return new ResponseEntity<>(String.format("Status of user %s %s has been changed to %s",
                    firstName, lastName, status), HttpStatus.OK);
        } catch (BookshelfResourceNotFoundException exception) {
            return new ResponseEntity<>(String.format("User %s %s doesn't exist", firstName, lastName), HttpStatus.NOT_FOUND);
        } catch (BookshelfConflictException exception) {
            return new ResponseEntity<>(String.format("Status of user %s %s is already %s",
                    firstName, lastName, status.name()), HttpStatus.CONFLICT);
        }

    }

    /**
     * GET Request to receive string that contains user status information.
     *
     * @param firstName
     * @param lastName
     * @return String that contains user status information.
     */
    @Override
    public ResponseEntity<String> getUserStatus(@RequestParam String firstName, @RequestParam String lastName) {

        try {
            String status = shelfUserService.userStatusGet(firstName, lastName);
            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch (BookshelfResourceNotFoundException exception) {
            return new ResponseEntity<>(String.format(String.valueOf(Status.NOT_PRESENT)), HttpStatus.NOT_FOUND);
        }
    }


}
