package com.globallogic.bookshelf.utils.CustomObjects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO class for borrow entity
 */
@Getter
@Setter
@NoArgsConstructor
public class CustomBorrow {
  protected String borrowed;
  protected String firstname;
  protected String lastname;
  protected String comment;
  protected CustomBook book;
}
