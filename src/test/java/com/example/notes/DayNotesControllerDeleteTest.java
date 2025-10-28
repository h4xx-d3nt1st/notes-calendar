package com.example.notes;

import com.example.notes.entity.Note;
import com.example.notes.repository.NoteRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DayNotesControllerDeleteTest {

  @Autowired MockMvc mockMvc;
  @Autowired NoteRepository noteRepository;

  @Test
  void deleteById_shouldReturn204_andRemoveEntity() throws Exception {
    // arrange
    Note n = new Note();
    n.setDate(LocalDate.parse("2025-01-01"));
    n.setContent("to be deleted");
    n.setIndexInDay(1);
    n = noteRepository.save(n);
    Long id = n.getId();

    // act + assert
    mockMvc.perform(delete("/api/v1/notes/{id}", id))
        .andExpect(status().isNoContent());

    assertThat(noteRepository.findById(id)).isEmpty();
  }

  @Test
  void deleteDay_shouldReturnCount_andRemoveOnlyRequestedDate() throws Exception {
    // arrange
    LocalDate d1 = LocalDate.parse("2025-01-01");
    LocalDate d2 = LocalDate.parse("2025-01-02");

    for (int i = 1; i <= 3; i++) {
      Note n = new Note();
      n.setDate(d1);
      n.setContent("d1 note " + i);
      n.setIndexInDay(i);
      noteRepository.save(n);
    }

    // другая дата — не должна удалиться
    Note other = new Note();
    other.setDate(d2);
    other.setContent("keep me");
    other.setIndexInDay(1);
    noteRepository.save(other);

    // act + assert
    mockMvc.perform(delete("/api/v1/notes/day").param("date", "2025-01-01"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.date").value("2025-01-01"))
        .andExpect(jsonPath("$.deleted").value(3));

    // verify repository state
    assertThat(noteRepository.countByDate(d1)).isZero();
    assertThat(noteRepository.countByDate(d2)).isEqualTo(1);
  }
}
