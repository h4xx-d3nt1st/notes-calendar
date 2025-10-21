package com.example.notes.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO для ответа на запрос заметок за конкретный день. Добавлены флаги праздника по данным
 * isdayoff.ru.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotesForDayResponse {

  /** Дата в формате YYYY-MM-DD */
  private String date;

  /** Является ли день праздничным или выходным */
  private boolean holiday;

  /**
   * Краткое текстовое описание (например "Государственный праздник", "Выходной", "Рабочий день")
   */
  private String holidayLabel;

  /** Исходный тип дня из API isdayoff: HOLIDAY, WEEKEND, WORKING, PREHOLIDAY и т.п. */
  private String holidayKind;

  /** Для совместимости — если где-то уже используется старое поле holidayName */
  private String holidayName;

  /** Список заметок на день */
  private List<NoteDto> notes;
}
