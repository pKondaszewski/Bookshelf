package com.globallogic.bookshelf.repository;

import com.globallogic.bookshelf.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowRepository extends JpaRepository<Borrow, Integer> {


}
