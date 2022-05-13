package com.globallogic.bookshelf.utils;

import com.globallogic.bookshelf.entity.Borrow;

/**
 * Special class to create a user-friendly String output
 */
public class StringRepresentation {

    /**
     * Method that's create user-friendly output of completed and uncompleted borrow
     *
     * @param borrow body of the borrow
     * @return user-friendly string output for the borrow
     */
    public static String ofTheBorrow(Borrow borrow) {
        String borrowAsString;
        borrowAsString = "Author = " + borrow.getBook().getAuthor() + ", " +
                         "Title = " + borrow.getBook().getTitle() + ", " +
                         "Date of the borrow = " + borrow.getBorrowed() + ", ";
        if (borrow.getReturned() == null) {
            borrowAsString += "Date of the return = " + borrow.getReturned() + ", ";
        }
        borrowAsString += "Comment = " + borrow.getComment() + ", " +
                          "CategoryName = " + borrow.getBook().getCategory().getName();
        return borrowAsString;
    }
}
