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

###  Вариант 2 — запуск через Docker Compose

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

## Проверка API (через curl / Postman) — полный CRUD

###  Сводная матрица CRUD

| Действие | Метод | URL | Тело запроса | Ожидаемый код | Кратко |
|-----------|--------|-----|---------------|----------------|--------|
| Создать заметку | POST | /api/v1/notes | {"date":"YYYY-MM-DD","content":"..."} | 201 Created | Возвращает созданный объект |
| Список за день | GET | /api/v1/notes/day-notes?date=YYYY-MM-DD | — | 200 OK / 404 Not Found | Возвращает статус дня + массив заметок |
| Обновить заметку | PUT | /api/v1/notes/{id} | {"content":"..."} | 200 OK / 404 Not Found | Меняет текст заметки |
| Удалить по id | DELETE | /api/v1/notes/{id} | — | 204 No Content / 404 Not Found | Физическое удаление |
| Удалить день целиком | DELETE | /api/v1/notes/day?date=YYYY-MM-DD | — | 204 No Content | Сносит все заметки за дату |
| Статус дня (IsDayOff) | GET | /api/v1/holiday?date=YYYY-MM-DD | — | 200 OK | WORKDAY/HOLIDAY/SHORTDAY |

---

###  Создание заметки (C — Create)
```bash
curl -X POST http://localhost:8080/api/v1/notes \
  -H "Content-Type: application/json" \
  -d '{"date":"2025-11-12","content":"Подготовить отчёт по курсовой"}'
```
Ожидаемый ответ (201):
```json
{
  "id": 57,
  "date": "2025-11-12",
  "content": "Подготовить отчёт по курсовой",
  "indexInDay": 1
}
```

---

###  Получить заметки за дату (R — Read)
```bash
curl "http://localhost:8080/api/v1/notes/day-notes?date=2025-11-12"
```
Ожидаемый ответ (200):
```json
{
  "date": "2025-11-12",
  "holiday": false,
  "holidayKind": "WORKDAY",
  "holidayLabel": "Рабочий день",
  "holidayName": "Рабочий день",
  "notes": [
    { "id": 56, "date": "2025-11-12", "content": "Проверка метрик Prometheus", "indexInDay": 1 },
    { "id": 57, "date": "2025-11-12", "content": "Подготовить отчёт по курсовой", "indexInDay": 2 }
  ]
}
```
Если за дату нет записей → **404 Not Found**.

---

###  Обновить заметку (U — Update)
```bash
curl -X PUT http://localhost:8080/api/v1/notes/57 \
  -H "Content-Type: application/json" \
  -d '{"content":"Подготовить отчёт (финальная версия)"}'
```
Ожидаемый ответ (200):
```json
{
  "id": 57,
  "date": "2025-11-12",
  "content": "Подготовить отчёт (финальная версия)",
  "indexInDay": 2
}
```
Если id не существует → **404 Not Found**.

---

###  Удалить заметку по id (D — Delete by ID)
```bash
curl -X DELETE http://localhost:8080/api/v1/notes/57 -i
```
Ожидаемый ответ: **204 No Content** (тело пустое).  
Повторное удаление того же id вернёт **404 Not Found**.

---

###  Удалить все заметки за день (D — Delete by Date)
```bash
curl -X DELETE "http://localhost:8080/api/v1/notes/day?date=2025-11-12" -i
```
Ожидаемый ответ: **204 No Content**.  
Проверка:
```bash
curl "http://localhost:8080/api/v1/notes/day-notes?date=2025-11-12" -i
# ожидаем 404 Not Found
```

---

###  Статус дня через IsDayOff (интеграция)
```bash
curl "http://localhost:8080/api/v1/holiday?date=2025-03-07"
```
Ответ (200):
```json
{ "status": "SHORTDAY" }
```
Если внешний сервис недоступен — fallback:  
в логах:
```
WARN  HolidayService - API timeout, fallback=WORKDAY
```
в ответе — `"status": "WORKDAY"`.

---

###  Быстрый smoke-скрипт
```bash
# 1) создаём две заметки
curl -s -X POST http://localhost:8080/api/v1/notes -H "Content-Type: application/json" \
  -d '{"date":"2025-11-12","content":"Заметка A"}' | tee /tmp/noteA.json
curl -s -X POST http://localhost:8080/api/v1/notes -H "Content-Type: application/json" \
  -d '{"date":"2025-11-12","content":"Заметка B"}' | tee /tmp/noteB.json

# 2) читаем список за день
curl -s "http://localhost:8080/api/v1/notes/day-notes?date=2025-11-12"

# 3) апдейтим первую (подставляем id из вывода /tmp/noteA.json)
ID_A=$(jq -r '.id' /tmp/noteA.json)
curl -s -X PUT http://localhost:8080/api/v1/notes/$ID_A \
  -H "Content-Type: application/json" \
  -d '{"content":"Заметка A (обновлено)"}'

# 4) удаляем вторую по id
ID_B=$(jq -r '.id' /tmp/noteB.json)
curl -s -X DELETE http://localhost:8080/api/v1/notes/$ID_B -i

# 5) удаляем весь день
curl -s -X DELETE "http://localhost:8080/api/v1/notes/day?date=2025-11-12" -i

# 6) проверяем, что за день теперь 404
curl -s "http://localhost:8080/api/v1/notes/day-notes?date=2025-11-12" -i
```
Если нет **jq**, вытащи id вручную из ответа POST.

---

###  Коды ошибок и валидация
- **400 Bad Request** — пустой `content`, неверный формат `date`, отсутствует тело запроса;  
- **404 Not Found** — нет заметки с таким id, или за запрошенную дату записей нет;  
- **500 Internal Server Error** — сбой БД/сетевой таймаут (IsDayOff), детали в `logs/app.log`.


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
