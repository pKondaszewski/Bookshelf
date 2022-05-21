package com.globallogic.bookshelf.utils.CustomObjects;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO class for book entity
 */
@Getter
@Setter
public class CustomBook {
    protected String author;
    protected String title;
    protected CustomCategory category;
}
