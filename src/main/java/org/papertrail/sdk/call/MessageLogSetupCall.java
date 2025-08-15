package org.papertrail.sdk.call;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.papertrail.sdk.response.ApiResponse;
import org.papertrail.sdk.response.ErrorResponseObject;
import org.papertrail.sdk.response.AuditLogResponseObject;
import org.papertrail.sdk.response.MessageLogResponseObject;
import org.papertrail.utilities.EnvConfig;
import org.tinylog.Logger;

import java.time.LocalDateTime;

public class MessageLogSetupCall {

    private MessageLogSetupCall(){
        throw  new IllegalStateException("Utility Class");
    }

    private static final String BASE_URL = EnvConfig.get("API_URL");

    public static ApiResponse<MessageLogResponseObject, ErrorResponseObject> registerGuild(String guildId, String channelId){

        HttpResponse<String> response = Unirest.post(BASE_URL +"api/v1/log/message")
                .header("Content-Type", "application/json")
                .body(new AuditLogResponseObject(guildId, channelId))
                .asString();

        ObjectMapper mapper = new ObjectMapper();

        if(response.isSuccess()) {
            try {
                MessageLogResponseObject successResponse = mapper.readValue(response.getBody(), MessageLogResponseObject.class);
                return new ApiResponse<>(successResponse, null);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResponse<>(null, new ErrorResponseObject(-1, e.getMessage(), LocalDateTime.now().toString()));
            }
        } else {
            try {
                ErrorResponseObject errorResponseObject = mapper.readValue(response.getBody(), ErrorResponseObject.class);
                return new ApiResponse<>(null, errorResponseObject);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResponse<>(null, new ErrorResponseObject(-1, e.getMessage(), LocalDateTime.now().toString()));
            }
        }

    }

    public static ApiResponse<MessageLogResponseObject, ErrorResponseObject> getRegisteredGuild(String guildId){

        HttpResponse<String> response = Unirest.get(BASE_URL +"api/v1/log/message/"+guildId)
                .asString();

        ObjectMapper mapper = new ObjectMapper();

        if(response.isSuccess()) {
            try {
                MessageLogResponseObject successResponse = mapper.readValue(response.getBody(), MessageLogResponseObject.class);
                return new ApiResponse<>(successResponse, null);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResponse<>(null, new ErrorResponseObject(-1, e.getMessage(), LocalDateTime.now().toString()));
            }
        } else {
            try {
                ErrorResponseObject errorResponseObject = mapper.readValue(response.getBody(), ErrorResponseObject.class);
                return new ApiResponse<>(null, errorResponseObject);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResponse<>(null, new ErrorResponseObject(-1, e.getMessage(), LocalDateTime.now().toString()));
            }
        }
    }

    public static ApiResponse<MessageLogResponseObject, ErrorResponseObject> updateRegisteredGuild(String guildId, String channelId){

        HttpResponse<String> response = Unirest.put(BASE_URL +"api/v1/log/message")
                .header("Content-Type", "application/json")
                .body(new AuditLogResponseObject(guildId, channelId))
                .asString();

        ObjectMapper mapper = new ObjectMapper();

        if(response.isSuccess()) {
            try {
                MessageLogResponseObject successResponse = mapper.readValue(response.getBody(), MessageLogResponseObject.class);
                return new ApiResponse<>(successResponse, null);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResponse<>(null, new ErrorResponseObject(-1, e.getMessage(), LocalDateTime.now().toString()));
            }
        } else {
            try {
                ErrorResponseObject errorResponseObject = mapper.readValue(response.getBody(), ErrorResponseObject.class);
                return new ApiResponse<>(null, errorResponseObject);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResponse<>(null, new ErrorResponseObject(-1, e.getMessage(), LocalDateTime.now().toString()));
            }
        }
    }

    public static ApiResponse<MessageLogResponseObject, ErrorResponseObject> deleteRegisteredGuild(String guildId){

        HttpResponse<String> response = Unirest.delete(BASE_URL +"api/v1/log/message/"+guildId)
                .asString();

        ObjectMapper mapper = new ObjectMapper();

        if(response.isSuccess()) {
            return new ApiResponse<>(new MessageLogResponseObject("0", "0"), null);
        } else {
            try {
                ErrorResponseObject errorResponseObject = mapper.readValue(response.getBody(), ErrorResponseObject.class);
                return new ApiResponse<>(null, errorResponseObject);
            } catch (JsonProcessingException e) {
                Logger.error(e);
                return new ApiResponse<>(null, new ErrorResponseObject(-1, e.getMessage(), LocalDateTime.now().toString()));
            }
        }

    }
}
