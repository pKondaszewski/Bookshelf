package com.globallogic.shelfuser.controller;

import com.globallogic.shelfuser.utils.Status;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface ShelfUserInterface {


    @ApiOperation(value = "Create user.")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "User Add", response = ShelfUserController.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal ShelfUser server error")})
    @PostMapping(path = "/userAdd", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> userAdd(@RequestParam String firstName
            , @RequestParam String lastName
            , @RequestParam Status status);


    @DeleteMapping(path = "/{id}")
    @ApiOperation(value = "Delete user.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "User deleted", response = String.class),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal ShelfUser server error")})
    public ResponseEntity<String> userDelete(@PathVariable(name = "id") Integer id);


    @PutMapping
    @ApiOperation(value = "Change user status.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "User status change", response = String.class),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal ShelfUser server error")})
    public ResponseEntity<String> userChangeStatus(@RequestParam String firstName, @RequestParam String lastName, @RequestParam Status status);


    @ApiOperation(value = "Get user status.")
    @GetMapping(path = "/userStatus", produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "User status", response = String.class),
            @ApiResponse(code = 500, message = "Internal ShelfUser server error")})
    public ResponseEntity<String> getUserStatus(@RequestParam String firstName, @RequestParam String lastName);
}
