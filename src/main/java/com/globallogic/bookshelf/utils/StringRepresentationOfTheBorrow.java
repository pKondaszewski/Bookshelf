package com.globallogic.bookshelf.utils;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Borrow;
import lombok.NoArgsConstructor;

/**
 * Special class to create a user-friendly output of borrow
 */
@NoArgsConstructor
public class StringRepresentationOfTheBorrow {

    /**
     * Method that's create user-friendly output of uncompleted borrow
     *
     * @param borrow body of the borrow
     * @return user-friendly string output for uncompleted borrow
     */
    public String stringRepresentationOfUncompletedBorrow(Borrow borrow) {
        return "Author = " + borrow.getBook().getAuthor() + ", " +
                "Title = " + borrow.getBook().getTitle() + ", " +
                "Comment = " + borrow.getComment() + ", " +
                "Category name = " + borrow.getBook().getCategory().getName() + ", " +
                "Date of the borrow = " + borrow.getBorrowed();
    }

    /**
     * Method that's create user-friendly output of completed borrow
     *
     * @param borrow body of the borrow
     * @return user-friendly string output for completed borrow
     */
     public String stringRepresentationOfCompletedBorrow(Borrow borrow) {
         return stringRepresentationOfUncompletedBorrow(borrow) + ", " +
                "Date of the return = " + borrow.getReturned();
     }

}
