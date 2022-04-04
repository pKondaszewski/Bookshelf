package com.globallogic.bookshelf.controller;


import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.BorrowRepository;
import com.globallogic.bookshelf.service.BorrowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Client-server communication class that's processes /category requests
 *
 * @author Bartłomiej Chojnacki
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/borrows")
@Slf4j
@Api("Management Api")
public class BorrowController {
    @Autowired
    BorrowService borrowsService;
    @Autowired
    BorrowRepository borrowsRepository;
    @Autowired
    BookRepository bookRepository;

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Borrows found", response = BorrowSO.class),
            @ApiResponse(code = 404, message = "Borrows not found"),
            @ApiResponse(code = 500, message = "Internal Borrows server error")})
    @ResponseStatus(code = HttpStatus.OK)
    public BorrowSO get(@PathVariable(name = "id") Integer id) {
        BorrowSO borrowsSO = borrowsService.get(id);
        log.info("Returning Borrows={}", borrowsSO);
        return borrowsSO;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public BorrowSO create(@RequestBody BorrowSO borrowSO) {
        return borrowsService.create(borrowSO);
    }

}
