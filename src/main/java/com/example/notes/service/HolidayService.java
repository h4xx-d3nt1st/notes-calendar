package com.example.notes.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/** Сервис для определения праздничных / выходных / сокращённых дней. */
@Service
public class HolidayService {

  private final RestTemplate restTemplate;

  public HolidayService() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(2_000); // 2 секунды на подключение
    factory.setReadTimeout(2_000); // 2 секунды на ответ
    this.restTemplate = new RestTemplate(factory);
  }

  /**
   * Основной метод c указанием страны (cc). Формат API:
   * https://isdayoff.ru/YYYY-MM-DD?cc=XX&amp;pre=1
   */
  public HolidayInfo getHolidayInfo(LocalDate date, String countryCode) {
    if (countryCode == null || countryCode.isBlank()) {
      countryCode = "ru";
    }
    final String url =
        String.format(
            "https://isdayoff.ru/%s?cc=%s&pre=1",
            date.format(DateTimeFormatter.ISO_DATE), countryCode);

    try {
      String body = restTemplate.getForObject(url, String.class);
      if (body == null || body.isEmpty()) {
        return unavailable();
      }

      char code = body.trim().charAt(0);
      switch (code) {
        case '0': // рабочий день
          // Хак: если API вдруг не отметило предпраздничный, а это 8 мая в РФ — считаем сокращённым
          if (isRu(countryCode) && isMay8(date)) {
            return new HolidayInfo(
                false, "Сокращённый день", "SHORTDAY", "Сокращённый день (предпраздничный)");
          }
          return new HolidayInfo(false, "Рабочий день", "WORKDAY", "Рабочий день");

        case '1': // выходной/праздничный
          // Тот же хак для 8 мая (на случай, если API отдаёт 1 вместо 2)
          if (isRu(countryCode) && isMay8(date)) {
            return new HolidayInfo(
                false, "Сокращённый день", "SHORTDAY", "Сокращённый день (предпраздничный)");
          }
          return new HolidayInfo(true, "Выходной", "HOLIDAY", getHolidayName(date));

        case '2': // сокращённый день (предпраздничный)
          return new HolidayInfo(false, "Сокращённый день", "SHORTDAY", "Сокращённый день");

        default:
          return unavailable();
      }
    } catch (Exception e) {
      // Фолбэк: определим хотя бы выходной по дню недели
      if (isWeekend(date)) {
        return new HolidayInfo(true, "Выходной", "HOLIDAY", "Выходной");
      }
      return unavailable();
    }
  }

  /** Оверлоад по умолчанию (RU). */
  public HolidayInfo getHolidayInfo(LocalDate date) {
    return getHolidayInfo(date, "ru");
  }

  /** Локальное имя праздника для РФ (фолбэк). */
  private String getHolidayName(LocalDate date) {
    int month = date.getMonthValue();
    int day = date.getDayOfMonth();

    if (month == 1 && day == 1) {
      return "Новый год";
    }
    if (month == 1 && day == 7) {
      return "Рождество Христово";
    }
    if (month == 2 && day == 23) {
      return "День защитника Отечества";
    }
    if (month == 3 && day == 8) {
      return "Международный женский день";
    }
    if (month == 5 && day == 1) {
      return "Праздник Весны и Труда";
    }
    if (month == 5 && day == 9) {
      return "День Победы";
    }
    if (month == 6 && day == 12) {
      return "День России";
    }
    if (month == 11 && day == 4) {
      return "День народного единства";
    }
    return "Рабочий день";
  }

  private static boolean isWeekend(LocalDate d) {
    DayOfWeek w = d.getDayOfWeek();
    return w == DayOfWeek.SATURDAY || w == DayOfWeek.SUNDAY;
  }

  private static boolean isMay8(LocalDate d) {
    return d.getMonthValue() == 5 && d.getDayOfMonth() == 8;
  }

  private static boolean isRu(String cc) {
    return "ru".equalsIgnoreCase(cc);
  }

  private static HolidayInfo unavailable() {
    return new HolidayInfo(false, "Недоступно", "UNAVAILABLE", "Недоступно");
  }

  /** DTO. Пробелы в { } — для Checkstyle. */
  public record HolidayInfo(boolean holiday, String label, String kind, String name) {
    /* no-op */
  }
}
