# Overview
PaperTrail is a free and open-source, self-hostable, and privacy-friendly Discord bot designed to provide audit log data directly in a channel—without requiring users to navigate server settings each time.

Key Features:

- Full audit log integration (supports over 50+ event types) and generic support for unknown types
- Message logging  (edit, delete)
- Member activity tracking (joins, leaves, kicks, bans, updates)
- Voice activity logging (join/leave, move)
- Auto-deletion of logged messages after 30 days

> [!IMPORTANT]
> PaperTrail is currently in maintenance mode. Existing bugs will be fixed, dependency updates will be provided
> but large new features will likely not be added.

# Self-Hosting Guide
> [!IMPORTANT]
> It is recommended that you deploy the [Persistence API](https://github.com/Egg-03/PaperTrail-PersistenceAPI?tab=readme-ov-file#papertrail-persistenceapi) service before deploying the bot itself since the bot relies on the URL of the service to communicate

The project is split into two services:
1) The core bot service
2) A [Persistence API](https://github.com/Egg-03/PaperTrail-PersistenceAPI) service

The following guide shows how to set up the bot service

To read the guide on deploying the Persistence API Service, click [here](https://github.com/eggy03/PaperTrail-PersistenceAPI?tab=readme-ov-file#papertrail-persistenceapi)

### Step 1: Get Required Secrets

You will need the following environment variables to run the bot:

| Variable  | Description                                                                                               |
|-----------|-----------------------------------------------------------------------------------------------------------|
| `TOKEN`   | Discord application bot token (from the [Developer Portal](https://discord.com/developers/applications))  |
| `API_URL` | Internal URL of the Persistence API (e.g., `http://persistence:8080`)                                     |

### Step 2: Deployment Options

Fork this repository to your GitHub account, connect it to your preferred cloud platform, and configure your environment variables in the platform.
Some platform services may also support adding secrets directly from your `.env` file.

#### Cloud Platforms with GitHub + Docker Support
- These can auto-deploy using the included `Dockerfile`
- Optional: The bot exposes a `/ping` endpoint which can be used by platforms to periodically check for it's health

#### Locally
- You can also test it locally by building and running using the `Dockerfile`
- Navigate your terminal to the repository and execute the following commands
  
  ```
  docker build -t papertrail-bot .
  docker run --env-file .env papertrail-bot
  ```
  
#### Healthcheck Endpoint
The bot exposes a `/ping` endpoint on port **8080**.  
This endpoint simply returns `200 OK` and is intended for platforms or uptime monitors to check if the bot is alive.

> Note: This is **not a public API** and serves no other function beyond internal service health monitoring.

# Privacy

PaperTrail is built with privacy-first principles. By default, it **does not log any personal data** unless features are explicitly enabled by server admins.

- Messages are logged for moderation purposes only, if enabled.
- Logs are automatically deleted after 30 days.
- No personal data is used for analytics, profiling, or sold to third parties.
- If requested, users can have their data deleted by ID.

*Never post sensitive information in public channels.*

[Read the full Privacy Policy](./PRIVACY.md)

# Security

If you discover a security vulnerability in PaperTrail, please report it **privately**.

- Do **not** open public GitHub issues for security bugs.
- Instead, email me at 📧 **eggzerothree@proton.me**
- I will respond as soon as possible and work with you to resolve the issue.

[View the full Security Policy](./SECURITY.md)

# Terms of Use

PaperTrail is provided under the Apache 2.0 License and is intended for responsible use.
By using the official hosted instance or self-hosting it, you agree to the basic terms outlined in our [Terms of Service](./TERMS.md).

# License

PaperTrail is licensed under the [Apache License 2.0](./LICENSE).

You are free to:
- Use, modify, and redistribute the code
- Self-host or publicly host your own instance
- Build on top of this bot for your own projects

Just make sure to include proper attribution and comply with the [terms](https://www.apache.org/licenses/LICENSE-2.0).

---
Feel free to contribute to this guide or raise issues on GitHub if you get stuck!
