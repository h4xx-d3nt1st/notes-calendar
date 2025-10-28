package com.example.notes.repository;

import com.example.notes.entity.Note;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findAllByDateOrderByIndexInDayAsc(LocalDate date);

    long deleteByDate(java.time.LocalDate date);   // добавлено: удаляет все заметки за конкретный день

    long countByDate(LocalDate date);

    List<Note> findAllByDateBetweenOrderByDateAscIndexInDayAsc(
            LocalDate from, LocalDate to);
}
