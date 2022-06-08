package com.globallogic.bookshelf.controller;


import com.globallogic.bookshelf.client.ShelfUserFeignClient;
import com.globallogic.bookshelf.utils.Status;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Client-server communication class that's processes /shelfUser requests
 *
 * @author Bartlomiej Chojnacki
 * @author Przemyslaw Kondaszewski
 * @since 1.0
**/

 @RestController
@RequestMapping("/shelfUser")
@RequiredArgsConstructor
public class ShelfUserController implements ShelfUserFeignClient{

    private final ShelfUserFeignClient userFeignClient;

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
            , @RequestParam Status status){

        userFeignClient.userAdd(firstName,lastName,status);

        return new ResponseEntity<>(String.format("User %s %s created", firstName, lastName), HttpStatus.CREATED);
    }

    /**
     * GET Request to receive string that contains user status information.
     *
     * @param firstName
     * @param lastName
     * @return String that contains user status information.
     */
    @Override
    public ResponseEntity<String> getUserStatus(@RequestParam String firstName,@RequestParam String lastName){
        String status = Status.NOT_PRESENT.name();
        try {
            ResponseEntity responseEntity= userFeignClient.getUserStatus(firstName, lastName);
               status = String.valueOf(responseEntity.getBody());

            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch (FeignException.NotFound exception) {
            return new ResponseEntity<>(status, HttpStatus.NOT_FOUND);
        }

    }

}
