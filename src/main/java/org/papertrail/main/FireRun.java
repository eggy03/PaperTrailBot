package org.papertrail.main;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.papertrail.listeners.audit.event.AuditLogEventListener;
import org.papertrail.listeners.audit.event.GuildBoostEventListener;
import org.papertrail.listeners.audit.event.GuildMemberJoinAndLeaveEventListener;
import org.papertrail.listeners.audit.event.GuildPollEventListener;
import org.papertrail.listeners.audit.event.GuildVoiceEventListener;
import org.papertrail.listeners.audit.setup.AuditLogSetupCommandListener;
import org.papertrail.listeners.message.event.MessageLogListener;
import org.papertrail.listeners.message.setup.MessageLogSetupCommandListener;
import org.papertrail.listeners.misc.BotSetupInstructionCommandListener;
import org.papertrail.listeners.misc.SelfKickListener;
import org.papertrail.listeners.misc.ServerStatCommandListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
/*
 * The main class of the bot
 */
public class FireRun {

	// All I/O blocking operations run inside the vThreadPool
	private static final Executor vThreadPool = Executors.newVirtualThreadPerTaskExecutor();

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

        // Custom health check endpoint
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/ping", new PingHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
	}
	
	static class PingHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
        	 exchange.sendResponseHeaders(200, -1);
        }
    }

}
