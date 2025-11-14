package org.papertrail.commons.sdk.client;

import io.vavr.control.Either;
import org.jetbrains.annotations.NotNull;
import org.papertrail.commons.sdk.http.HttpServiceEngine;
import org.papertrail.commons.sdk.model.AuditLogObject;
import org.papertrail.commons.sdk.model.ErrorObject;
import org.papertrail.commons.utilities.EnvConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.List;

public class AuditLogClient {

    private static final String BASE_URL = EnvConfig.get("API_URL");
    private static final HttpHeaders httpHeaders = new HttpHeaders();
    private static final HttpServiceEngine engine = HttpServiceEngine.getInstance();

    static {
        httpHeaders.put("Content-Type", List.of("application/json"));
    }

    private AuditLogClient() {
        throw new IllegalStateException("Client class");
    }

    @NotNull
    public static Either<ErrorObject, AuditLogObject> registerGuild(String guildId, String channelId){

        return engine.makeRequestWithBody(
                HttpMethod.POST,
                BASE_URL +"api/v1/log/audit",
                httpHeaders,
                new AuditLogObject(guildId, channelId),
                AuditLogObject.class
        );
    }

    @NotNull
    public static Either<ErrorObject, AuditLogObject> getRegisteredGuild(String guildId){

        return engine.makeRequest(
                HttpMethod.GET,
                BASE_URL +"api/v1/log/audit/"+guildId,
                httpHeaders,
                AuditLogObject.class
        );
    }

    @NotNull
    public static Either<ErrorObject, AuditLogObject> updateRegisteredGuild(String guildId, String channelId){

        return engine.makeRequestWithBody(
                HttpMethod.PUT,
                BASE_URL +"api/v1/log/audit",
                httpHeaders,
                new AuditLogObject(guildId, channelId),
                AuditLogObject.class
        );
    }

    public static Either<ErrorObject, Void> deleteRegisteredGuild(String guildId){

        return engine.makeRequest(
                HttpMethod.DELETE,
                BASE_URL +"api/v1/log/audit/"+guildId,
                httpHeaders,
                Void.class
        );
    }
}
