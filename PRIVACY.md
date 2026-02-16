# Privacy Policy

**Data Controller:** This project is maintained by [@Egg-03](https://github.com/eggy03).

**Last Updated:** February 16, 2026

This Discord bot was built as an open source project with a privacy-first mindset.
The Service is provided at no cost and is intended for use as-is.

This page is used to inform users regarding our policies with the collection, use, and disclosure of information for anyone choosing to use the bot.

If you choose to use this bot in your Discord server, you agree to the collection and use of information in accordance with this policy.
The data collected is strictly limited to what is necessary for the bot's core functionality and moderation features.

We do not sell your data.
We do not share personal data with third parties except where necessary to provide the service, such as cloud hosting and database providers, or in response to lawful requests.
Any third-party processors we use are bound by contractual data-protection requirements and may only process data on our behalf and for the purpose of providing the service.

---

## Scope of This Policy

This Privacy Policy applies **only** to the official hosted instance of this bot that is managed by [@Egg-03](https://github.com/eggy03).  
The official instance can be identified by its **Discord Application ID: `1381658412550590475`**.  

If you or someone else self-hosts the bot, that instance is operated independently and is **not covered by this Privacy Policy**.
In those cases, data handling is the sole responsibility of the host of that particular instance.

---

## Information Collection and Use

The bot only stores data when specific features are explicitly enabled by server administrators:

### If **Message Logging** is enabled:
- Message Content
- Message ID
- Author ID (Discord user ID of the message owner)
- Channel ID
- Guild ID
- Timestamp

### If **only Audit Logging** is enabled:
- Guild ID
- Channel ID where audit logs should be sent (one per guild)

No message content, message ID or Author ID is stored in this case.

**Note on Direct Messages (DMs)**: The bot does not monitor, read, or store data from Direct Messages between users.
It only processes data within Discord Guilds (servers) where it has been explicitly invited.

---

## Data Retention

We adhere to a strict data minimization policy.
Information is only retained for the duration necessary to provide the bot's logging features.

- **Temporary Message Logs**: All captured message content (text-only) and metadata logs are stored for a maximum period
  of 30 days.
  Following this period, data is programmatically purged from our production databases.
- **System Backups**: Data contained within database backups may persist for up to an additional 7 days beyond the
  standard retention period before being overwritten.
- **Administrative Data**: Configuration settings (e.g. designated log channels) are retained as long as the bot
  remains authorized within the Discord Guild.

---

## Data Security

We implement reasonable technical and organizational measures to protect stored data from unauthorized access, loss,
or misuse. This includes strict access controls, limited operator access, and infrastructure security best practices.

**Infrastructure & Storage**

- Cloud Providers: We utilize `Northflank` for application hosting and `Aiven` for managed database services.
- Encryption: While all collected data is stored in a readable format, our infrastructure providers employ encryption
  to protect data on the physical disk level. To know more, read their security
  policies of our providers [Northflank](https://northflank.com/security)
  and [Aiven](https://aiven.io/security-compliance).

**Access Control**

Access to the production environment and database is strictly limited to the Data Controller (@Egg-03).
No other contributors or third parties have access to the raw data stored by the bot.

**Disclaimer**

While we strive to use commercially acceptable means to protect your information, no method of transmission over the
internet or method of electronic storage is 100% secure.
We cannot guarantee absolute security and the service is provided on an "as-is" basis.

---

## Diagnostic Logs

In case of runtime errors, the bot may log basic diagnostic information such as error messages, timestamps, and internal event states to assist with debugging.
Logs may include additional metadata such as guild IDs, channel IDs, message IDs, or user IDs when necessary to diagnose issues.

Log retention is governed by our infrastructure providers and operational requirements, and may vary over time.
Logs are not used for analytics.

---

## Children’s Privacy

This bot is not intended for users under the age of 13. It does not knowingly collect any personal information from children.
If it is discovered that such data has been inadvertently stored, it will be deleted immediately upon request.

---

## User Rights

We respect the privacy rights of all Discord users. Regardless of your jurisdiction, you may exercise the following:

- **Right to Access**: Users may request a transcript of all stored data associated with their Discord Snowflake ID.
- **Right to Erasure**: Users may request the immediate removal of their data from our active logs.

To submit a request: Please contact us using one of the methods below.
For security purposes, we may require you to verify your identity via a signed message or a direct interaction through
the official Discord client.
NOTE: We will never ask for any kinds of passwords or secret tokens from you.

---

## Contact

For privacy-related questions or other requests:
- GitHub: [Submit an issue or discussion](https://github.com/eggy03/PaperTrailBot/issues)
- Email: `eggzerothree@proton.me`

---

## Changes to This Privacy Policy

This policy may be updated from time to time. Changes will be posted here and are effective immediately once published.

---

