/**
 * Contains supplementary audit-style event listeners for actions not covered
 * by Discord's native audit log system.
 *
 * <p>These listeners observe events that are audit-relevant (such as guild,
 * thread, or other structural changes) but cannot be derived from
 * {@link  net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent} entries.</p>
 *
 * <p>All events in this package log to the same destination and follow the same
 * formatting and conventions as standard audit log listeners and hence, no additional setup is required.</p>
 */
package io.github.eggy03.papertrail.bot.listeners.auditlogsupl;