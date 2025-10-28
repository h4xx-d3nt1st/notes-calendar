package com.example.notes.repository;

import com.example.notes.entity.Note;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface NoteRepository extends JpaRepository<Note, Long> {

  List<Note> findAllByDateOrderByIndexInDayAsc(LocalDate date);

  long countByDate(LocalDate date);

  List<Note> findAllByDateBetweenOrderByDateAscIndexInDayAsc(LocalDate from, LocalDate to);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Transactional
  @Query("delete from Note n where n.date = :date")
  int deleteByDate(@Param("date") LocalDate date);
}
