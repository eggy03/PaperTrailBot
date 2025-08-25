package org.papertrail.sdk.client;

import kong.unirest.core.HttpMethod;
import org.papertrail.sdk.http.HttpServiceEngine;
import org.papertrail.sdk.http.HttpServiceResponse;
import org.papertrail.sdk.model.ErrorResponse;
import org.papertrail.sdk.model.MessageContentResponse;
import org.papertrail.utilities.EnvConfig;

import java.util.Map;

public class MessageContentLogClient {

    private MessageContentLogClient(){
        throw  new IllegalStateException("Utility Class");
    }

    private static final String BASE_URL = EnvConfig.get("API_URL")+"api/v1/content/message";
    private static final Map<String, String > CONTENT_HEADER = Map.of("Content-Type", "application/json");

    public static HttpServiceResponse<MessageContentResponse, ErrorResponse> logMessage(String messageId, String messageContent, String authorId){

        return HttpServiceEngine.makeRequest(
                HttpMethod.POST,
                BASE_URL,
                CONTENT_HEADER,
                new MessageContentResponse(messageId, messageContent, authorId),
                MessageContentResponse.class,
                ErrorResponse.class
        );

    }

    public static HttpServiceResponse<MessageContentResponse, ErrorResponse> retrieveMessage(String messageId){

        return HttpServiceEngine.makeRequest(
                HttpMethod.POST,
                BASE_URL+"/"+messageId,
                CONTENT_HEADER,
                null,
                MessageContentResponse.class,
                ErrorResponse.class
        );
    }

    public static HttpServiceResponse<MessageContentResponse, ErrorResponse> updateMessage(String messageId, String messageContent, String authorId){

        return HttpServiceEngine.makeRequest(
                HttpMethod.PUT,
                BASE_URL,
                CONTENT_HEADER,
                new MessageContentResponse(messageId, messageContent, authorId),
                MessageContentResponse.class,
                ErrorResponse.class
        );
    }

    public static HttpServiceResponse<Void, ErrorResponse> deleteMessage(String messageId) {

        return HttpServiceEngine.makeRequest(
                HttpMethod.POST,
                BASE_URL,
                CONTENT_HEADER,
                null,
                Void.class,
                ErrorResponse.class
        );

    }
}

