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
docker run --env-file .env papertrail-bot
```

#### Cloud Based

You can also deploy on cloud platforms that support docker-based deploys via Dockerfile.
The exact procedure varies, but it usually involves linking the repository, choosing the Dockerfile, and supplying the
necessary environment variables.

# Sharding Configuration (Optional, Advanced)

> [!CAUTION]
> This section is intended for users who understand how Discord gateway sharding works
> and want to run their bot using a custom shard layout.
>
> Most of the time you do not need to configure this manually.
> If no shard configuration is provided, your bot will run using a single shard by default.

Sharding splits your bot connection into multiple independent connections to the Discord gateway.
Each independent connection is called a shard.
Discord allows you to have upto 2500 guilds per shard but the recommended configuration is 1 shard per 1000 guilds.

You will need the following additional environment variables for custom shard configuration.

| Variable       | Description                                                                   |
|----------------|-------------------------------------------------------------------------------|
| `TOTAL_SHARDS` | Total number of shards used by the bot across all running processes/instances |
| `MIN_SHARD_ID` | The first shard ID handled by this specific bot instance                      |
| `MAX_SHARD_ID` | The last shard ID handled by this specific bot instance                       |

Shard IDs start at 0.

If `TOTAL_SHARDS=5`, the valid shard IDs are:

```
0 1 2 3 4
```

Take a look at the following configuration examples to have a clearer picture of what values to put
for your use-case

**Example 1: Single Process / Small Bot (<2500 Guilds)**

If your bot is small or self-hosted for a limited number of servers (<2500), one shard is sufficient.
This is the default pre-applied configuration when you do not provide any manual shard info.

```dotenv
TOTAL_SHARDS=1
MIN_SHARD_ID=0
MAX_SHARD_ID=0
```

**Example 2: Single Process / Medium Bot (2500 - 5000 Guilds)**

If your bot exceeds the 2500 guild limit for a single shard, you can increase the shard count while still running one
process:

```dotenv
TOTAL_SHARDS=2
MIN_SHARD_ID=0
MAX_SHARD_ID=1
```

Remember that each shard can only handle up to 2500 guilds so plan the total number shards accordingly

**Example 3: 2 Bot Processes / 25000 Guilds**

If you run your bot across multiple processes, you need to split the shards between them.

Process/Instance 1:

```dotenv
TOTAL_SHARDS=10
MIN_SHARD_ID=0
MAX_SHARD_ID=4
```

Process/Instance 2:

```dotenv
TOTAL_SHARDS=10
MIN_SHARD_ID=5
MAX_SHARD_ID=9
```

Process 1 handles shards 0-4 and Process 2 handles 5-9.
Each process manages 5 shards, together covering all 10 shards.


> [!CAUTION]
> Shard ID ranges must never overlap between running processes/instances.

> [!IMPORTANT]
> PaperTrail is designed primarily for **ease of self-hosting**.
> Running multiple bot instances is supported, but **rate-limit coordination between instances is not implemented**.
>
> Gateway and REST API rate limits are automatically handled by the underlying runtime within a single process.
> When running multiple bot instances, each process manages its own rate-limit state independently.
>
> Large-scale deployments may require centralized systems for **cross-shard communication, request coordination, and
shared rate-limit tracking**. Implementing such infrastructure would significantly increase operational complexity and
> runs counter to PaperTrail’s goal of remaining simple to deploy and self-host.
>
> In practice, this limitation is only relevant for **very large bots** operating at significant scale.

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
