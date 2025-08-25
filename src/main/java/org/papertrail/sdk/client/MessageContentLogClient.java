package org.papertrail.sdk.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.papertrail.sdk.model.result.ApiResult;
import org.papertrail.sdk.model.MessageContentResponse;
import org.papertrail.sdk.model.ErrorResponse;
import org.papertrail.utilities.EnvConfig;
import org.tinylog.Logger;

import java.time.LocalDateTime;
import java.util.Arrays;

public class MessageContentLogClient {

    private MessageContentLogClient(){
        throw  new IllegalStateException("Utility Class");
    }

    private static final String BASE_URL = EnvConfig.get("API_URL")+"api/v1/content/message";
    private static final String API_KEY_NAME = "apikey";
    private static final String API_KEY_VALUE = EnvConfig.get("API_KEY");

    public static ApiResult<MessageContentResponse, ErrorResponse> logMessage(String messageId, String messageContent, String authorId){

        HttpResponse<String> response = Unirest.post(BASE_URL)
                .basicAuth(API_KEY_NAME, API_KEY_VALUE)
                .header("Content-Type", "application/json")
                .body(new MessageContentResponse(messageId, messageContent, authorId))
                .asString();

        ObjectMapper mapper = new ObjectMapper();

        if(response.isSuccess()) {
            try {
                MessageContentResponse successResponse = mapper.readValue(response.getBody(), MessageContentResponse.class);
                return new ApiResult<>(successResponse, null);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResult<>(null, new ErrorResponse(-1, e.getClass().getSimpleName(), e.getMessage(), LocalDateTime.now().toString(), Arrays.toString(e.getStackTrace())));
            }
        } else {
            try {
                ErrorResponse errorResponse = mapper.readValue(response.getBody(), ErrorResponse.class);
                return new ApiResult<>(null, errorResponse);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResult<>(null, new ErrorResponse(-1, e.getClass().getSimpleName(), e.getMessage(), LocalDateTime.now().toString(), Arrays.toString(e.getStackTrace())));
            }
        }

    }

    public static ApiResult<MessageContentResponse, ErrorResponse> retrieveMessage(String messageId){

        HttpResponse<String> response = Unirest.get(BASE_URL+"/"+messageId)
                .basicAuth(API_KEY_NAME, API_KEY_VALUE)
                .asString();

        ObjectMapper mapper = new ObjectMapper();

        if(response.isSuccess()) {
            try {
                MessageContentResponse successResponse = mapper.readValue(response.getBody(), MessageContentResponse.class);
                return new ApiResult<>(successResponse, null);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResult<>(null, new ErrorResponse(-1, e.getClass().getSimpleName(), e.getMessage(), LocalDateTime.now().toString(), Arrays.toString(e.getStackTrace())));
            }
        } else {
            try {
                ErrorResponse errorResponse = mapper.readValue(response.getBody(), ErrorResponse.class);
                return new ApiResult<>(null, errorResponse);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResult<>(null, new ErrorResponse(-1, e.getClass().getSimpleName(), e.getMessage(), LocalDateTime.now().toString(), Arrays.toString(e.getStackTrace())));
            }
        }
    }

    public static ApiResult<MessageContentResponse, ErrorResponse> updateMessage(String messageId, String messageContent, String authorId){

        HttpResponse<String> response = Unirest.put(BASE_URL)
                .basicAuth(API_KEY_NAME, API_KEY_VALUE)
                .header("Content-Type", "application/json")
                .body(new MessageContentResponse(messageId, messageContent, authorId))
                .asString();

        ObjectMapper mapper = new ObjectMapper();

        if(response.isSuccess()) {
            try {
                MessageContentResponse successResponse = mapper.readValue(response.getBody(), MessageContentResponse.class);
                return new ApiResult<>(successResponse, null);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResult<>(null, new ErrorResponse(-1, e.getClass().getSimpleName(), e.getMessage(), LocalDateTime.now().toString(), Arrays.toString(e.getStackTrace())));
            }
        } else {
            try {
                ErrorResponse errorResponse = mapper.readValue(response.getBody(), ErrorResponse.class);
                return new ApiResult<>(null, errorResponse);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResult<>(null, new ErrorResponse(-1, e.getClass().getSimpleName(), e.getMessage(), LocalDateTime.now().toString(), Arrays.toString(e.getStackTrace())));
            }
        }
    }

    public static ApiResult<MessageContentResponse, ErrorResponse> deleteMessage(String messageId){

        HttpResponse<String> response = Unirest.delete(BASE_URL+"/"+messageId)
                .basicAuth(API_KEY_NAME, API_KEY_VALUE)
                .asString();

        ObjectMapper mapper = new ObjectMapper();

        if(response.isSuccess()) {
            return new ApiResult<>(new MessageContentResponse("0", "0", "0"), null);
        } else {
            try {
                ErrorResponse errorResponse = mapper.readValue(response.getBody(), ErrorResponse.class);
                return new ApiResult<>(null, errorResponse);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResult<>(null, new ErrorResponse(-1, e.getClass().getSimpleName(), e.getMessage(), LocalDateTime.now().toString(), Arrays.toString(e.getStackTrace())));
            }
        }
    }
}

