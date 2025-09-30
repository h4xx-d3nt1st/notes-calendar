package com.example.notes.dto;

import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class NoteDto {
  private Long id;

  @NotNull private LocalDate date;

  @NotBlank
  @Size(max = 500)
  private String content;

  private Integer indexInDay;

  public NoteDto() {
    // default ctor for Jackson & Checkstyle
  }

  public NoteDto(Long id, LocalDate date, String content, Integer indexInDay) {
    this.id = id;
    this.date = date;
    this.content = content;
    this.indexInDay = indexInDay;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Integer getIndexInDay() {
    return indexInDay;
  }

  public void setIndexInDay(Integer indexInDay) {
    this.indexInDay = indexInDay;
  }
}
