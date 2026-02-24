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

| Repository                                                         | Description                              |
|--------------------------------------------------------------------|------------------------------------------|
| [PaperTrailBot](https://github.com/eggy03/PaperTrailBot)           | Core bot application                     |
| [PaperTrail SDK](https://github.com/eggy03/papertrail-sdk)         | Client library for the API               |
| [PaperTrail API](https://github.com/eggy03/PaperTrail-API-Quarkus) | Backend API for providing CRUD functions |

> [!IMPORTANT]
> PaperTrail is currently in maintenance mode. Existing bugs will be fixed, dependency updates will be provided
> but large new features will likely not be added.

# Self-Hosting Guide

> [!IMPORTANT]
> Please note that this section is only for users who want to self-host this bot.
>
> It is recommended that you set up the API service before setting up the bot.

## Setting up the API Service

Follow this [guide](https://github.com/eggy03/PaperTrail-API-Quarkus?tab=readme-ov-file)

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

| Variable       | Description                                                                              |
|----------------|------------------------------------------------------------------------------------------|
| `TOKEN`        | Discord application bot token (from the Developer Portal)                                |
| `API_URL`      | Internal URL of the PaperTrail API (e.g., `http://localhost:8081`)                       |
| `TOTAL_SHARDS` | The total number of shards (connections) your bot is using across **ALL** bot instances. |
| `MIN_SHARD_ID` | The first shard number **THIS** bot instance should handle.                              |
| `MAX_SHARD_ID` | The last shard number **THIS** bot instance should handle.                               |
| `PORT`         | The port which the health check endpoint will bind to.                                   |

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

> [!CAUTION]
> Shard ID ranges must never overlap between running instances.

### Step 3: Deployment Options

#### Local

- Clone the Repository

```shell
git clone https://github.com/eggy03/PaperTrailBot.git
cd PaperTrail-API-Quarkus
```

- Keep your `.env` file ready inside the locally cloned repository
- Make sure docker is running. The provided `Dockerfile` will be used for building.

```shell
docker build -t papertrail-bot .
docker run -p <PORT>:<PORT> --env-file .env papertrail-bot
```

`<PORT>` should be replaced by the Port number you have set in your `.env` file.

#### Cloud Based

You can also deploy on cloud platforms that support docker-based deploys via Dockerfile.
The exact procedure varies, but it usually involves linking the repository, choosing the Dockerfile, and supplying the
necessary environment variables.

#### Health check Endpoint (Optional)

The bot exposes a `/health` endpoint on the port set by you in the `.env` file or as system env variable.

This endpoint simply returns `HTTP 200` if the bot is ready to work, else `HTTP 503`.

This endpoint serves as a readiness probe for containers to check the health of the bot.

# License

This project is licensed under the [AGPLv3](/LICENSE) license.

### What this means for you ?

- If you deploy this project **without modifying the source code**, you do not need to provide anything additional.
  The source code is already publicly available.

- If you **modify the source code** and run it as a service where users interact with it over a network,
  you must make the complete corresponding source code of your modified version available to those users.
  You are not required to publish it publicly.

- You may modify, redistribute, rebrand, and monetize the project. However:
  - Your version must remain licensed under AGPLv3.
  - You must preserve copyright notices and the original license.
  - You must clearly state any changes you have made.

- This software is provided without warranty, as described in AGPLv3.

# Help

If you face any problems during self-hosting or have a question that needs to be answered, please feel free to
open an issue in the Issues tab. I will try my best to answer them as soon as I can.
