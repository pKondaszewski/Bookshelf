package com.globallogic.bookshelf.controller;


import com.globallogic.bookshelf.client.ShelfUserFeignClient;
import com.globallogic.bookshelf.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.bookshelf.utils.Status;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shelfUser")
@RequiredArgsConstructor
public class ShelfUserController {

    private final ShelfUserFeignClient userFeignClient;

    @PostMapping("/userAdd")
    public ResponseEntity<String> userAdd(@RequestParam String firstName
            , @RequestParam String lastName
            , @RequestParam Status status){

        userFeignClient.userAdd(firstName,lastName,status);

        return new ResponseEntity<>(String.format("User %s %s created", firstName, lastName), HttpStatus.CREATED);
    }

    @GetMapping("/userStatus")
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
