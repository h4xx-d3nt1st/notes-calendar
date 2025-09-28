package com.example.notes.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "notes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private String content;

    private Integer indexInDay;
}
