package com.example.notes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.notes.dto.NoteDto;
import com.example.notes.entity.Note;
import com.example.notes.repository.NoteRepository;
import com.example.notes.service.NoteService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

public class NoteServiceTest {

  @Test
  void createAssignsIndexInDay() {
    NoteRepository repo = mock(NoteRepository.class);
    when(repo.findAllByDateOrderByIndexInDayAsc(any())).thenReturn(List.of());
    when(repo.save(any()))
        .thenAnswer(
            inv -> {
              Note n = inv.getArgument(0);
              n.setId(123L);
              return n;
            });

    NoteService svc = new NoteService(repo);
    NoteDto dto = new NoteDto(null, LocalDate.of(2025, 10, 5), "unit", null);

    NoteDto created = svc.create(dto);

    assertNotNull(created.getId());
    assertEquals(1, created.getIndexInDay());
    assertEquals("unit", created.getContent());
  }
}
