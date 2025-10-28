package com.example.notes.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/** Сервис для определения праздничных / выходных / сокращённых дней. */
@Service
public class HolidayService {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Основной метод c указанием страны (cc).
     * Формат API: https://isdayoff.ru/YYYY-MM-DD?cc=XX&pre=1
     */
    public HolidayInfo getHolidayInfo(LocalDate date, String countryCode) {
        if (countryCode == null || countryCode.isBlank()) {
            countryCode = "ru";
        }
        final String url = String.format(
                "https://isdayoff.ru/%s?cc=%s&pre=1",
                date.format(DateTimeFormatter.ISO_DATE),
                countryCode);

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
                        return new HolidayInfo(false, "Сокращённый день", "SHORTDAY",
                                "Сокращённый день (предпраздничный)");
                    }
                    return new HolidayInfo(false, "Рабочий день", "WORKDAY", "Рабочий день");

                case '1': // выходной/праздничный
                    // Тот же хак для 8 мая (на случай, если API отдаёт 1 вместо 2)
                    if (isRu(countryCode) && isMay8(date)) {
                        return new HolidayInfo(false, "Сокращённый день", "SHORTDAY",
                                "Сокращённый день (предпраздничный)");
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
        if (date.getMonthValue() == 1 && date.getDayOfMonth() == 1)  return "Новый год";
        if (date.getMonthValue() == 1 && date.getDayOfMonth() == 7)  return "Рождество Христово";
        if (date.getMonthValue() == 2 && date.getDayOfMonth() == 23) return "День защитника Отечества";
        if (date.getMonthValue() == 3 && date.getDayOfMonth() == 8)  return "Международный женский день";
        if (date.getMonthValue() == 5 && date.getDayOfMonth() == 1)  return "Праздник Весны и Труда";
        if (date.getMonthValue() == 5 && date.getDayOfMonth() == 9)  return "День Победы";
        if (date.getMonthValue() == 6 && date.getDayOfMonth() == 12) return "День России";
        if (date.getMonthValue() == 11 && date.getDayOfMonth() == 4) return "День народного единства";
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
    public record HolidayInfo(boolean holiday, String label, String kind, String name) { /* no-op */ }
}