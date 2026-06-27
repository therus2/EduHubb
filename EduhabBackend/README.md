# EduHubBackend

REST API backend for EduHub school management system.

## Stack
- Java 11, Spring Boot 2.5.5
- JPA (javax.persistence), H2 (dev), PostgreSQL 15 (prod)
- Liquibase migrations
- Lombok

## Profiles
| Profile | Database   | How to activate                     |
|---------|------------|-------------------------------------|
| `dev`   | H2 in-mem | Default                             |
| `prod`  | PostgreSQL | `SPRING_PROFILES_ACTIVE=prod`       |

## API
All endpoints are prefixed with `/api`:
```
GET    /api/user
POST   /api/user
GET    /api/user/{id}
PUT    /api/user/{id}
DELETE /api/user/{id}
... (same pattern for /student, /teacher, /group, /subject, /lesson, /grade, /attendance, /assignment, /homework-completion, /notification, /announcement, /announcement-read, /schedule-break, /user-settings)
```

## Security
JWT-аутентификация не реализована.
Все эндпоинты открыты. Не деплоить в публичный доступ без добавления Spring Security + JWT.

## Деплой на сервер (с GitHub)

На сервере нужны только **Docker** и **git**. Код копируешь с GitHub, не вручную с ПК.

```bash
# 1. Клонировать репозиторий
git clone https://github.com/therus2/EduhabBackendd.git
cd EduhabBackendd

# 2. Одна команда — PostgreSQL + Spring Boot на порту 8067
chmod +x start.sh
./start.sh
```

Готово. API: `http://<IP-сервера>:8067/api/`

Проверка:

```bash
curl http://localhost:8067/api/user
docker compose ps
```

Пароли и порт — в `.env` (при первом `./start.sh` создаётся из `.env.example`).

Без скрипта (то же самое):

```bash
git clone https://github.com/therus2/EduhabBackendd.git
cd EduhabBackendd
docker compose up -d --build
```

Обновление после `git push`:

```bash
cd EduhabBackendd
git pull
./start.sh
```

Windows (Docker Desktop): `git clone ...` → `.\deploy.ps1`

Локальная разработка без Docker для backend:
```bash
docker compose up -d postgres   # только БД
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

Windows (только БД в Docker, backend через Maven):
```powershell
.\run.ps1
```

## Тестирование

### Быстрый запуск всех тестов

```bash
mvn test
```

### Запуск конкретной категории

```bash
# Только repository тесты
mvn test -Dtest="*RepositoryTest"

# Только service тесты
mvn test -Dtest="*ServiceImplTest,*AuthServiceTest"

# Только controller тесты
mvn test -Dtest="*ControllerTest"

# Только integration тест
mvn test -Dtest="UserIntegrationTest"

# Конкретный тестовый класс
mvn test -Dtest="UserServiceImplTest"
```

### Что покрывают тесты

Тесты разделены на 4 слоя и используют H2 in-memory БД (dev-профиль):

| Слой | Технология | Что тестирует | Классы |
|---|---|---|---|
| **Repository** | `@DataJpaTest` + H2 | Кастомные запросы Spring Data (`findByEmail`, `findByGroupId`, etc.) | `UserRepositoryTest`, `GradeRepositoryTest`, `LessonRepositoryTest`, `StudentRepositoryTest` |
| **Service** | `@ExtendWith(MockitoExtension.class)` | Бизнес-логика: CRUD, кодирование пароля, `EntityNotFoundException` | `UserServiceImplTest`, `GradeServiceImplTest`, `LessonServiceImplTest`, `AuthServiceTest` |
| **Controller** | `@WebMvcTest` + `MockMvc` | REST-эндпоинты: HTTP-статусы, тело ответа, `@Valid`-валидация, query-параметры | `UserControllerTest`, `GradeControllerTest`, `LessonControllerTest`, `AuthControllerTest` |
| **Integration** | `@SpringBootTest` + `TestRestTemplate` | Полный CRUD-цикл через реальный HTTP | `UserIntegrationTest` |

### Структура тестов

```
src/test/java/com/eduhab/
├── EduHubApplicationTests.java          # Проверка контекста
├── UserIntegrationTest.java             # Интеграционный тест
├── repository/
│   ├── UserRepositoryTest.java
│   ├── GradeRepositoryTest.java
│   ├── LessonRepositoryTest.java
│   └── StudentRepositoryTest.java
├── service/
│   ├── UserServiceImplTest.java
│   ├── GradeServiceImplTest.java
│   ├── LessonServiceImplTest.java
│   └── AuthServiceTest.java
└── rest/controller/
    ├── UserControllerTest.java
    ├── GradeControllerTest.java
    ├── LessonControllerTest.java
    └── AuthControllerTest.java
```

### Примечания

- Repository тесты автоматически создают схему БД через Hibernate (`ddl-auto=create-drop`)
- Service тесты используют Mockito — БД не требуется
- Controller тесты тестируют только слой веб-адаптера (сервисы мокаются)
- Integration тест поднимает полный Spring-контекст на случайном порту
- Liquibase миграции **не выполняются** в repository-тестах (отключаются `@DataJpaTest`)
