package com.globallogic.bookshelf.controller;

import com.globallogic.bookshelf.service.LogService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
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
public class LogController implements LogInterface {

    @Autowired
    private LogService logService;

    /**
     * PUT Request to update interval time of custom log
     *
     * @param minute minute value to update log time
     * @return String information about successful log interval time update
     */
    @Override
    public ResponseEntity<String> updateIntervalTime(Integer minute) {
        logService.updateIntervalTime(minute);
        log.info("Updating time of log interval with value: {}", minute);
        return new ResponseEntity<>(String.format("Log interval time value updated with value = %s", minute), HttpStatus.OK);
    }
}
