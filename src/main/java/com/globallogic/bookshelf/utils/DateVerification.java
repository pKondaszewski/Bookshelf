package com.globallogic.bookshelf.utils;

import java.util.Date;

/**
 * Class for verification if date was given or not. If not, generate and return actual date.
 */
public class DateVerification {
    public static Date checkNullDate(Date date) {
        if (date == null) {
            date = new Date();
        }
        return date;
    }
}
