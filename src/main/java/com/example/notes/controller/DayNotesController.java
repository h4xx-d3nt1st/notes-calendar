package com.example.notes.controller;

import com.example.notes.dto.NoteDto;
import com.example.notes.dto.NotesForDayResponse;
import com.example.notes.entity.Note;
import com.example.notes.repository.NoteRepository;
import com.example.notes.service.HolidayService;
import com.example.notes.service.HolidayService.HolidayInfo;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
public class DayNotesController {

  private final NoteRepository noteRepository;
  private final HolidayService holidayService;

  // Новый эндпоинт: cc опциональный (по умолчанию ru)
  // GET /api/v1/notes/day-notes?date=YYYY-MM-DD[&cc=ru]
  @GetMapping("/day-notes")
  public NotesForDayResponse getNotesForDay(
      @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @RequestParam(value = "cc", required = false, defaultValue = "ru") String countryCode) {

    List<Note> entities = noteRepository.findAllByDateOrderByIndexInDayAsc(date);

    List<NoteDto> notes =
        entities.stream()
            .map(
                n -> {
                  NoteDto dto = new NoteDto();
                  dto.setId(n.getId());
                  dto.setDate(n.getDate());
                  dto.setContent(n.getContent());
                  dto.setIndexInDay(n.getIndexInDay());
                  return dto;
                })
            .collect(Collectors.toList());

    HolidayInfo hi = holidayService.getHolidayInfo(date, countryCode);

    return NotesForDayResponse.builder()
        .date(date.toString())
        .holiday(hi.holiday())
        .holidayLabel(hi.label())
        .holidayKind(hi.kind())
        .holidayName(hi.name())
        .notes(notes)
        .build();
  }

  // Старый эндпоинт для совместимости: всегда ru
  // GET /api/v1/notes/day?date=YYYY-MM-DD
  @Deprecated
  @GetMapping("/day")
  public NotesForDayResponse getNotesForDay(
      @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    return getNotesForDay(date, "ru");
  }
}
