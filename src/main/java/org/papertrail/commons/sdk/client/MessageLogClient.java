package org.papertrail.commons.sdk.client;

import io.vavr.control.Either;
import org.jetbrains.annotations.NotNull;
import org.papertrail.commons.sdk.http.HttpServiceEngine;
import org.papertrail.commons.sdk.model.ErrorObject;
import org.papertrail.commons.sdk.model.MessageLogObject;
import org.papertrail.commons.utilities.EnvConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.List;


public class MessageLogClient {

    private static final String BASE_URL = EnvConfig.get("API_URL");
    private static final HttpHeaders httpHeaders = new HttpHeaders();
    private static final HttpServiceEngine engine = HttpServiceEngine.getInstance();

    static {
        httpHeaders.put("Content-Type", List.of("application/json"));
    }

    private MessageLogClient(){
        throw new IllegalStateException("Client class");
    }

    @NotNull
    public static Either<ErrorObject, MessageLogObject> registerGuild(String guildId, String channelId){

        return engine.makeRequestWithBody(
                HttpMethod.POST,
                BASE_URL +"api/v1/log/message",
                httpHeaders,
                new MessageLogObject(guildId, channelId),
                MessageLogObject.class
        );
    }

    public static Either<ErrorObject, MessageLogObject> getRegisteredGuild(String guildId){

        return engine.makeRequest(
                HttpMethod.GET,
                BASE_URL +"api/v1/log/message/"+guildId,
                httpHeaders,
                MessageLogObject.class
        );

    }

    public static Either<ErrorObject, MessageLogObject> updateRegisteredGuild(String guildId, String channelId){

        return HttpServiceEngine.getInstance().makeRequestWithBody(
                HttpMethod.PUT,
                BASE_URL + "api/v1/log/message",
                httpHeaders,
                new MessageLogObject(guildId, channelId),
                MessageLogObject.class
        );
    }

    public static Either<ErrorObject, Void> deleteRegisteredGuild(String guildId){

        return engine.makeRequest(
                HttpMethod.DELETE,
                BASE_URL +"api/v1/log/message/"+guildId,
                httpHeaders,
                Void.class
        );
    }
}
