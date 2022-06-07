package com.globallogic.bookshelf.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Interface for LogController
 *
 * @author Przemysław Kondaszewski
 */
public interface LogInterface {

    @PutMapping(path = "/updateIntervalTime")
    @ApiOperation(value = "Update time value of log interval")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Log interval updated"),
                            @ApiResponse(code = 500, message = "Internal Bookshelf server error")})
    ResponseEntity<String> updateIntervalTime(@RequestParam(name = "minute") Integer minute);
}
