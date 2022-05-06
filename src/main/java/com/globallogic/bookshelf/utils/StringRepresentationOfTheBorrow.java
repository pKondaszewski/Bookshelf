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
     * @param book book body to get author, title, category name
     * @param borrow borrow body to get comment and date of the borrow
     * @return user-friendly string output for uncompleted borrow
     */
    public String stringRepresentationOfUncompletedBorrow(Book book, Borrow borrow) {
        return "Author = " + book.getAuthor() + ", " +
                "Title = " + book.getTitle() + ", " +
                "Comment = " + borrow.getComment() + ", " +
                "Category name = " + book.getCategory().getName() + ", " +
                "Date of the borrow = " + borrow.getBorrowed();
    }

    /**
     * Method that's create user-friendly output of completed borrow
     *
     * @param book
     * @param borrow borrow body to get date of the return
     * @return user-friendly string output for completed borrow
     */
     public String stringRepresentationOfCompletedBorrow(Book book, Borrow borrow) {
         return stringRepresentationOfUncompletedBorrow(book, borrow) + ", " +
                "Date of the return = " + borrow.getReturned();
     }

}
