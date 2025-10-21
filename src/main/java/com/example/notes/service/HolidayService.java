package com.example.notes.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/** Сервис для определения праздничных и выходных дней. */
@Service
public class HolidayService {

  private final RestTemplate restTemplate = new RestTemplate();

  /**
   * Основной метод c указанием страны (cc). Использует формат API
   * https://isdayoff.ru/YYYY-MM-DD?cc=XX
   */
  public HolidayInfo getHolidayInfo(LocalDate date, String countryCode) {
    if (countryCode == null || countryCode.isBlank()) {
      countryCode = "ru";
    }
    try {
      String url =
          String.format(
              "https://isdayoff.ru/%s?cc=%s", date.format(DateTimeFormatter.ISO_DATE), countryCode);

      String body = restTemplate.getForObject(url, String.class);
      if (body == null || body.isEmpty()) {
        return new HolidayInfo(false, "Недоступно", "UNAVAILABLE", "Недоступно");
      }

      char code = body.trim().charAt(0);
      switch (code) {
        case '0':
          return new HolidayInfo(false, "Рабочий день", "WORKDAY", "Рабочий день");
        case '1':
          return new HolidayInfo(true, "Выходной", "HOLIDAY", getHolidayName(date));
        case '2':
          return new HolidayInfo(false, "Сокращённый день", "SHORTDAY", "Сокращённый день");
        default:
          return new HolidayInfo(false, "Недоступно", "UNAVAILABLE", "Недоступно");
      }
    } catch (Exception e) {
      return new HolidayInfo(false, "Недоступно", "UNAVAILABLE", "Недоступно");
    }
  }

  /** Оверлоад по-умолчанию (ru). */
  public HolidayInfo getHolidayInfo(LocalDate date) {
    return getHolidayInfo(date, "ru");
  }

  /** Возвращает название праздника по дате (локальный фолбэк). */
  private String getHolidayName(LocalDate date) {
    if (date.getMonthValue() == 1 && date.getDayOfMonth() == 1) {
      return "Новый год";
    }
    if (date.getMonthValue() == 1 && date.getDayOfMonth() == 7) {
      return "Рождество Христово";
    }
    if (date.getMonthValue() == 2 && date.getDayOfMonth() == 23) {
      return "День защитника Отечества";
    }
    if (date.getMonthValue() == 3 && date.getDayOfMonth() == 8) {
      return "Международный женский день";
    }
    if (date.getMonthValue() == 5 && date.getDayOfMonth() == 1) {
      return "Праздник Весны и Труда";
    }
    if (date.getMonthValue() == 5 && date.getDayOfMonth() == 9) {
      return "День Победы";
    }
    if (date.getMonthValue() == 6 && date.getDayOfMonth() == 12) {
      return "День России";
    }
    if (date.getMonthValue() == 11 && date.getDayOfMonth() == 4) {
      return "День народного единства";
    }
    return "Рабочий день";
  }

  /** DTO внутри сервиса. Требуются пробелы внутри { } для Checkstyle. */
  public record HolidayInfo(boolean holiday, String label, String kind, String name) {
    /* no-op */
  }
}
