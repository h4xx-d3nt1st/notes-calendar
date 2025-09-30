package com.example.notes.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HolidayService {
  private static final String API_URL = "https://isdayoff.ru/{date}?cc=ru";
  private final RestTemplate restTemplate = new RestTemplate();

  public boolean isHoliday(LocalDate date) {
    String formatted = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    String response = restTemplate.getForObject(API_URL, String.class, formatted);
    return "1".equals(response);
  }
}
