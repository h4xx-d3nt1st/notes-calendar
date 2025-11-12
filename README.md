#  Notes Calendar

> REST-сервис для ведения заметок с интеграцией API [IsDayOff.ru](https://isdayoff.ru).  
> Позволяет создавать, удалять и просматривать заметки по датам, определяя тип дня (рабочий / праздничный / сокращённый).

---

## Технологический стек

| Компонент | Назначение |
|------------|------------|
| **Java 17** | язык разработки |
| **Spring Boot 2.7** | основной веб-фреймворк |
| **Gradle 8.10.1** | система сборки |
| **PostgreSQL 15** | база данных |
| **Spring Data JPA** | ORM и работа с БД |
| **Flyway** | миграции БД |
| **Lombok** | генерация кода |
| **Logback / Log4j2** | логирование |
| **Apache POI** | экспорт заметок в Excel |
| **Swagger / OpenAPI** | документация API |
| **Prometheus + Grafana** | метрики и мониторинг (опц.) |

---

##  Быстрый старт

###  Вариант 1 — локальный запуск (без Docker)

#### 1. Установите зависимости:
- Java 17+  
- PostgreSQL 15+  
- Gradle (если не встроен)

#### 2. Создайте базу данных:
```bash
psql -U postgres
CREATE DATABASE notesdb;
```

#### 3. Настройте подключение
Отредактируйте `src/main/resources/application.yml` или `.env`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/notesdb
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: validate
```

#### 4. Запустите приложение
```bash
./gradlew bootRun
```
или соберите jar:
```bash
./gradlew clean build
java -jar build/libs/notes-calendar-*.jar
```

#### 5. Проверьте работу:
Откройте в браузере:
```
http://localhost:8080/swagger-ui.html
```
или выполните:
```bash
curl "http://localhost:8080/api/v1/notes/day-notes?date=2025-11-12"
```

---

### 🐳 Вариант 2 — запуск через Docker Compose

#### 1. Склонируйте репозиторий:
```bash
git clone https://github.com/h4xx-d3nt1st/notes-calendar.git
cd notes-calendar
```

#### 2. Подготовьте `.env` (пример):
```env
POSTGRES_DB=notesdb
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/notesdb
```

#### 3. Поднимите контейнеры:
```bash
docker-compose up -d
```

#### 4. Проверка:
Приложение будет доступно по адресу:
```
http://localhost:8080
```

Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

PostgreSQL:
```
localhost:5432 (user=postgres, pass=postgres)
```

Grafana (если включена):
```
http://localhost:3000 (login: admin / admin)
```

---

##  Проверка API (через curl / Postman)

###  Создание заметки
```bash
curl -X POST http://localhost:8080/api/v1/notes   -H "Content-Type: application/json"   -d '{"date":"2025-11-12","content":"Проверка работы Notes Calendar"}'
```

###  Получение заметок за день
```bash
curl "http://localhost:8080/api/v1/notes/day-notes?date=2025-11-12"
```

Пример ответа:
```json
{
  "date": "2025-11-12",
  "holiday": false,
  "holidayKind": "WORKDAY",
  "notes": [
    {"id": 56, "content": "Проверка работы Notes Calendar"}
  ]
}
```

###  Обновление заметки
```bash
curl -X PUT http://localhost:8080/api/v1/notes/56   -H "Content-Type: application/json"   -d '{"content":"Изменённая заметка"}'
```

###  Удаление
```bash
curl -X DELETE http://localhost:8080/api/v1/notes/56
```

---

##  Основные эндпоинты

| Метод | URL | Назначение |
|-------|-----|------------|
| POST | `/api/v1/notes` | создать новую заметку |
| GET | `/api/v1/notes/day-notes?date=` | получить заметки за дату |
| PUT | `/api/v1/notes/{id}` | обновить заметку |
| DELETE | `/api/v1/notes/{id}` | удалить одну заметку |
| DELETE | `/api/v1/notes/day?date=` | удалить все заметки за день |
| GET | `/api/v1/holiday?date=` | статус дня через IsDayOff |
| GET | `/api/v1/notes/export?format=xls` | экспорт заметок в Excel |

---

##  Мониторинг и метрики

### Actuator / Prometheus
```
http://localhost:8080/actuator/health
http://localhost:8080/actuator/prometheus
```

Проверка через терминал:
```bash
curl -s http://localhost:8080/actuator/prometheus | grep http_server_requests_seconds_count | head
```

### Grafana (опционально)
```
http://localhost:3000
login: admin
password: admin
```

JSON-дашборд доступен в `/grafana/dashboard.json`.

---

##  Полезные команды

| Команда | Назначение |
|----------|------------|
| `./gradlew clean build` | собрать проект |
| `./gradlew bootRun` | запустить приложение |
| `docker-compose up -d` | поднять контейнеры |
| `docker-compose logs -f app` | просмотреть логи приложения |
| `./gradlew test` | запустить юнит-тесты |
| `./gradlew javadoc` | сгенерировать документацию |

---

##  Логирование
Логи сохраняются в:
```
logs/app.log
```
Пример сообщений:
```
INFO  Created new note: 2025-11-12 -> Проверка
WARN  HolidayService - API timeout, fallback=WORKDAY
ERROR Database connection lost
```

---

##  Архитектура проекта

```
src/
├── main/
│   ├── java/com/example/notescalendar/
│   │   ├── controller/   → REST-контроллеры
│   │   ├── service/      → бизнес-логика
│   │   ├── repository/   → доступ к PostgreSQL
│   │   ├── entity/       → модели данных
│   │   └── NotesCalendarApplication.java
│   └── resources/
│       ├── application.yml
│       ├── db/migration/
│       ├── log4j2.xml
│       └── static/
└── test/
    └── java/             → JUnit-тесты
```

---

##  Автор

**Михаил Резцов (ИКБ-41)**  
Кафедра Информационной Безопасности, СПбГУТ  
📎 [GitHub: h4xx-d3nt1st](https://github.com/h4xx-d3nt1st/notes-calendar)

---
