# Notes Calendar — отчёт

## 1. Цель и стек
Java 17, Spring Boot 2.7, Gradle 8.10.1, PostgreSQL, Flyway, OpenAPI (Swagger UI), Apache POI (XLSX), Lombok, Logback, CI GitHub Actions.

## 2. Архитектура
Entity Note(id, date, content, indexInDay); слой Controller → Service → Repository; DTO для API.

## 3. Функциональность
CRUD для заметок, фильтр по дате, экспорт XLSX, аналитика/статистика (план), (опц.) ML-модуль.

## 4. Качество кода
Checkstyle + Spotless; Unit/Integration tests (JUnit5, H2); JaCoCo coverage.

## 5. CI/CD
Матрица JDK 17/21; отчёты артефактами; Boot JAR как артефакт.

## 6. Библиотеки — зачем
POI (XLSX), springdoc (доки), Lombok (сокращение бойлерплейта), Flyway (миграции).
