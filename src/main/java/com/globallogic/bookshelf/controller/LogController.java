package com.globallogic.bookshelf.controller;

import com.globallogic.bookshelf.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Client-server communication class that's processes /log requests
 *
 * @author Przemyslaw Kondaszewski
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/log")
@Slf4j
@Api("Management Api")
public class LogController {

    @Autowired
    private LogService logService;

    @PutMapping(path = "/updateIntervalTime")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Log interval updated"),
                            @ApiResponse(code = 500, message = "Internal Bookshelf server error")})
    @ApiOperation(value = "Update time value of log interval")
    public ResponseEntity<String> updateIntervalTime(@RequestParam(name = "minute") Integer minute) {
        logService.updateIntervalTime(minute);
        log.info("Updating time of log interval with value: {}", minute);
        return new ResponseEntity<>(String.format("Log interval time value updated with value = %s", minute), HttpStatus.OK);
    }
}
