# Сервер Quipy

## Сборка клиента (quipy-client)

Необходимо скопировать репозиторий с исходным кодом клиентской части системы, перейти в новосозданную папку и выполнить
команду по сборке изображения `quipy-client` с тегом `latest`

```shell
docker build -t quipy-client:latest .
```

## Сборка сервера (quipy-server)

Необходимо скопировать репозиторий с исходным кодом серверной части системы, перейти в новосозданную папку, выполнить
команду по генерации jar-файла (файл должен будет находится в директории `target`) и выполнить команду по сборке
изображения `quipy-server` с тегом `latest`.

```shell
./mvnw package -DskipTests
docker build -t quipy-server:latest .
```

## Подъем системы

Находясь в директории с исходным кодом сервера выполнить команду.

```shell
docker-compose -f docker-compose-prod.yml up -d --force-recreate
```

## Обновление системы

Необходимо пересобрать образы соотвествующей части системы и выполнить команду аналогичную подъему системы.