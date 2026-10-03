_Last Updated : October 03, 2026_

# Deployment

> [!IMPORTANT]
>
> This guide assumes you already have a working PostgreSQL and Redis/Valkey instance or know how to set up one.

## Step 1: Setting up the API Service

Follow this [guide](https://github.com/eggy03/PaperTrail-API-Quarkus?tab=readme-ov-file)

## Step 2: Setting up the Bot In Discord

Log on to the [Discord Developer Portal](https://discord.com/developers/applications) and create an application.

The application can have any name, avatar, banner and description but the following scopes, permissions and intents are
needed for it to work properly:

**Installation Contexts**

1) Guild Install

**Scopes**

1) applications.commands
2) bot

**Permissions**

1) Manage Server
2) Read Message History
3) Send Messages
4) Send Messages In Threads
5) View Audit Log
6) View Channels

**Privileged Gateway Intents**

1) Presence Intent
2) Server Members Intent
3) Message Content Intent

Note down the `BOT TOKEN` since it will be shown only once and will be required in the later steps.

## Step 3: Deploying the Bot

### 3.1: Get Required Variables

| Variable         | Description                                               | Default Value    | Optional |
|------------------|-----------------------------------------------------------|------------------|----------|
| `DB_HOST`        | PostgreSQL database host/address                          | No Default Value | No       |
| `DB_PORT`        | PostgreSQL database port                                  | No Default Value | No       |
| `DB_NAME`        | PostgreSQL database name                                  | No Default Value | No       |
| `DB_USERNAME`    | PostgreSQL database username                              | No Default Value | No       |
| `DB_PASSWORD`    | PostgreSQL database password                              | No Default Value | No       |
| `REDIS_USERNAME` | Redis/Valkey username                                     | No Default Value | No       |
| `REDIS_PASSWORD` | Redis/Valkey password                                     | No Default Value | No       |
| `REDIS_HOST`     | Redis/Valkey server host/address                          | No Default Value | No       |
| `REDIS_PORT`     | Redis/Valkey server port                                  | No Default Value | No       |
| `TOKEN`          | Discord application bot token (from the Developer Portal) | No Default Value | No       |

### 3.2: Deployment

Use the following docker compose setup to deploy both the bot and the API

```yaml
networks:
  papertrail-network:

services:
  api:
    container_name: papertrail-api
    image: ghcr.io/eggy03/papertrail-api-native:latest
    environment:
      DB_URL: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
      DB_USERNAME: ${DB_USERNAME}
      DB_PASSWORD: ${DB_PASSWORD}
      REDIS_URL: rediss://${REDIS_USERNAME}:${REDIS_PASSWORD}@${REDIS_HOST}:${REDIS_PORT}
    healthcheck:
      test: [ "CMD", "curl", "-f", "http://localhost:9000/q/health" ]
      interval: 10s
      timeout: 5s
      retries: 3

  bot:
    container_name: papertrail-bot
    image: ghcr.io/eggy03/papertrail-bot-native:latest
    depends_on:
      api:
        condition: service_healthy
    environment:
      TOKEN: ${TOKEN}
      API_URL: http://papertrail-api:8080
    healthcheck:
      test: [ "CMD", "curl", "-f", "http://localhost:9000/q/health" ]
      interval: 10s
      timeout: 5s
      retries: 3
```

If you want to build from source and then deploy individually,
you can point your builder to this [and the API] repository's
[Dockerfile](/Dockerfile) or [Dockerfile Native (GraalVM Native Image)](/Dockerfile.native).

# Customization Options

The following environment variables have defaults, but you can change them to allow for more customization:

## Bot

| Environment Variable          | Description                                                | Default Value             |
|-------------------------------|------------------------------------------------------------|---------------------------|
| `APP_NAME`                    | Changes the application name used internally.              | `PaperTrailBot`           |
| `APP_ACTIVITY`                | Changes the activity displayed by the bot in Discord.      | `/help \| latest version` |
| `APP_LOG_LEVEL`               | Changes the application log level.                         | `INFO`                    |
| `PORT`                        | Changes the port used by the application.                  | `8080`                    |
| `MANAGEMENT_PORT`             | Changes the management port used by Quarkus health checks. | `9000`                    |
| `EMBED_SUCCESS_COLOR_INT`     | Changes the color of embeds for **creation events**.       | `GREEN (712458)`          |
| `EMBED_WARNING_COLOR_INT`     | Changes the color of embeds for **update events**.         | `YELLOW (16776960)`       |
| `EMBED_DESTRUCTIVE_COLOR_INT` | Changes the color of embeds for **deletion events**.       | `RED (16711680)`          |

## API

| Environment Variable | Description                                                | Default Value |
|----------------------|------------------------------------------------------------|---------------|
| `LOG_LEVEL`          | Changes the application log level.                         | `INFO`        |
| `PORT`               | Changes the port used by the application.                  | `8080`        |
| `MANAGEMENT_PORT`    | Changes the management port used by Quarkus health checks. | `9000`        |