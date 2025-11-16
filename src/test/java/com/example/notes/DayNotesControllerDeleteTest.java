package com.example.notes;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.notes.entity.Note;
import com.example.notes.repository.NoteRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Transactional
class DayNotesControllerDeleteTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private NoteRepository noteRepository;

  @BeforeEach
  void cleanDb() {
    noteRepository.deleteAll();
  }

  @Test
  void deleteById_shouldReturn204_andRemoveEntity() throws Exception {
    LocalDate d = LocalDate.of(2099, 1, 1);

    Note n = new Note();
    n.setDate(d);
    n.setIndexInDay(1);
    n.setContent("A");
    n = noteRepository.save(n);

    Note m = new Note();
    m.setDate(d);
    m.setIndexInDay(2);
    m.setContent("B");
    noteRepository.save(m);

    mockMvc
        .perform(delete("/api/v1/notes/" + n.getId()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteDay_shouldReturnCount_andRemoveOnlyRequestedDate() throws Exception {
    LocalDate d1 = LocalDate.of(2099, 1, 1);
    LocalDate d2 = LocalDate.of(2099, 1, 2);

    for (int i = 1; i <= 2; i++) {
      Note n = new Note();
      n.setDate(d1);
      n.setIndexInDay(i);
      n.setContent("Note " + i);
      noteRepository.save(n);
    }

    Note other = new Note();
    other.setDate(d2);
    other.setIndexInDay(1);
    other.setContent("Other");
    noteRepository.save(other);

    mockMvc
        .perform(delete("/api/v1/notes/day?date=" + d1).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()) // было isNoContent()
        .andExpect(content().string("2")); // контроллер возвращает число удалённых записей
  }
}
