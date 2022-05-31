package com.globallogic.bookshelf.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class Log {
    @Id
    private Integer id;
    private Integer interval;
}
