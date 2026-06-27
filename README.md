# EduHub — Система управления школой

**EduHub** — это полноценная система для управления школьным журналом, состоящая из двух компонентов:

- **`EduhabBackend/`** — REST API сервер (Java, Spring Boot 2.5, PostgreSQL/H2)
- **`EduHub/`** — Android-клиент (Java, offline-first, фоновая синхронизация)

---

## Архитектура

```
┌──────────────────────┐       HTTP/JSON        ┌──────────────────────┐
│   Android-клиент     │ ◄─────────────────────► │   REST API Backend  │
│   (EduHub/)          │   JWT + Retrofit 2.11   │   (EduhabBackend/)  │
│                      │                         │                      │
│   SQLite (локально)  │                         │   PostgreSQL/H2     │
│   WorkManager (фон)  │                         │   Liquibase         │
└──────────────────────┘                         └──────────────────────┘
```

Android-приложение работает offline-first: данные хранятся локально в SQLite и синхронизируются с сервером в фоне через WorkManager.

---

## Быстрый старт

### 1. Запустить бэкенд

```bash
# Через Docker (рекомендуется)
git clone https://github.com/therus2/EduhabBackendd.git
cd EduhabBackendd
./start.sh

# Или локально (только БД в Docker, бэкенд через Maven)
docker compose up -d postgres
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

API будет доступен на `http://localhost:8067/api/`

### 2. Собрать Android-приложение

```bash
cd EduHub
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

### 3. Войти в приложение

| Роль | Email | Пароль |
|------|-------|--------|
| Студент | student-001@school.ru … student-036@school.ru | 123456 |
| Учитель | ivanov@school.ru (и ещё 9) | 123456 |
| Админ | admin@school.ru | 123456 |

---

## Проекты

### EduhabBackend — REST API

Spring Boot 2.5.5 приложение, предоставляющее CRUD-эндпоинты для всех сущностей школьного журнала.

**Технологии:** Java 11, Spring Boot 2.5.5, Spring Data JPA, Liquibase, Lombok, H2 (dev) / PostgreSQL 15 (prod), JWT, Docker

**Сущности:** User, Student, Teacher, StudentGroup, Subject, Lesson, Grade, Attendance, Assignment, HomeworkCompletion, Announcement, Notification, ScheduleBreak, UserSettings

**Порты:** 8067 (контекст `/api`)

**Профили:**
| Профиль | База данных | Активация |
|---------|-------------|-----------|
| `dev` | H2 in-memory | По умолчанию |
| `prod` | PostgreSQL | `SPRING_PROFILES_ACTIVE=prod` |

Подробнее: [`EduhabBackend/README.md`](./EduhabBackend/README.md)

---

### EduHub — Android-клиент

Нативное Android-приложение для студентов и преподавателей.

**Технологии:** Java, Min SDK 24, Target SDK 36, Gradle + AGP 9.1.1, SQLite, Retrofit 2.11 + OkHttp 4.12, Hilt, Navigation Component, WorkManager 2.9.1, Glide 4.16, jBCrypt

**Роли:**
- **Студент:** просмотр оценок, расписания, посещаемости, объявлений, заданий, уведомлений
- **Преподаватель:** выставление оценок, отметка посещаемости, создание заданий, управление уроками

Подробнее: [`EduHub/README.md`](./EduHub/README.md)

---

## Тестирование

| Компонент | Команда | Что тестируется |
|-----------|---------|-----------------|
| **Бэкенд** | `mvn test` | Repository (`@DataJpaTest`), Service (Mockito), Controller (`@WebMvcTest`), Integration (`@SpringBootTest`) |
| **Android** | `./gradlew test` | Domain models, утилиты, безопасность (bcrypt), JSON-сериализация, разрешение ролей |

---

## Разработка

### Требования

- **Backend:** Java 11, Maven, Docker (для PostgreSQL)
- **Android:** Android Studio, JDK 17, Gradle

### Структура репозитория

```
EduHubb/
├── EduhabBackend/          # REST API сервер
│   ├── src/                # Исходный код (Java)
│   ├── pom.xml             # Maven конфигурация
│   ├── docker-compose.yml  # Docker Compose (PostgreSQL + Backend)
│   └── Dockerfile          # Многостадийная сборка
├── EduHub/                 # Android-клиент
│   ├── app/src/            # Исходный код (Java)
│   ├── build.gradle.kts    # Gradle конфигурация
│   └── gradle/             # Version catalog
└── README.md               # Этот файл
```

---

## Лицензия

Проект разработан для учебных целей.
