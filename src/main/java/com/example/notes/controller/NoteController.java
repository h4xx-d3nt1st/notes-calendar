package com.example.notes.controller;

import com.example.notes.dto.NoteDto;
import com.example.notes.service.NoteService;
import java.time.LocalDate;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notes")
public class NoteController {

  private final NoteService noteService;

  public NoteController(NoteService noteService) {
    this.noteService = noteService;
  }

  // Список заметок за день
  @GetMapping(params = "date")
  public List<NoteDto> getByDate(
      @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    return noteService.getByDate(date); // сервис уже возвращает List<NoteDto>
  }

  // Создать заметку
  @PostMapping
  @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
  public NoteDto create(@RequestBody NoteDto req) {
    return noteService.create(req); // <- передаём DTO целиком
  }

  // Обновить заметку
  @PutMapping("/{id}")
  public NoteDto update(@PathVariable Long id, @RequestBody NoteDto req) {
    return noteService.update(id, req); // <- передаём DTO целиком
  }

  // Удалить одну заметку
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    noteService.delete(id);
    return ResponseEntity.noContent().build();
  }

  // Очистить весь день
  @DeleteMapping(params = "date")
  @Transactional
  public ResponseEntity<Void> deleteDay(
      @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    noteService.deleteDay(date);
    return ResponseEntity.noContent().build();
  }
}
