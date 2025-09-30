package com.example.notes.repository;

import com.example.notes.entity.Note;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
  List<Note> findAllByDateOrderByIndexInDayAsc(LocalDate date);

  long deleteByDate(LocalDate date);

  long countByDate(LocalDate date);

  java.util.List<com.example.notes.entity.Note> findAllByDateBetweenOrderByDateAscIndexInDayAsc(
      java.time.LocalDate from, java.time.LocalDate to);
}
