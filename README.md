# Overview

PaperTrail is a free and open-source, self-hostable, and privacy-friendly Discord bot
designed to provide audit log data directly in a channel without requiring users to navigate server settings each time.

Key Features:

- Full audit log integration (supports over 72 event types) and generic support for unknown types
- Message logging  (edit, delete)
- Member activity tracking (joins, leaves, kicks, bans, updates)
- Voice activity logging (join/leave, move)
- Auto-deletion of logged messages after 30 days

> Get it from here: https://discord.com/discovery/applications/1381658412550590475

# Repositories

| Repository                                                                            | Description                              |
|---------------------------------------------------------------------------------------|------------------------------------------|
| [PaperTrailBot](https://github.com/eggy03/PaperTrailBot)                              | Core bot application                     |
| [PaperTrail SDK](https://github.com/eggy03/papertrail-sdk)                            | Client library for the API               |
| [PaperTrail Persistence Service](https://github.com/eggy03/PaperTrail-PersistenceAPI) | Backend API for providing CRUD functions |

> [!IMPORTANT]
> PaperTrail is currently in maintenance mode. Existing bugs will be fixed, dependency updates will be provided
> but large new features will likely not be added.

# Self-Hosting Guide

> [!IMPORTANT]
> Please note that this is only for advanced users who want to self-host this bot
>
> Set up the API Service before setting up the bot

## Setting up the API Service

Follow this [guide](https://github.com/Egg-03/PaperTrail-PersistenceAPI?tab=readme-ov-file#papertrail-persistenceapi)

## Setting up the Bot Service

### Step 1: Create an application in the Developer Portal

Log on to the [Discord Developer Portal](https://discord.com/developers/applications) and create an application.

The application can have any name, avatar, banner and description but the following scopes, permissions and intents are
needed
for it to work properly:

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

Don't forget to copy the `bot token` as it will be required in the next step

### Step 2: Get Required Secrets

You will need the following environment variables to run the bot:

| Variable       | Description                                                           |
|----------------|-----------------------------------------------------------------------|
| `TOKEN`        | Discord application bot token (from the Developer Portal)             |
| `API_URL`      | Internal URL of the Persistence API (e.g., `http://persistence:8080`) |
| `TOTAL_SHARDS` | The total number of shards (connections) your bot is using overall.   |
| `MIN_SHARD_ID` | The first shard number this bot instance should handle.               |
| `MAX_SHARD_ID` | The last shard number this bot instance should handle.                |

Each shard allows handling up-to 2500 guilds.

Take a look at the following configuration examples to have a clearer picture of what values to put
for your use-case

**1: JVM Instance / 1-2500 Guilds / 1 Shard**

If your bot is small or self-hosted for a limited number of servers, you only need one shard.
This should be sufficient for the majority of self-host users

```dotenv
TOTAL_SHARDS=1
MIN_SHARD_ID=0
MAX_SHARD_ID=0
```

**2: JVM Instance /2500-5000 Guilds / 2 Shards**

If your bot is in more than 2,500 servers, but you are still running a single JVM instance, you can increase the total
shard count:

```dotenv
TOTAL_SHARDS=2
MIN_SHARD_ID=0
MAX_SHARD_ID=1
```

Remember that each shard can only handle up to 2500 guilds so plan the total number shards accordingly

**3: JVM Instances / 25000 Guilds / 10 Shards**

If you scale your bot horizontally, you need to split the shards between them.

Instance 1:

```dotenv
TOTAL_SHARDS=10
MIN_SHARD_ID=0
MAX_SHARD_ID=4
```

Instance 2:

```dotenv
TOTAL_SHARDS=10
MIN_SHARD_ID=5
MAX_SHARD_ID=9
```

Shard ID ranges must never overlap between running instances.

### Step 3: Deployment Options

Fork this repository to your GitHub account, connect it to your preferred cloud platform, and configure your environment variables in the platform.
Some platform services may also support adding secrets directly from your `.env` file.

#### Cloud Platforms with GitHub + Docker Support
- These can auto-deploy using the included `Dockerfile`

#### Locally
- You can also test it locally by building and running using the `Dockerfile`
- Navigate your terminal to the repository and execute the following commands
  
  ```
  docker build -t papertrail-bot .
  docker run --env-file .env papertrail-bot
  ```
  
#### Healthcheck Endpoint

The bot exposes a `/health` endpoint on port **8080**.  
This endpoint simply returns `200 OK` and is intended for platforms or uptime monitors to check if the bot is alive.

> Note: This is **not a public endpoint** and serves no other function beyond internal service health monitoring.

# License

- **PaperTrailBot**: AGPLv3
- **PaperTrail Persistence API**: AGPLv3
- **PaperTrail SDK**: GNU GPLv3

---
Feel free to raise issues on GitHub if you get stuck!
