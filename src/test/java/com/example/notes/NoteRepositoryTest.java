package com.example.notes;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.notes.entity.Note;
import com.example.notes.repository.NoteRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test") // <- важная строка: берёт application-test.properties (Flyway выключен)
class NoteRepositoryTest {

  @Autowired NoteRepository repo;

  @Test
  void saveAndFindByDate() {
    Note n = new Note();
    n.setDate(LocalDate.of(2025, 10, 5));
    n.setContent("test");
    n.setIndexInDay(1);
    repo.save(n);

    List<Note> found = repo.findAllByDateOrderByIndexInDayAsc(LocalDate.of(2025, 10, 5));

    assertThat(found).hasSize(1);
    assertThat(found.get(0).getContent()).isEqualTo("test");
  }
}
