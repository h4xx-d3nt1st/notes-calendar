package com.example.notes.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Service
@RequiredArgsConstructor
public class HolidayService {

  private final RestTemplate restTemplate;

  @Value("${isdayoff.base-url:https://isdayoff.ru}")
  private String baseUrl;

  private static final DateTimeFormatter API_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

  public HolidayInfo getHolidayInfo(LocalDate date) {
    String ymd = date.format(API_FMT);
    // cc=ru — Россия; pre=1 — учитывать предпраздничные; covid=1 — спец-коды ковидных периодов
    String url = String.format("%s/api/getdata?date=%s&cc=ru&pre=1&covid=1", baseUrl, ymd);

    try {
      ResponseEntity<String> resp = restTemplate.getForEntity(url, String.class);
      String body = resp.getBody() == null ? "" : resp.getBody().trim();
      char code = body.isEmpty() ? 'x' : body.charAt(0);
      switch (code) {
        case '0':
          return new HolidayInfo(false, "Рабочий день", "WORKING");
        case '1':
          return new HolidayInfo(true, "Праздничный/выходной", "HOLIDAY_OR_WEEKEND");
        case '2':
          return new HolidayInfo(true, "Сокращённый день (предпраздничный)", "PREHOLIDAY");
        case '4':
          return new HolidayInfo(false, "Рабочий день (особый режим)", "COVID_WORKING");
        default:
          log.warn("isdayoff: unexpected code '{}' for {}", code, ymd);
          return new HolidayInfo(false, "Неизвестно", "UNKNOWN");
      }
    } catch (Exception e) {
      log.warn("isdayoff: request failed for {}: {}", ymd, e.toString());
      return new HolidayInfo(false, "Недоступно", "UNAVAILABLE");
    }
  }

  /** Короткая DTO внутри сервиса. */
  @Getter
  @AllArgsConstructor
  public static class HolidayInfo {
    private final boolean holiday;
    private final String label;
    private final String kind;
  }
}
