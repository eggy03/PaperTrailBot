<div align="center">
  <img
    src="https://github.com/user-attachments/assets/c465fe26-5c31-4e75-b8c5-d73b6311979b"
    alt="logo_diamond"
    width="186"
    height="186"
  />

  <h2>PaperTrail Bot</h2>
</div>

# Status

![Latest Release](https://img.shields.io/github/v/release/eggy03/PaperTrailBot?sort=date&display_name=tag&style=for-the-badge&label=LATEST%20RELEASE)

# Table of Contents

* [Overview](#overview)
* [Repositories](#repositories)
* [Using The Bot](#using-the-bot)
* [License](#license)
* [AI Usage Declaration and Policy](#ai-usage-declaration-and-policy)
* [Help](#help)

# Overview

A free and open-source, self-hostable Discord bot designed to record the changes made to a server
and deliver them to a configured channel without the need to manually navigate to Discord's Audit Log section.

With support for detecting more than 72 events, it can log changes made to: AutoMod Settings, Servers, Onboarding,
Invites, Members, Roles, Channels, Threads, Stages, Events, Polls, Messages, Boosts, Emojis, Stickers, Soundboard,
Integrations, Webhooks, Moderation Action, Unusual DMs, Raids and Unknown events.

# Repositories

| Repository                                                         | Description                                                 |
|--------------------------------------------------------------------|-------------------------------------------------------------|
| [PaperTrailBot](https://github.com/eggy03/PaperTrailBot)           | Core bot application                                        |
| [PaperTrail SDK](https://github.com/eggy03/papertrail-sdk)         | Java client library for interacting with the API            |
| [PaperTrail API](https://github.com/eggy03/PaperTrail-API-Quarkus) | Backend API providing configuration and data storage        |
| [PaperTrailBot Lite](https://github.com/eggy03/PaperTrailBot-Lite) | Lighter version of the bot requiring no additional services |

> [!IMPORTANT]
> PaperTrail is currently in maintenance mode. Existing bugs will be fixed, dependency updates will be provided
> but large new features will likely not be added. However, changes will be made to keep the bot and its services
> up to date with the latest Discord API changes.

# Using The Bot

### By Inviting It

A pre-configured and deployed instance is the easiest way to use this bot. Just invite it to your server and that's all.

Get it from here: https://discord.com/discovery/applications/1381658412550590475

Run the `/setup` slash command to see instructions on how to configure the bot for your server.

### By Self Hosting It

You can easily self-host the `LITE` version of the bot by reading the
guide [here](https://github.com/eggy03/PaperTrailBot-Lite).

You can also self-host the `ORIGINAL` version by reading the guide [here](/DEPLOYMENT.md).
The guide also mentions the difference between the `LITE` and the `ORIGINAL` versions.

# License

This project is licensed under the [AGPLv3](/LICENSE) license.

# AI Usage Declaration and Policy

No AI or LLM agents have been used to generate any part of the project's source code.
This policy applies to all existing source code and future code contributions.

After two years of development and manual testing, the application has reached a fairly stable and maintainable state.
To preserve code quality, maintainability, format, and stability,
AI-generated source code is **not accepted** for inclusion in this repository and the repositories related to the
application.

Limited use of AI for non-code tasks, such as commit message generation, spelling correction,
or documentation proofreading, may be permitted at the maintainer's discretion.
Such usage does not constitute authorship of the project's source code.

# Help

If you face any problems during self-hosting or have a question that needs to be answered, please feel free to
open an issue in the Issues tab. I will try my best to respond as soon as I can.
