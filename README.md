# Status

![Docker Images](https://img.shields.io/github/actions/workflow/status/eggy03/PaperTrailBot/.github%2Fworkflows%2Fpublish-container-image.yml?style=for-the-badge&label=IMAGES)
![Latest Tag](https://img.shields.io/github/v/tag/eggy03/PaperTrailBot?sort=semver&style=for-the-badge&label=LATEST%20TAG)
![Latest Release](https://img.shields.io/github/v/release/eggy03/PaperTrailBot?sort=date&display_name=tag&style=for-the-badge&label=LATEST%20RELEASE)
![GitHub commits since latest release](https://img.shields.io/github/commits-since/eggy03/PaperTrailBot/latest?sort=date&style=for-the-badge)

# Table of Contents

* [Overview](#overview)
* [Repositories](#repositories)
* [Self-Host (Auto Configuration)](#self-host-auto-configuration)
* [Self-Host (Manual Configuration)](#self-host-manual-configuration)
* [Sharding (Advanced Configuration)](#sharding-advanced-configuration)
* [Synchronizing Rate Limits (Advanced Configuration)](#synchronizing-rate-limits-advanced-configuration)
* [License](#license)
* [Help](#help)

# Overview

A free and open-source, self-hostable Discord bot designed to record the changes made to a server
and deliver them to a configured channel without the need to manually navigate to Discord's Audit Log section.

With support for detecting more than 72 events, it can log changes made to: AutoMod Settings, Servers, Onboarding,
Invites, Members, Roles, Channels, Threads, Stages, Events, Polls, Messages, Boosts, Emojis, Stickers, Soundboard,
Integrations, Webhooks, Moderation Action and Unknown events.

> Get it from here: https://discord.com/discovery/applications/1381658412550590475

Run the `/setup` slash command to see instructions on how to configure the bot for your server.

# Repositories

| Repository                                                               | Description                                                                                          |
|--------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------|
| [PaperTrailBot](https://github.com/eggy03/PaperTrailBot)                 | Core bot application                                                                                 |
| [PaperTrail SDK](https://github.com/eggy03/papertrail-sdk)               | Java client library for interacting with the API                                                     |
| [PaperTrail API](https://github.com/eggy03/PaperTrail-API-Quarkus)       | Backend API providing configuration and data storage                                                 |
| [PaperTrail Deployment](https://github.com/eggy03/PaperTrail-Deployment) | Contains docker compose files for auto configuration and deployment of the bot and required services |

> [!IMPORTANT]
> PaperTrail is currently in maintenance mode. Existing bugs will be fixed, dependency updates will be provided
> but large new features will likely not be added. However, changes will be made to keep the bot and its services
> up to date with the latest Discord API changes.

# Self-Host (Auto Configuration)

Select this option if you want a hassle-free deployment locally or in a VPS
and do not intend to scale your bot across more than 2000 servers.

Follow the deployment guide in:
[PaperTrail-Deployment Repository](https://github.com/eggy03/PaperTrail-Deployment?tab=readme-ov-file)

# Self-Host (Manual Configuration)

This option gives you full control over the services you want to deploy

## Step 1: Setting up the API Service

Follow this [guide](https://github.com/eggy03/PaperTrail-API-Quarkus?tab=readme-ov-file)

## Step 2: Setting up the Bot Service

### Step 2.1: Create an application in the Developer Portal

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

### Step 2.2: Get Required Secrets

| Variable  | Description                                                        |
|-----------|--------------------------------------------------------------------|
| `TOKEN`   | Discord application bot token (from the Developer Portal)          |
| `API_URL` | Internal URL of the PaperTrail API (e.g., `http://localhost:8080`) |

Example `.env` file:

```dotenv
TOKEN="my-token"
API_URL="http://localhost:8080"
```

### Step 2.3: Deployment Options

#### Option A : Local Deployment

<ins>Using Pre-Built Images</ins>

The [GitHub Container Registry](https://github.com/eggy03/PaperTrailBot/pkgs/container/papertrail-bot)
has the native build images for the bot which you can use.

Make sure you have the `.env` file containing the required secrets in the root of the folder
you're executing the following commands from:

```bash
docker run -d --name papertrail-bot --env-file .env ghcr.io/eggy03/papertrail-bot:latest
```

<ins>Building From Source</ins>

Alternatively, you can use the provided Dockerfile to build from source:

```bash
git clone https://github.com/eggy03/PaperTrailBot.git
cd PaperTrailBot
```

```bash
docker build -t papertrail-bot .
docker run -d --name papertrail-bot --env-file .env papertrail-bot
```

> [!NOTE]
>
> While the above sub-options use `--env-file .env` for examples, you can also pass environment variables directly
> via `docker -e KEY:"VALUE"`

#### Option B: Cloud Deployment

Many cloud platforms support Docker-based deployments directly from a repository.

Typically, the process involves:

- Linking the repository
- Selecting the `Dockerfile`
- Supplying the required environment variables

Alternatively, you can deploy using the pre-built container images found in the GitHub Container Registry, if suported.

## Step 3: Testing your deployment

Upon successful deployment of all the required services, including the bot, you can run the slash command
`/setup` in a server where the bot has been invited. The command will tell you how to configure your bot.

# Sharding (Advanced Configuration)

> [!NOTE]
> This section is required only when your bot has reached over 1000 servers.

Sharding splits your bot connection into multiple independent connections to the Discord gateway.
Each independent connection is called a shard.
Discord allows you to have up to 2500 guilds per shard but the recommended configuration is 1 shard per 1000 guilds.

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

> [!IMPORTANT]
> Shard ID ranges must never overlap between running bot processes/instances.

# Synchronizing Rate Limits (Advanced Configuration)

> [!NOTE]
> This section is required only if you have multiple bot processes running concurrently, like in Example 3 of Sharding.

When you run multiple instances/process, the JDA in each process thinks that it has the sole responsibility
of handling Discord's API rate limits because the processes aren't aware of each other's existence.
This means, without some sort of communication or synchronization between the instances, you may exceed the rate limits
pretty early.

It is possible to synchronize Discord's rate limits across multiple instances/processes
by using an external proxy such as the [Twilight HTTP Proxy](https://github.com/twilight-rs/http-proxy).
This proxy acts as a shared HTTP gateway that coordinates Discord API rate limits across multiple bot instances.

This however, requires disabling the default rate limiter in JDA because the proxy will handle them globally.

It is also worth noting that this feature is largely untested in PaperTrail.
Read more about this in the [Limitations](#limitations) section.

### Running the Proxy

To use the pre-built Docker images from
the [container registry](https://github.com/twilight-rs/http-proxy/pkgs/container/http-proxy),
run one of the following commands:

```shell
$ docker run -itd -e DISCORD_TOKEN="my token" -p 3000:80 ghcr.io/twilight-rs/http-proxy
# Or with metrics enabled
$ docker run -itd -e DISCORD_TOKEN="my token" -p 3000:80 ghcr.io/twilight-rs/http-proxy:metrics

```

This will set the discord token to `"my token"` and map the bound port to port `3000` on the host machine.

### Bot Configuration

Add the following environment variable to your bot:

| Variable    | Description                                                                                                                |
|-------------|----------------------------------------------------------------------------------------------------------------------------|
| `PROXY_URL` | Base URL for the HTTP proxy that will receive Discord API requests instead of discord.com (Example: http://localhost:3000) |

When configured, all Discord API requests made by the bot will be routed through the proxy.

### Limitations

Twilight HTTP Proxy has its own global rate limiting feature and recommends clients to disable their per-instance
rate limit checks. That's because requests from all bot instances are centrally managed and throttled by the proxy
rather than by each client individually.

For PaperTrail, this would mean replacing JDA's default `SequentialRestRateLimiter`,
which is an implementation of the `RestRateLimiter`, with a custom no-op implementation.
Such an implementation would effectively bypass JDA’s local rate-limit checks and defer all rate limiting to the proxy.
At the moment, PaperTrail does not provide such an implementation.

This means that if you use a proxy for your bot clusters, you are effectively getting throttled at the proxy-level, as
well
as the instance-level. While this should not create functional conflicts,
it may lead to under-utilization of the rate limits provided by Discord and can reduce the overall throughput of your
bot cluster.

# License

This project is licensed under the [AGPLv3](/LICENSE) license.

### What this means for you:

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
open an issue in the Issues tab. I will try my best to respond as soon as I can.
