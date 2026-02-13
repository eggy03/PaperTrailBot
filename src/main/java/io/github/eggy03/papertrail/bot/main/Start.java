package io.github.eggy03.papertrail.bot.main;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.github.eggy03.papertrail.bot.listeners.auditlog.event.AuditLogEventListener;
import io.github.eggy03.papertrail.bot.listeners.auditlog.setup.AuditLogSetupCommandListener;
import io.github.eggy03.papertrail.bot.listeners.auditlogsupl.event.guild.GuildBoostEventListener;
import io.github.eggy03.papertrail.bot.listeners.auditlogsupl.event.guild.GuildMemberJoinAndLeaveEventListener;
import io.github.eggy03.papertrail.bot.listeners.auditlogsupl.event.guild.GuildPollEventListener;
import io.github.eggy03.papertrail.bot.listeners.auditlogsupl.event.guild.GuildVoiceEventListener;
import io.github.eggy03.papertrail.bot.listeners.messagelog.event.MessageLogListener;
import io.github.eggy03.papertrail.bot.listeners.messagelog.setup.MessageLogSetupCommandListener;
import io.github.eggy03.papertrail.bot.listeners.misc.ActivityUpdateListener;
import io.github.eggy03.papertrail.bot.listeners.misc.BotSetupInstructionCommandListener;
import io.github.eggy03.papertrail.bot.listeners.misc.SelfKickListener;
import io.github.eggy03.papertrail.bot.listeners.misc.ServerStatCommandListener;
import lombok.NonNull;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/*
 * The main class of the bot
 */
public class Start {

    // All I/O blocking operations run inside the vThreadPool
    private static final @NonNull Executor vThreadPool = Executors.newVirtualThreadPerTaskExecutor();

    public static void main(String[] args) throws IOException {

        ConnectionInitializer ci = new ConnectionInitializer();
        ShardManager manager = ci.getManager();

        manager.addEventListener(new AuditLogSetupCommandListener());
        manager.addEventListener(new AuditLogEventListener(vThreadPool));

        manager.addEventListener(new MessageLogSetupCommandListener());
        manager.addEventListener(new MessageLogListener(vThreadPool));

        manager.addEventListener(new GuildVoiceEventListener(vThreadPool));
        manager.addEventListener(new GuildMemberJoinAndLeaveEventListener(vThreadPool));
        manager.addEventListener(new GuildPollEventListener(vThreadPool));
        manager.addEventListener(new GuildBoostEventListener(vThreadPool));
        manager.addEventListener(new SelfKickListener(vThreadPool));

        manager.addEventListener(new ServerStatCommandListener());
        manager.addEventListener(new BotSetupInstructionCommandListener());
        manager.addEventListener(new ActivityUpdateListener(manager));
        // re-enable it only when adding/updating/deleting commands
        //manager.addEventListener(new SlashCommandRegistrationListener());

        // Custom health check endpoint
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/ping", new PingHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class PingHandler implements HttpHandler {
        @Override
        public void handle(@NonNull HttpExchange exchange) throws IOException {
            exchange.sendResponseHeaders(200, -1);
        }
    }

}
