package com.example.notes.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotesForDayResponse {
  private String date;
  private boolean holiday;
  private String holidayName;
  private List<NoteDto> notes;
}
