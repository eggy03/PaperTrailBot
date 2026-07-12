_Last Updated : July 09, 2026_

# Table of Contents

* [Overview](#overview)
* [LITE vs ORIGINAL](#difference-between-original-and-lite-editions)
* [Docker Compose Deployment](#docker-compose-deployment)
* [Manual Deployment](#manual-deployment)
* [Sharding](#sharding)
* [Synchronizing Rate Limits](#synchronizing-rate-limits)
* [Health Checks](#health-checks-and-custom-port-configuration)
* [Native-Builds](#native-builds)

# Overview

This section will show you how to deploy PaperTrail bot, and its related services. Note that this deployment guide
is meant for the `ORIGINAL` edition and not the `LITE` edition.

By `ORIGINAL` edition, I mean the current repository,
while `LITE` edition is [this](https://github.com/eggy03/PaperTrailBot-Lite) repository.

# Difference between `ORIGINAL` and `LITE` editions

The ORIGINAL edition was originally designed to support self-hosting, horizontal scaling, customizable sharding,
and a simple post-deployment configuration experience.
This required you to set up a `custom API service`, a configurable instance of `Redis` and a `PostgreSQL` instance,
alongside an instance (or multiple instances) of the bot.
As a result, the self-hosting process is considerably more involved,
while the post-deployment experience offers fewer server-level customization options in exchange for greater
scalability.

The `LITE` edition is designed with greater personalization and easier setup in mind.
It is designed to be invited to and used on a single server and does not require any form of sharding or scaling.
Hence, you do not need to set up anything other than the bot and a redis/valkey instance.
It is also designed to allow for more customization such as routing of individual events to custom channels which the
user can set.

Other than the aforementioned differences, everything else is identical between the two editions.

If you are self-hosting it, you will probably host it for your own server, and hence in most cases, `LITE` edition
is the only edition you need as it's way less complicated to set up than the `ORIGINAL` edition.
Guide to the lite edition is available in this repository's [readme](/README.md).

You should use the `ORIGINAL` edition only if you need capabilities that are not available in the `LITE` edition,
such as manual sharding or horizontal scaling.

The `ORIGINAL` edition supports two forms of deployments:

1) [Docker Compose Deployment](#docker-compose-deployment)
2) [Manual Deployment](#manual-deployment)

# Latest Docker Image Releases

[![Latest API Release](https://img.shields.io/github/v/release/eggy03/PaperTrail-API-Quarkus?sort=date&display_name=tag&style=for-the-badge&label=PaperTrail%20API)](https://github.com/eggy03/PaperTrail-API-Quarkus/pkgs/container/papertrail-api)
[![Latest Bot Release](https://img.shields.io/github/v/release/eggy03/PaperTrailBot?sort=date&display_name=tag&style=for-the-badge&label=PaperTrail%20Bot)](https://github.com/eggy03/PaperTrailBot/pkgs/container/papertrail-bot)

# Docker Compose Deployment

> [!NOTE]
> An easier version of the Manual Deployment, uses docker compose with pre-built images.
>
> Automatically configures every service for you.
>
> You can configure the compose files to change how you want to deploy them.
>
> This guide assumes you know how to set up Redis and PGSQL on the cloud.
>
> Limitations: Only autoconfigures 1 instance of each service; no customizable sharding or horizontal scaling support

## Step 1: Create an application in the Developer Portal

Log on to the [Discord Developer Portal](https://discord.com/developers/applications) and create an application.

The application can have any name, avatar, banner, or description.
However, the following scopes, permissions, and intents are required:

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

Don't forget to copy the bot `TOKEN` as it will be required in the next step.

If you have never created a bot before, follow
this [visual guide](https://jda.wiki/using-jda/getting-started/#creating-a-discord-bot)

## Step 2: Clone this repository

```shell
git clone https://github.com/eggy03/PaperTrailBot.git
cd PaperTrailBot
```

## Step 3: Create your environment file in the repository root

You will need the following environment variables :

| Variable         | Description                                               |
|------------------|-----------------------------------------------------------|
| `TOKEN`          | Discord application bot token (from the Developer Portal) |
| `DB_NAME`        | A name for your database                                  |
| `DB_USERNAME`    | A username for your database                              |
| `DB_PASSWORD`    | A strong password for the database user                   |
| `CACHE_PASSWORD` | A strong password for your valkey cache                   |

The following is an example `.env` data

```dotenv
TOKEN=somevalue
DB_NAME=papertrail
DB_USERNAME=defaultdb
DB_PASSWORD=admin
CACHE_PASSWORD=admin
```

> [!CAUTION]
> Never commit your .env file to version control.
> If your `TOKEN` is ever exposed, immediately regenerate it in the Discord Developer Portal.

## Step 4: Deploy the services

```shell
docker compose up -d
```

## Step 5: Verify your deployment

To check if everything is running properly

```bash
# base deployment
docker compose ps

# check logs
docker compose logs -f
```

If the deployment was successful:

- Invite the bot to a Discord server.
- Confirm the bot appears online.
- Run the `/setup` slash command.

This command will guide you through the initial configuration for your server.

## Stopping, Starting and Restarting PaperTrail

```bash
docker compose stop
```

```bash
docker compose start
```

```bash
docker compose restart
```

## Updating PaperTrail

```bash
# stop containers
docker compose down

# update images
docker compose pull

# restart containers
docker compose up -d
```

## Uninstalling PaperTrail

```bash
# Remove containers, networks and volumes, except images
docker compose down -v

# Removing everything
docker compose down --rmi all -v
```

# Manual Deployment

> [!NOTE]
> Gives you full control of the services you want to deploy.
>
> You set up each service manually but have full control over the process, including customizable sharding and
> horizontal scaling
>
> Guides are provided for building and deploying each service (except Redis and PostgreSQL) locally or in cloud, with or
> without docker.
>
> This guide assumes you know how to set up Redis and PGSQL on the cloud.

## Step 1: Setting up the API Service

Follow this [guide](https://github.com/eggy03/PaperTrail-API-Quarkus?tab=readme-ov-file)

## Step 2: Setting up the Bot In Discord

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

Note down the `BOT TOKEN` since it will be shown only once and will be required in the later steps.

## Step 3: Deploying the Bot

### 3.1: Get Required Secrets

| Variable  | Description                                                        | Default Value    | Optional |
|-----------|--------------------------------------------------------------------|------------------|----------|
| `TOKEN`   | Discord application bot token (from the Developer Portal)          | No Default Value | No       |
| `API_URL` | Internal URL of the PaperTrail API (e.g., `http://localhost:8080`) | No Default Value | No       |

Example `.env` file:

```dotenv
TOKEN="my-token"
API_URL="http://localhost:8080"
```

### 3.2: Deployment Options

> [!IMPORTANT]
> Since v4.1.3, the bot comes in two build forms: `JVM` and `Native`.
> While this guide covers both the forms, it is recommended to use the JVM form since Native builds are currently
> unstable.
>
> For more info on native builds, see [the native image section](#native-builds)

#### Option A : Deploy Using Pre-Built Docker Images

The GitHub Container Registry
has pre-built docker images for both JVM and Native versions the bot which you can use.

[Container Registry for JVM Edition](https://github.com/eggy03/PaperTrailBot/pkgs/container/papertrail-bot)

[Container Registry for Native Edition](https://github.com/eggy03/PaperTrailBot/pkgs/container/papertrail-bot-native)

You may choose either one.

Make sure you have the `.env` file containing the required secrets in the root of the folder
you're executing the following commands from:

```bash
# JVM
docker run -d --name papertrail-bot --env-file .env ghcr.io/eggy03/papertrail-bot:latest
```

```bash
# Native
docker run -d --name papertrail-bot-native --env-file .env ghcr.io/eggy03/papertrail-bot-native:latest
```

#### Option B : Building From Source With Docker

```bash
git clone https://github.com/eggy03/PaperTrailBot.git
cd PaperTrailBot
```

```bash
# JVM
docker build -t papertrail-bot .
docker run -d --name papertrail-bot --env-file .env papertrail-bot
```

```bash
# Native
docker build -f Dockerfile.native -t papertrail-bot-native .
docker run -d --name papertrail-bot-native --env-file .env papertrail-bot-native
```

> [!NOTE]
>
> While the above sub-options use `--env-file .env` for examples, you can also pass environment variables directly
> via `docker -e KEY:"VALUE"`

#### Option C : Building From Source Without Docker

```bash
git clone https://github.com/eggy03/PaperTrailBot.git
cd PaperTrailBot
```

```bash
# JVM
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
```

```bash
# Native
./mvnw clean package -Dnative
```

The built application will be found in the `target` folder of the project.

#### Option D : Cloud Deployment

If your cloud supports building from Dockerfile, point the source towards `Dockerfile` (for JVM Build)
or `Dockerfile.native` (for Native Build), found in the project's root.

If your cloud supports using pre-built docker images, you can find the image links in
the container registry.

## Step 4: Testing your deployment

Upon successful deployment of all the required services, including the bot, you can run the slash command
`/setup` in a server where the bot has been invited. The command will tell you how to configure your bot.

# Sharding

> [!NOTE]
> Sharding is required only when your bot reaches 2500 servers.

Sharding splits your bot connection into multiple independent connections to the Discord gateway.
Each independent connection is called a shard.
Discord allows you to have up to 2500 guilds per shard but the recommended configuration is 1 shard per 1000 guilds.

You will need the following additional environment variables for custom shard configuration.

| Variable       | Description                                                                   | Default Value | Optional |
|----------------|-------------------------------------------------------------------------------|---------------|----------|
| `TOTAL_SHARDS` | Total number of shards used by the bot across all running processes/instances | 1             | Yes      |
| `MIN_SHARD_ID` | The first shard ID handled by this specific bot instance                      | 0             | Yes      |
| `MAX_SHARD_ID` | The last shard ID handled by this specific bot instance                       | 0             | Yes      |

Shard IDs start at 0.

If `TOTAL_SHARDS=5`, the valid shard IDs are:

```
0 1 2 3 4
```

Take a look at the following configuration examples to have a clearer picture of what values to put
for your use-case

#### Example 1: Single Process / Small Bot (<2500 Guilds)

If your bot is small or self-hosted for a limited number of servers (<2500), one shard is sufficient.
This is the default pre-applied configuration when you do not provide any manual shard info.

```dotenv
TOTAL_SHARDS=1
MIN_SHARD_ID=0
MAX_SHARD_ID=0
```

#### Example 2: Single Process / Medium Bot (2500 - 5000 Guilds)

If your bot exceeds the 2500 guild limit for a single shard, you can increase the shard count while still running one
process:

```dotenv
TOTAL_SHARDS=2
MIN_SHARD_ID=0
MAX_SHARD_ID=1
```

Remember that each shard can only handle up to 2500 guilds so plan the total number shards accordingly

#### Example 3: 2 Bot Processes / 25000 Guilds

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
>
> `TOTAL_SHARDS` count must be equal for all instances and must reflect the total shards used across all instances
> combined.

# Synchronizing Rate Limits

> [!NOTE]
> This section is required only if you have multiple instances of the bot running, like in Example 3 of Sharding.

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

| Variable                  | Description                                                                                                                | Default Value | Optional |
|---------------------------|----------------------------------------------------------------------------------------------------------------------------|---------------|----------|
| `TWILIGHT_HTTP_PROXY_URL` | Base URL for the HTTP proxy that will receive Discord API requests instead of discord.com (Example: http://localhost:3000) | Blank         | Yes      |

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

# Health Checks and Custom Port Configuration

### Health Checks

Since v4, it is possible to get health information by probing any of the following health endpoints:

| Endpoint            | Description                                                                                           |
|---------------------|-------------------------------------------------------------------------------------------------------|
| `/q/health`         | Aggregated health status containing all registered checks.                                            |
| `/q/health/live`    | Liveness probe. Verifies that the bot process is healthy and that all shards are operating normally.  |
| `/q/health/ready`   | Readiness probe. Verifies that all shards are fully connected to Discord and ready to receive events. |
| `/q/health/started` | Startup probe. Indicates whether the application has completed its startup sequence.                  |

#### Liveness Check

The liveness check reports **UP** when all shards belonging to this instance are in one of the following states:

* `CONNECTED`
* `ATTEMPTING_TO_RECONNECT`
* `RECONNECT_QUEUED`
* `WAITING_TO_RECONNECT`

#### Readiness Check

The readiness check reports **UP** only when every shard belonging to this instance is fully `CONNECTED` to Discord.

### Custom Port Configuration

By, default PaperTrail runs on port 8080. If port 8080 is occupied by a different service, or you wish to
run the application on a different port, you can manually set the `PORT` environment variable. Same goes for
`MANAGEMENT_PORT` which will expose the health check endpoints.

| Variable          | Description                                           | Default Value | Optional |
|-------------------|-------------------------------------------------------|---------------|----------|
| `PORT`            | Port Number on which the instance of the bot will run | 8080          | Yes      |
| `MANAGEMENT_PORT` | Port for Health Check Interface                       | 9000          | Yes      |

# Native Builds

> [!CAUTION]
> Native builds are experimental

Since `v4.1.3` it is possible to create native builds of the bot. Native builds are recommended
when you are hosting the bot in a very resource constrained environment,
and you need the bot to have faster startup times and low memory consumption.

Native builds have a larger and resource incentive build time compared to standard JVM builds.

Please note that native builds are experimental and I will try my best to improve support for it in upcoming versions.

You may come across errors which require you to initialize classes during build time
or missing reflection config during runtime.
If you run across such errors, create an Issue in GitHub along with the build or runtime logs.
