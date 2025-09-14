package org.papertrail.sdk.client;

import io.vavr.control.Either;
import org.jetbrains.annotations.NotNull;
import org.papertrail.sdk.http.HttpServiceEngine;
import org.papertrail.sdk.model.ErrorObject;
import org.papertrail.sdk.model.MessageContentObject;
import org.papertrail.utilities.EnvConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.List;

public class MessageContentLogClient {

    private static final String BASE_URL = EnvConfig.get("API_URL")+"api/v1/content/message";
    private static final HttpHeaders httpHeaders = new HttpHeaders();
    private static final HttpServiceEngine engine = HttpServiceEngine.getInstance();

    static {
        httpHeaders.put("Content-Type", List.of("application/json"));
    }

    private MessageContentLogClient() {
        throw new IllegalStateException("Client class");
    }

    @NotNull
    public static Either<ErrorObject, MessageContentObject> logMessage(String messageId, String messageContent, String authorId){

        return engine.makeRequestWithBody(
                HttpMethod.POST,
                BASE_URL,
                httpHeaders,
                new MessageContentObject(messageId, messageContent, authorId),
                MessageContentObject.class
        );

    }

    @NotNull
    public static Either<ErrorObject, MessageContentObject> retrieveMessage(String messageId){

        return engine.makeRequest(
                HttpMethod.GET,
                BASE_URL+"/"+messageId,
                httpHeaders,
                MessageContentObject.class
        );
    }

    @NotNull
    public static Either<ErrorObject, MessageContentObject> updateMessage(String messageId, String messageContent, String authorId){

        return engine.makeRequestWithBody(
                HttpMethod.PUT,
                BASE_URL,
                httpHeaders,
                new MessageContentObject(messageId, messageContent, authorId),
                MessageContentObject.class
        );
    }

    @NotNull
    public static Either<ErrorObject, Void> deleteMessage(String messageId) {

        return engine.makeRequest(
                HttpMethod.DELETE,
                BASE_URL+"/"+messageId,
                httpHeaders,
                Void.class
        );

    }
}

