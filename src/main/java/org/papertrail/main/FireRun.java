package org.papertrail.main;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.papertrail.listeners.audit.event.AuditLogListener;
import org.papertrail.listeners.audit.event.GuildBoostEventListener;
import org.papertrail.listeners.audit.event.GuildMemberJoinAndLeaveListener;
import org.papertrail.listeners.audit.event.GuildPollListener;
import org.papertrail.listeners.audit.event.GuildVoiceListener;
import org.papertrail.listeners.audit.setup.AuditLogSetupCommandListener;
import org.papertrail.listeners.message.event.MessageLogListener;
import org.papertrail.listeners.message.setup.MessageLogSetupCommandListener;
import org.papertrail.listeners.misc.BotSetupInstructionCommandListener;
import org.papertrail.listeners.misc.SelfKickListener;
import org.papertrail.listeners.misc.ServerStatCommandListener;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.Security;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
/*
 * The main class of the bot
 */
public class FireRun {

	// All I/O blocking operations run inside the vThreadPool
	private static final Executor vThreadPool = Executors.newVirtualThreadPerTaskExecutor();

	// Register Bouncy Castle as a security provider
	// Required for the PBEWITHSHA256AND256BITAES-CBC-BC encryption algorithm
	private static void registerBouncyCastle() {	
		
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
			Logger.info("Bouncy Castle Security Provider Registered.");
		}
	}

	public static void main(String[] args) throws IOException {
		
		registerBouncyCastle();

		ConnectionInitializer ci = new ConnectionInitializer();
		ShardManager manager = ci.getManager();

		manager.addEventListener(new AuditLogSetupCommandListener());
		manager.addEventListener(new AuditLogListener(vThreadPool));

		manager.addEventListener(new MessageLogSetupCommandListener());
		manager.addEventListener(new MessageLogListener(vThreadPool));

		manager.addEventListener(new GuildVoiceListener(vThreadPool));
		manager.addEventListener(new GuildMemberJoinAndLeaveListener(vThreadPool));
		manager.addEventListener(new GuildPollListener(vThreadPool));
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
