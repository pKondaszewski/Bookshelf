package com.globallogic.bookshelf.repository;

import com.globallogic.bookshelf.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Integer> {
}
