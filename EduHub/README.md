# EduHub — Android School Journal

Мобильное приложение для управления школьным журналом. Роли: студент (просмотр), преподаватель (редактирование). Offline-first с фоновой синхронизацией.

## Технический стек

- **Язык:** Java 100%
- **Min SDK:** 24 | **Target SDK:** 36 | **Compile SDK:** 36
- **Сборка:** Gradle (Kotlin DSL) + AGP 9.1.1
- **БД:** SQLite (SQLiteOpenHelper)
- **Сеть:** Retrofit 2.11 + OkHttp 4.12 (фоновый режим)
- **DI:** Hilt
- **Навигация:** Navigation Component + BottomNavigation
- **Фоновые задачи:** WorkManager 2.9.1

## Сборка

```bash
# Windows
gradlew.bat assembleDebug

# Linux/macOS
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## Тестовые учётные данные

| Роль | Email | Пароль |
|------|-------|--------|
| Студент | student-001@school.ru … student-036@school.ru | pass |
| Учитель | ivanov@school.ru (и ещё 9 учителей) | 123456 |
| Админ (обе роли) | admin@school.ru | 123456 |

### Список учителей

| Email | Имя |
|-------|-----|
| ivanov@school.ru | Иванов Иван Иванович |
| petrova@school.ru | Петрова Светлана Викторовна |
| sidorov@school.ru | Сидоров Алексей Михайлович |
| kuznetsova@school.ru | Кузнецова Елена Андреевна |
| morozov@school.ru | Морозов Дмитрий Сергеевич |
| belova@school.ru | Белова Ольга Николаевна |
| kozlov@school.ru | Козлов Андрей Борисович |
| grigorieva@school.ru | Григорьева Анна Владимировна |
| fedorov@school.ru | Фёдоров Павел Романович |
| semyonova@school.ru | Семёнова Ирина Дмитриевна |

### Группы студентов

| Группа | Класс |
|--------|-------|
| 7А | student-001 … student-010 |
| 9Б | student-011 … student-018 |
| 11А | student-019 … student-028 |
| ИВТ-21 | student-029 … student-036 |

## Backend

Репозиторий: `/EduhabBackend` — Spring Boot 2.5 + PostgreSQL/H2.
См. `EduhabBackend/README.md`.

## Тестирование

### Запуск тестов

```bash
# unit-тесты (на машине разработчика)
./gradlew test

# Инструментальные тесты (на устройстве/эмуляторе)
./gradlew connectedAndroidTest
```

### Что покрывают тесты

Тесты разделены на 2 категории и не требуют Android-устройства (unit-тесты запускаются на JVM).

| Категория | Пакет | Что тестирует | Классы |
|---|---|---|---|
| **Domain models** | `domains.classes` | Конструкторы, геттеры/сеттеры, валидация (`Grade` 2-5), бизнес-логика (`canSetGrades`, `isAbsent`, `isOverdue`, `getFullName`) | `StudentTest`, `TeacherTest`, `GradeTest`, `AttendanceTest`, `AssignmentTest` |
| **Utilities** | `utils` | Форматирование оценок, расчёт учебного периода | `GradeDisplayHelperTest`, `LearningPeriodHelperTest` |
| **Security** | `security` | Хеширование паролей (bcrypt) | `PasswordHasherTest` |
| **Network** | `network.sync` | JSON-сериализация очереди синхронизации | `SyncEngineTest` |
| **Network** | `network.session` | Разрешение ролей для навигации | `RoleResolverTest` |

### Структура тестов

```
app/src/test/java/com/example/eduhub/
├── ExampleUnitTest.java              # Заглушка (2+2=4)
├── domains/classes/
│   ├── StudentTest.java              # 15 тестов
│   ├── TeacherTest.java              # 14 тестов
│   ├── GradeTest.java                # 13 тестов
│   ├── AttendanceTest.java           # 11 тестов
│   └── AssignmentTest.java           # 12 тестов
├── utils/
│   ├── GradeDisplayHelperTest.java   # 11 тестов
│   └── LearningPeriodHelperTest.java # 9 тестов
├── security/
│   └── PasswordHasherTest.java       # 17 тестов
├── network/sync/
│   └── SyncEngineTest.java           # 4 теста
└── network/session/
    └── RoleResolverTest.java         # 10 тестов
```

### Примечания

- Unit-тесты используют JUnit 4 (совместимость с существующими тестами)
- Mockito и Robolectric **не используются** — тестируется только чистая Java-логика
- Для тестирования DBHelper, репозиториев и UI-компонентов требуется Android-окружение (инструментальные тесты или Robolectric)
