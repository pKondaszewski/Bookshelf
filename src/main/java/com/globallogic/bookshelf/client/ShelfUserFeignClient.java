package com.globallogic.bookshelf.client;


import com.globallogic.bookshelf.utils.Status;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "shelfUser",url = "http://localhost:8466/shelfUser/" )
public interface ShelfUserFeignClient {

    @RequestMapping(method = RequestMethod.POST,
            value = "userAdd",
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    ResponseEntity<String> userAdd(@RequestParam String firstName
            , @RequestParam String lastName
            , @RequestParam Status status);


    @GetMapping(path = "userStatus", produces = MediaType.TEXT_PLAIN_VALUE)
    ResponseEntity<String> getUserStatus(@RequestParam String firstName, @RequestParam String lastName);
}


