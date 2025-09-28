package com.example.notes.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "notes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private Integer indexInDay;
}
