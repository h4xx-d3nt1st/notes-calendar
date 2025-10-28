package com.example.notes.service;

import com.example.notes.dto.NoteDto;
import com.example.notes.entity.Note;
import com.example.notes.repository.NoteRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NoteService {

  private final NoteRepository noteRepository;

  public NoteService(NoteRepository noteRepository) {
    this.noteRepository = noteRepository;
  }

  private static NoteDto toDto(Note n) {
    if (n == null) {
      return null;
    }
    return new NoteDto(n.getId(), n.getDate(), n.getContent(), n.getIndexInDay());
  }

  private static Note fromDto(NoteDto dto) {
    if (dto == null) {
      return null;
    }
    Note n = new Note();
    n.setDate(dto.getDate());
    n.setContent(dto.getContent());
    n.setIndexInDay(dto.getIndexInDay());
    return n;
  }

  @Transactional(readOnly = true)
  public List<NoteDto> getByDate(LocalDate date) {
    return noteRepository.findAllByDateOrderByIndexInDayAsc(date).stream()
        .map(NoteService::toDto)
        .collect(Collectors.toList());
  }

  @Transactional
  public NoteDto create(NoteDto dto) {
    Integer idx = dto.getIndexInDay();
    if (idx == null) {
      idx =
          noteRepository.findAllByDateOrderByIndexInDayAsc(dto.getDate()).stream()
              .map(Note::getIndexInDay)
              .filter(Objects::nonNull)
              .max(Comparator.naturalOrder())
              .map(i -> i + 1)
              .orElse(1);
    }

    Note entity = fromDto(dto);
    entity.setIndexInDay(idx);
    entity = noteRepository.save(entity);

    // --- Metrics ---
    if (com.example.notes.metrics.MetricsConfig.notesCreatedCounter != null) {
      com.example.notes.metrics.MetricsConfig.notesCreatedCounter.increment();
    }
    if (com.example.notes.metrics.MetricsConfig.notesLengthSummary != null
        && entity.getContent() != null) {
      com.example.notes.metrics.MetricsConfig.notesLengthSummary.record(
          entity.getContent().length());
    }

    return toDto(entity);
  }

  @Transactional
  public NoteDto update(Long id, NoteDto dto) {
    Note entity =
        noteRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Note not found: id=" + id));
    entity.setDate(dto.getDate());
    entity.setContent(dto.getContent());
    if (dto.getIndexInDay() != null) {
      entity.setIndexInDay(dto.getIndexInDay());
    }
    entity = noteRepository.save(entity);
    return toDto(entity);
  }

  @Transactional
  public void delete(Long id) {
    noteRepository.deleteById(id);
  }

  @Transactional
  public int deleteDay(LocalDate date) {
    return noteRepository.deleteByDate(date);
  }
}
