# Request System

Backend-система для заведения заявок пользователями с хранением медиа-файлов в S3-совместимом хранилище MinIO.

## Что реализовано

- REST API для регистрации, входа, создания и просмотра заявок.
- JWT-аутентификация через Spring Security.
- Пароли пользователей хранятся в BCrypt-хеше.
- Заявки и сведения о файлах сохраняются в PostgreSQL.
- Сами файлы сохраняются в MinIO.
- Пользователь видит и скачивает только свои заявки и файлы.
- OpenAPI/Swagger UI доступен по адресу `/swagger-ui/index.html`.
- У заявки есть категория: `TECHNICAL`, `DOCUMENTS` или `OTHER`.

## Запуск

Нужен установленный Docker.

```bash
docker compose up --build
```

После запуска:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- MinIO Console: `http://localhost:9101`
- MinIO login/password: `minioadmin` / `minioadmin`

## Основные endpoint'ы

### Регистрация

```bash
curl -X POST http://localhost:8080/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"ivan\",\"password\":\"password123\"}"
```

Ответ содержит JWT:

```json
{
  "token": "jwt-token",
  "username": "ivan"
}
```

### Вход

```bash
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"ivan\",\"password\":\"password123\"}"
```

### Создание заявки с файлом

Вставьте полученный токен вместо `TOKEN`.

```bash
curl -X POST http://localhost:8080/api/applications ^
  -H "Authorization: Bearer TOKEN" ^
  -F "title=Проблема с документом" ^
  -F "category=DOCUMENTS" ^
  -F "description=Нужно проверить прикрепленный файл" ^
  -F "files=@C:\path\to\file.jpg"
```

Можно передать несколько файлов, повторив параметр `files`.

### Просмотр своих заявок

```bash
curl http://localhost:8080/api/applications ^
  -H "Authorization: Bearer TOKEN"
```

### Просмотр одной заявки

```bash
curl http://localhost:8080/api/applications/1 ^
  -H "Authorization: Bearer TOKEN"
```

### Скачивание файла заявки

Ссылка приходит в поле `downloadUrl` у файла заявки.

```bash
curl http://localhost:8080/api/applications/1/files/1 ^
  -H "Authorization: Bearer TOKEN" ^
  -o file.jpg
```

## Локальный запуск без Docker для приложения

PostgreSQL и MinIO должны быть уже запущены. Настройки можно передать переменными окружения:

- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- `MINIO_ENDPOINT`, `MINIO_ACCESS_KEY`, `MINIO_SECRET_KEY`, `MINIO_BUCKET`
- `APP_JWT_SECRET`, `APP_JWT_EXPIRATION_MS`

```bash
mvn spring-boot:run
```
