package com.example.notes.controller;

import com.example.notes.dto.NotesForDayResponse;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Отдельный контроллер на /api/v1 (без /notes)
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DayNotesAliasController {

  private final DayNotesController delegate;

  @GetMapping("/day-notes")
  public NotesForDayResponse dayNotes(
      @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    // делегируем в основной метод
    return delegate.getNotesForDay(date);
  }
}
