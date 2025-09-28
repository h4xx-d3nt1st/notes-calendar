package com.example.notes.dto;

import com.example.notes.entity.Note;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NoteDto {
    private Long id;
    private LocalDate date;
    private String content;
    private Integer indexInDay;

    public static NoteDto fromEntity(Note note) {
        return NoteDto.builder()
                .id(note.getId())
                .date(note.getDate())
                .content(note.getContent())
                .indexInDay(note.getIndexInDay())
                .build();
    }
}
