package com.example.notes.service;

import com.example.notes.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository repository;

    @Transactional
    public void deleteAllByDate(LocalDate date) {
        repository.deleteByDate(date);
    }

    @javax.transaction.Transactional
    public void deleteDay(java.time.LocalDate date) {
        noteRepository.deleteByDate(date);
    }

}
