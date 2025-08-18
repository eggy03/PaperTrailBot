# Overview
PaperTrail is a free and open source, privacy friendly Discord bot designed to deliver structured, reliable logging across all major audit and runtime events. It hooks into Discord's audit logs to cover for most of the audit log events and for events not covered by Audit Logs, it supplements them with real-time listeners to bridge gaps in native coverage (e.g. voice state, boosts, message edits and deletions, custom triggers).

Key Features:

- 🔍 Full audit log integration (supports over 50+ event types) and generic support for unknown types
- 💬 Encrypted Message logging  (edit, delete)
- 👤 Member activity tracking (joins, leaves, kicks, bans, updates)
- 🔊 Voice activity logging (join/leave, move)
- 🚀 Server boost tracking
- 🧱 Auto-deletion of logged messages from our servers after 30 days

> 📎 Get it from here: https://discord.com/discovery/applications/1381658412550590475

# Self-Hosting Guide (Coming Soon)
The legacy version still supports self hosting. You may get the guide [here](https://github.com/Egg-03/PaperTrailBot/blob/legacy-v1.2.2/README.md).
Since v2, the project has been split into two services:
1) The core bot service
2) A [Persistence API](https://github.com/Egg-03/PaperTrail-PersistenceAPI) service

The guide needs to be updated to reflect the changes

# Privacy

PaperTrail is built with privacy-first principles. By default, it **does not log any personal data** unless features are explicitly enabled by server admins.

- Messages are logged for moderation purposes only, if enabled.
- All stored messages are encrypted before being saved to the database.
- Logs are automatically deleted after 30 days.
- No personal data is used for analytics, profiling, or sold to third parties.
- If requested, users can have their data deleted by ID.

*Never post sensitive information in public channels. While PaperTrail encrypts all the data it receives, other bots may not.*

[Read the full Privacy Policy](./PRIVACY.md)

# Security

If you discover a security vulnerability in PaperTrail, please report it **privately**.

- Do **not** open public GitHub issues for security bugs.
- Instead, email me at 📧 **egg03@duck.com**
- I will respond as soon as possible and work with you to resolve the issue.

[View the full Security Policy](./SECURITY.md)

# Terms of Use

PaperTrail is provided under the Apache 2.0 License and is intended for responsible use. By using the public instance or self-hosting it, you agree to the basic terms outlined in our [Terms of Service](./TERMS.md).

# License

PaperTrail is licensed under the [Apache License 2.0](./LICENSE).

You are free to:
- Use, modify, and redistribute the code
- Self-host or publicly host your own instance
- Build on top of this bot for your own projects

Just make sure to include proper attribution and comply with the [terms](https://www.apache.org/licenses/LICENSE-2.0).

---
Feel free to contribute to this guide or raise issues on GitHub if you get stuck!

