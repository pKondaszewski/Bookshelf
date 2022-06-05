package com.globallogic.bookshelf.repository;

import com.globallogic.bookshelf.entity.Book;
import com.globallogic.bookshelf.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findByBook(Book book);
}
