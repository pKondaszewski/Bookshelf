package com.globallogic.bookshelf.service;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Log;
import com.globallogic.bookshelf.repository.BookRepository;
import com.globallogic.bookshelf.repository.LogRepository;
import com.globallogic.bookshelf.utils.StringRepresentation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;


/**
 * Custom logger class with time interval setup
 * @author Przemyslaw Kondaszewski
 */
@Slf4j
@Component
public class LogService {

    private static final String fileName = "bookshelf.log";
    private final BookRepository bookRepository;
    private final LogRepository logRepository;

    public LogService(BookRepository bookRepository, LogRepository logRepository) {
        this.bookRepository = bookRepository;
        this.logRepository = logRepository;
    }

    public Integer getIntervalTime() {
        return logRepository.findById(1).get().getInterval();
    }

    public void updateIntervalTime(Integer minute) {
        Log log = logRepository.getById(1);
        log.setInterval(minute);
        logRepository.save(log);
    }

    public void writeLog() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            Instant instant = Instant.now();
            String timeInUTC = instant.atZone(ZoneOffset.UTC).toString();
            int numberOfAvailableBooks = bookRepository.findAllByAvailable(true).size();
            int numberOfUnavailableBooks = bookRepository.findAllByAvailable(false).size();
            ArrayList<String> titlesOfAvailableBooks = new ArrayList<>();
            for (Book book : bookRepository.findAllByAvailable(true)) {
                titlesOfAvailableBooks.add(book.getTitle());
            }
            String logEntry = StringRepresentation.ofTheLog(timeInUTC, numberOfAvailableBooks,
                    numberOfUnavailableBooks, titlesOfAvailableBooks);
            writer.append(logEntry);
            writer.close();
        } catch (IOException exception) {
            log.error("Error occurred during writing data into the log file.");
        }
    }
}