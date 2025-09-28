package com.example.notes.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotesForDayResponse {
    private String date;
    private boolean holiday;
    private String holidayName;
    private List<NoteDto> notes;
}
